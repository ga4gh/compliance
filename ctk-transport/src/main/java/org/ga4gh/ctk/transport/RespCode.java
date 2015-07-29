package org.ga4gh.ctk.transport;

/**
 * <p>Define subset of all HttpStatus codes for specific use in a GA4GH server.
 * Return of status codes not in this list will be flagged as a test failure.</p>
 * <p>Created by Wayne Stidolph on 6/8/2015.</p>
 */
public enum RespCode {
    /**
     * Request accepted and response provided.
     */
    OK(200),
    /**
     * Request accepted but doesn't match any data.
     */
    NOT_FOUND(404),
    /**
     * Client error, used to indicate violation of validation or semantic constraints.
     */
    BAD_REQUEST(400),
    /**
     * Request invokes facility not implemented.
     */
    NOT_IMPLEMENTED(501);

    private int code;

    RespCode(int value){
        this.code=value;
    }

    public static RespCode fromInt(int val){
        for(RespCode rc : values()){
            if(rc.code == val){
                return rc;
            }
        }
        org.slf4j.LoggerFactory.getLogger("org.ga4gh.ctk.transport.RespCode")
                .warn("Unexpected value lookup for RespCode: "+ val);
        return NOT_IMPLEMENTED;
    }

    public static boolean isKnownResponse(int val){
        for (RespCode rc : values())
            if(rc.code == val) return true;
        return false;
    }
}
