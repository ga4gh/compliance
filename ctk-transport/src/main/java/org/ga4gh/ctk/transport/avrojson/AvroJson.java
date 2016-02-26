package org.ga4gh.ctk.transport.avrojson;

import com.google.common.base.CharMatcher;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.http.HttpStatus;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.methods.GAException;

import java.util.Map;

import static org.ga4gh.ctk.transport.RespCode.fromInt;
import static org.ga4gh.ctk.transport.TransportUtils.makeUrl;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Provide Avro/Json communications layer specific to GA4GH and with extensive logging in
 * support of CTK use.</p>
 * <p>This class is parameterized on the Avro reQuest (Q) and resPonse (P) type it handles.
 * Each instance handles one interaction, issuing a request and returning the response.</p>
 * <p>This class:<ul>
 *     <li>invokes the serializer/deserializer,</li>
 *     <li>invokes the HTTP interaction,</li>
 *     <li>tracks the data sent/received (via a WireTracker), and</li>
 *     <li>captures the traffic summary in a static table named 'messages'</li>
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

    /**
     * <p>Holds the message traffic sent/received by AvroJson during the entire test run.
     * Intended to support test quality/coverage reporting.</p>
     */
    private static Table<String, String, Integer> messages;

    static {
        log = getLogger(AvroJson.class);
        messages = HashBasedTable.create();
    }

    private final DatumWriter<Q> dw;

    private final Q theAvroReq;

    /**
     * url root to system-under-test; e.g., "http://localhost:8000"
     */
    private String urlRoot;

    private String path;

    private Schema reqSchema;

    private Schema respSchema;

    private String jsonStr;

    private HttpResponse<JsonNode> httpResp;

    private P theResp;

    private WireTracker wireTracker;

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

        log.debug("set urlRoot = " + this.urlRoot + " path = " + this.path + " merged = " +
                          makeUrl(this.urlRoot, this.path));
    }

    /**
     * <p>Access the message-traffic recording Table.</p>
     * <p>Each target endpoint/parameter string becomes a key to a row in the table,
     * and the row cells are:</p>
     * <p>| request (class, post/get, body/id) | response class (msg type) | HTTP status code |</p>
     */
    public static Table<String, String, Integer> getMessages() {
        return messages;
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
    public P doPostResp() throws GAException {
        reqSchema = theAvroReq.getSchema();

        //jsonBytes = JsonMaker.avroToJsonBytes(dw, reqSchema, theAvroReq);
        //jsonBytes = JsonMaker.JacksonToJsonBytes(theAvroReq);
        jsonStr = JsonMaker.GsonToJsonBytes(theAvroReq);

        httpResp = jsonPost(makeUrl(urlRoot, path));

        updateTheRespAndLogMessages("POST");

        return theResp;
    }

    /**
     * Create and return a custom {@link Gson} object that knows how to deal with the innards of
     * {@link GAException}, which has a field with a weird generated name (<tt>"message$"</tt>).
     *
     * @return a custom {@link Gson} object for dealing with {@link GAException}
     */
    private Gson makeGson() {
        final GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingStrategy(field -> {
            final String originalName = field.getName();
            if (originalName.equals("message$")) {
                return "message";
            }
            return originalName;
        });
        return builder.create();
    }

    private void updateTheRespAndLogMessages(String postOrGet) throws GAException {
        // httpResp can be null (e.g., a timeout)
        if (httpResp != null) {
            final int httpStatus = httpResp.getStatus();
            if (httpStatus != HttpStatus.SC_OK) {
                final String json = httpResp.getBody().toString();
                final Gson gson = makeGson();
                try {
                    final GAException cause = gson.fromJson(json, GAException.class);
                    log.info("Throwing GAException for " + json + ", status " + httpStatus);
                    throw new GAWrapperException(cause, httpStatus);
                } catch (JsonSyntaxException e) {
                    log.warn("Parse failure on GAException: BODY < " + json + " > " + e.toString());
                }
            } else {
                final String json = httpResp.getBody().toString();
                theResp = new AvroMaker<>(theResp).makeAvroFromJson(json, makeUrl(urlRoot, path));
            }
        } else {
            theResp = null;
        }
        // track all message types sent/received for simple "test coverage" indication
        String respName = theResp != null ? theResp.getClass().getSimpleName()  : "null";
        if (theAvroReq == null) {
            // it's a GET request, so no request object
            messages.put(postOrGet + " <" + jsonStr + ">", respName,
                         httpResp != null ? httpResp.getStatus() : 0);
        } else {
            messages.put(theAvroReq.getClass()
                                   .getSimpleName() + postOrGet + " <" + jsonStr + ">", respName,
                         httpResp != null ? httpResp.getStatus() : 0);
        }
    }

    /**
     * Perform GET (according the data stored in this object at construction).
     * <p>If this object has a WireTracker then the return JSON (if any) is copied into that.
     * This method also tracks all message types sent and received, in the 'messages' Table.</p>
     *
     * @param id string to be used as route param to the URL
     *
     * @return an instance of the response type (as set during object construction), can be null
     *
     * @throws GAException if the server throws one in response to this request
     */
    public P doGetResp(String id) throws GAException {
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
     * @return an instance of the response type (as set during object construction), can be null
     *
     * @throws GAException if the server throws one in response to this request
     */
    public P doGetResp(String id, Map<String, Object> queryParams) throws GAException {

        // no request object to build, just GET from the endpoint with route param
        httpResp = jsonGet(makeUrl(urlRoot, path), id, queryParams);

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
            log.warn("problem communicating with " + theURL, e.getMessage());
            log.debug(e.toString());
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
            log.warn("problem communicating with " + theUrl + " id: " + id, e);
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
