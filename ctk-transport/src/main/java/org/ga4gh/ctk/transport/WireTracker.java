package org.ga4gh.ctk.transport;

import com.google.gson.Gson;
import org.ga4gh.GAException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Tracking and measurement of the on-the-wire transaction.</p>
 * <p>Used to signal need for wire-format difference measurement
 * of the Avro-defined objects used in a given interaction</p>
 * <p>Created by Wayne Stidolph on 5/27/2015.</p>
 */
public class WireTracker {
    final static Logger log = getLogger(WireTracker.class);

    /**
     * <p>The target url with which this WireTracker communicated.</p>
     */
    public String theUrl;
    /**
     * <p>The string BODY sent to the target.</p>
     */
    public String bodySent;
    /**
     * <p>The string BODY received from the target</p>
     */
    public String bodyReceived;
    private GAException gae;
    private String gaeMessage; // convenience and in case non-parseable
    private int gaeErrorCode; // convenience and in case non-parseable

    /**
     * <p>Returns true if and only if a GAE was received on this interaction,
     * AND it was parsable.</p>
     * <p>If it's non-parseable then the gaeMessage field
     * will hold the returned BODY (same as the bodyReceived field) and the
     * gaeErrorCode will be set to -1</p>
     *
     * @return boolean that an error body is parsed
     */
    public boolean gotParseableGAE(){
        return getGae() instanceof GAException;
    }

    private boolean parseableGae = false;

    RespCode responseStatus;

    public int getErrorCode() {
        return gaeErrorCode;
    }

    public String getMessage() {
        return gaeMessage;
    }

    public GAException getGae() {
        if (responseStatus != RespCode.OK) {
            // parse the received body
            Gson gson = new Gson();
            try {
                gae = gson.fromJson(bodyReceived, GAException.class);
                gaeMessage = gae.getMessage();
                gaeErrorCode = gae.getErrorCode();
            }catch (Exception e){
                log.warn("Parse failure on GAException: BODY < " + bodyReceived + " > " + e.toString());
                gaeErrorCode = -1;
                gaeMessage = bodyReceived;
                parseableGae = false;
                gae = null;
            }
        }
        return gae;
    }

    public RespCode getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(RespCode responseStatus) {
        this.responseStatus = responseStatus;
    }
}
