package org.ga4gh.ctk.transport.avrojson;

import com.google.common.base.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;
import org.apache.avro.*;
import org.apache.avro.io.*;
import org.apache.avro.specific.*;
import org.apache.http.*;
import org.apache.http.message.*;
import org.ga4gh.ctk.domain.*;
import org.ga4gh.ctk.services.*;
import org.ga4gh.ctk.transport.*;

import java.util.*;

import static org.ga4gh.ctk.transport.RespCode.*;
import static org.ga4gh.ctk.transport.TransportUtils.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>Provide Avro/Json communications layer specific to GA4GH and with extensive logging in
 * support of CTK use.</p>
 * <p>This class is parameterized on the Avro reQuest (Q) and resPonse (P) type it handles.
 * Each instance handles one interaction, issuing a request and returning the response.</p>
 * <p>This class:<ul>
 *     <li>invokes the serializer/deserializer,</li>
 *     <li>invokes the HTTP interaction,</li>
 *     <li>tracks the data sent/received (via a WireTracker), and</li>
 *     <li>captures the traffic summary by storing TrafflicLogMsg objects in the TrafficLogService</li>
 * </ul>
 * <p>The class accepts the request and response objects, the URL root and path strings, and
 * an (optional) WireTracker (which will collect the JSON as sent/received on the wire).</p>
 * <p>Usage: </p>
 * <pre>
 *  {@code
 *  String path = URLMAPPING.getSearchReads();
 *  GASearchReadsResponse response = new GASearchReadsResponse();
 *  AvroJson aj =
 *    new AvroJson<>(request, response, URLMAPPING.getUrlRoot(), path, wireTracker);
 *  response = (GASearchReadsResponse) aj.doPostResp();
 * }
 * </pre>
 * <p>Created by Wayne Stidolph on 5/22/2015.</p>
 */
public class AvroJson<Q extends SpecificRecordBase, P extends SpecificRecordBase> {
    private static org.slf4j.Logger log;
    private TrafficLogService trafficLogService = TrafficLogService.getService();

    static {
        log = getLogger(AvroJson.class);
    }

    private final DatumWriter<Q> dw;

    private final Q theAvroReq;

    /**
     * url root to system-under-test; e.g., "http://localhost:8000"
     */
    String urlRoot;

    /**
     * url root to live comparison server
     */
    String refRoot;

    /**
     * if true, duplicate request to refserver and compare results
     */
    boolean compareToRef = false;

    /**
     * Cause communications to be skipped and NO_COMM_RESP returned when false.
     */
    static public boolean shouldDoComms = true;

    private String path;

    private Schema reqSchema;

    private Schema respSchema;

    private String jsonStr;

    private HttpResponse<JsonNode> httpResp;

    private P theResp;

    private WireTracker wireTracker;

    /**
     * The NO_COMM_RESP is a dummy returned when HTTP communications is stubbed out,
     * due to test or to previous comms failures.
     */
    public static HttpResponse<JsonNode> NO_COMM_RESP;

