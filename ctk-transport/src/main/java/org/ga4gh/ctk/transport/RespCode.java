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

    /**
     * Convert the HTTP status code to a {@link RespCode} value.
     * @param code the HTTP status code
     * @return the corresponding {@link RespCode}, or {@link #NOT_IMPLEMENTED} if not present
     */
    public static RespCode fromInt(int code) {
        for (RespCode rc : values()) {
            if (rc.code == code) {
                return rc;
            }
        }
        org.slf4j.LoggerFactory.getLogger(RespCode.class.getName())
                               .warn("Unexpected value lookup for RespCode: " + code);
        return NOT_IMPLEMENTED;
    }

    /**
     * Return true if the provided code can be mapped to a value in the {@link RespCode} enum.
     * @param code the HTTP response code
     * @return true if the provided code can be mapped to a value in the {@link RespCode} enum
     */
    public static boolean isKnownResponse(int code) {
        for (RespCode rc : values()) {
            if (rc.code == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the numeric code.
     * @return the numeric code
     */
    public int getCode() {
        return code;
    }
}
