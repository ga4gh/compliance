package org.ga4gh.ctk.transport;

import ga4gh.Common.GAException;

import java.io.IOException;

/**
 * A wrapper for {@link ga4gh.Common.GAException}, thrown instead of it.
 * It includes the HTTP response code the server sent.
 *
 * @author Herb Jellinek
 */
public class GAWrapperException extends IOException {

    /**
     * The {@link GAException} we're wrapping.
     */
    private final GAException cause;

    /**
     * The HTTP status code the server sent.
     */
    private final int httpStatusCode;

    /**
     * Create a new wrapper for the given {@link GAException}, with the provided HTTP status code.
     *
     * @param cause          the {@link GAException} we're wrapping
     * @param httpStatusCode the HTTP status code, which should never be 200 (== OK),
     *                       considering that this is an <i>exception</i>
     */
    public GAWrapperException(GAException cause, int httpStatusCode) {
        this.cause = cause;
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Return the HTTP status code.
     * @return the HTTP status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets the value of the 'errorCode' field.
     * @return The numerical error code
     */
    public Integer getErrorCode() {
        return cause.getErrorCode();
    }

    /**
     * Return the exception's message.
     * @return the exception's message
     */
    @Override
    public String getMessage() {
        return cause.getMessage();
    }
}