    static {
        org.apache.http.HttpResponse
                dummyResponse = new org.apache.http.message.BasicHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_0,
                                    HttpStatus.SC_SERVICE_UNAVAILABLE,
                                    "No communication with server"));
        NO_COMM_RESP = new HttpResponse<>(dummyResponse, JsonNode.class);
    }

    /**
     * <p>Construct an AvroJson for a particular request/response interaction.</p>
     * <p>The req and resp types parameterize this generic interaction object.</p>
     *
     * @param req     an instance of the Avro *Request method object
     * @param resp    an instance of the Avro *Response method object
     * @param urlRoot the server URL base
     * @param path    the request target path
     */
    public AvroJson(Q req, P resp, String urlRoot, String path) {
        this.theAvroReq = req;
        this.theResp = resp;
        this.dw = new SpecificDatumWriter<>();
        this.wireTracker = null;

        setCleanRootUrl(urlRoot, path);
    }

    /**
     * <p>Construct an AvroJson for an interaction which requires no request object.</p>
     * @param resp    an instance of the Avro *Response method object
     * @param urlRoot the server base
     * @param path    the request target path
     */
    public AvroJson(P resp, String urlRoot, String path) {
        this.theAvroReq = null;
        this.theResp = resp;
        this.dw = new SpecificDatumWriter<>();
        this.wireTracker = null;

        // neither urlRoot nor path should have spaces,
        // the urlRoot should end with exactly one slash
        setCleanRootUrl(urlRoot, path);
    }

    /**
     * <p>Construct an AvroJson for an interaction which requires no request object.</p>
     * @param resp    an instance of the avro *Response method object
     * @param urlRoot String the server base (often includes a version number)
     * @param path    String the request target path as identified in the schemas
     * @param wt If supplied, captures the data going across the wire
     */
    public AvroJson(P resp, String urlRoot, String path, WireTracker wt) {
        this(resp, urlRoot, path);
        this.wireTracker = wt;
    }

    /**
     * Construct an AvroJson for a particular request/response interaction.
     * The req and resp types parameterize this interaction object.
     *
     * @param req      an instance of the avro *Request method object
     * @param resp     an instance of the avro *Response method object
     * @param urlRoot  String the server base (often includes a version number)
     * @param path     String the request target path as identified in the schemas
     * @param wireTracker If supplied, captures the data going across the wire
     */
    public AvroJson(Q req, P resp, String urlRoot, String path, WireTracker wireTracker) {
        this(req, resp, urlRoot, path);
        this.wireTracker = wireTracker;
    }

    /**
     * Clean the root URL and endpoint path and store them.
     *
     * @param urlRoot the server base URL
     * @param path the request/endpoint target path
     */
    private void setCleanRootUrl(String urlRoot, String path) {
        // neither urlRoot nor path should have leading or trailing spaces.
        this.urlRoot = urlRoot.trim();

        // the path does not begin or end with a slash
        String tsPath = path.trim();
        this.path = CharMatcher.is('/').trimFrom(tsPath);

        log.info("set urlRoot = " + this.urlRoot + " path = " + this.path + " merged = " +
                         makeUrl(this.urlRoot, this.path));
    }

    /**
     * Getter for the WireTracker (if present, triggers JSON collection).
     *
     * @return the {@code WireTracker}
     */
    public WireTracker getWireTracker() {
        return wireTracker;
    }

    /**
     * Setter for the WireTracker (if present, we collect the JSON).
     *
     * @param wireTracker holds wire info for this single transaction
     */
    public void setWireTracker(WireTracker wireTracker) {
        this.wireTracker = wireTracker;
    }

    /**
     * Perform POST (according the data stored in this object at construction).
     * <p>
     * If this object has a WireTracker then the return JSON (if any) is copied into that.
     * This method also tracks all message types sent and received, in the 'messages' Table.
     *
     * @return an instance of the response type (as set during object construction), can be null.
     */
    public P doPostResp() {
        reqSchema = theAvroReq.getSchema();

        //jsonBytes = JsonMaker.avroToJsonBytes(dw, reqSchema, theAvroReq);
        //jsonBytes = JsonMaker.JacksonToJsonBytes(theAvroReq);
        jsonStr = JsonMaker.GsonToJsonBytes(theAvroReq);

        httpResp = shouldDoComms ? jsonPost(makeUrl(urlRoot, path)): NO_COMM_RESP;

        updateTheRespAndLogMessages("POST");

        return theResp;
    }

    private void updateTheRespAndLogMessages(String postOrGet){
        // httpResp can be null (e.g., a timeout)
        if (httpResp != null && httpResp.getStatus() == HttpStatus.SC_OK) {
            String json = httpResp.getBody().toString();

            theResp = new AvroMaker<>(theResp).makeAvroFromJson(json, makeUrl(urlRoot, path));
        } else {
            theResp = null;
        }
        // track all message types sent/received for simple "test coverage" indication
        String respName = theResp != null ? theResp.getClass().getCanonicalName()  : "null";
        String reqName = theAvroReq != null? theAvroReq.getClass().getCanonicalName() : "null";
        TrafficLog tlm = trafficLogService.getTrafficLogBuilder()
                .setClassSent(reqName)
                .setActionType(postOrGet)
                .setJsonReq(jsonStr)
                .setClassReceived(respName)
                .setResponseStatus(httpResp != null ? httpResp.getStatus() : 0)
                .setEndpoint(path)
                .setRunKey(Long.parseLong(System.getProperty("ctk.runkey","-1")))
                .setTestMethodKey(Integer.parseInt(System.getProperty("ctk.testmethodkey", "-1")))
                .build();
        tlm.save();
    }

    /**
     * Perform GET (according the data stored in this object at construction).
     * <p>If this object has a WireTracker then the return JSON (if any) is copied into that.
     * This method also tracks all message types sent and received, in the 'messages' Table.</p>
     *
     * @param id string to be used as route param to the URL
     *
     * @return an instance of the response type (as set during object construction), can be null.
     */
    public P doGetResp(String id) {
        return doGetResp(id, null);
    }

    /**
     * Perform GET (according the data stored in this object at construction).
     * <p>If this object has a WireTracker then the return JSON (if any) is copied into that.
     * This method also tracks all message types sent and received, in the 'messages' Table.</p>
     *
     * @param id string to be used as route param to the URL
     * @param queryParams optional query parameters to add to the GET request.  May be null.
     *
     * @return an instance of the response type (as set during object construction), can be null.
     */
    public P doGetResp(String id, Map<String, Object> queryParams) {

        // no request object to build, just GET from the endpoint with route param
        httpResp = shouldDoComms ? jsonGet(makeUrl(urlRoot, path), id, queryParams) : NO_COMM_RESP;

        updateTheRespAndLogMessages("GET");

        return theResp;
    }

    /**
     * Do actual post with logging/tracking
     *
     * @param theURL the the uRL
     * @return the HTTP response (can be null, if Unirest throws exception)
     */
    HttpResponse<JsonNode> jsonPost(String theURL) {
        if (log.isDebugEnabled()) {
            log.debug("begin jsonPost to " + theURL + " of " + jsonStr);
        }
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(theURL)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .body(jsonStr)
                    .asJson();
        } catch (UnirestException e) {
            log.warn("stubbing future comms due to problem communicating JSON with " + theURL, e.getMessage());
            log.debug(e.toString());
            shouldDoComms = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("exit jsonPost to " + theURL + " with status "
                    + jsonResponse != null ? jsonResponse.getStatusText() : "FAILED");
        }
        if (wireTracker != null) {
            wireTracker.theUrl = theURL;
            wireTracker.bodySent = jsonStr;
            wireTracker.bodyReceived = (jsonResponse != null? jsonResponse.getBody().toString(): null);
            wireTracker.setResponseStatus(fromInt(jsonResponse != null ? jsonResponse.getStatus() : 0));
        }
        return jsonResponse;
    }

    HttpResponse<JsonNode> jsonGet(String theUrl, String id, Map<String, Object> queryParams) {
        if (log.isDebugEnabled()) {
            log.debug("begin jsonGet to " + theUrl + " id = " + id);
        }
        HttpResponse<JsonNode> jsonResponse = null;

        try {
            jsonResponse = Unirest.get(theUrl)
                    .header("accept", "application/json")
                    .routeParam("id", id)
                    .queryString(queryParams)
                    .asJson();
        } catch (UnirestException e) {
            log.warn("stubbing future comms due to problem communicating JSON with " + theUrl + " id: " + id, e);
            shouldDoComms = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("exit jsonGet to " + theUrl + " id = " + id + " with status "
                    + jsonResponse != null ? jsonResponse.getStatusText() : "FAILED");
        }
        if (wireTracker != null) {
            // value below is for tracing/display only; it's not meant to be a valid URL
            wireTracker.theUrl = theUrl + " / " + id;
            wireTracker.bodySent = "";
            wireTracker.bodyReceived = (jsonResponse != null? jsonResponse.getBody().toString(): null);
            wireTracker.setResponseStatus(fromInt(jsonResponse != null ? jsonResponse.getStatus() : 0));
        }
        return jsonResponse;
    }

    public String toString(){
        String reqName = theAvroReq == null ? "null" : theAvroReq.getClass().getSimpleName();
        String respName = theResp == null? "null" : theResp.getClass().getSimpleName();
        return makeUrl(urlRoot, path) + " " + reqName + " " + respName;
    }
}
