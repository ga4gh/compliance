package org.ga4gh.ctk.transport.protobuf;

import com.google.common.base.CharMatcher;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.Common;
import org.apache.http.HttpStatus;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.WireTracker;

import static org.ga4gh.ctk.transport.RespCode.fromInt;
import static org.ga4gh.ctk.transport.TransportUtils.makeUrl;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class Base<T extends GeneratedMessage.Builder> {

    static org.slf4j.Logger log;

    /**
     * <p>Holds the message traffic sent/received during the entire test run.
     * Intended to support test quality/coverage reporting.</p>
     */
    private static Table<String, String, Integer> messages;

    static {
        log = getLogger(Base.class);
        messages = HashBasedTable.create();
    }

    /**
     * url root to system-under-test; e.g., "http://localhost:8000"
     */
    private final String urlRoot;

    private final String path;

    final WireTracker wireTracker;
    final T responseBuilder;

    public Base(String urlRoot, String path, T responseBuilder, WireTracker wireTracker) {
        this.wireTracker = wireTracker;
        // neither urlRoot nor path should have leading or trailing spaces.
        this.urlRoot = urlRoot.trim();

        // the path does not begin or end with a slash
        String tsPath = path.trim();
        this.path = CharMatcher.is('/').trimFrom(tsPath);

        log.debug("set urlRoot = " + this.urlRoot + " path = " + this.path + " merged = " +
                makeUrl(this.urlRoot, this.path));

        this.responseBuilder = responseBuilder;
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

    private String queryServer() throws GAWrapperException, UnirestException, InvalidProtocolBufferException {
        final String url = makeUrl(urlRoot, path);
        if (wireTracker != null) {
            wireTracker.theUrl = url;
        }
        try {
            HttpResponse<JsonNode> response = queryServer(url);
            if (wireTracker != null) {
                wireTracker.bodyReceived = response.getBody().toString();
                wireTracker.setResponseStatus(fromInt(response.getStatus()));
            }
            int httpStatus = response.getStatus();
            if (httpStatus == HttpStatus.SC_OK) {
                return response.getBody().toString();
            } else {
                String json = response.getBody().toString();
                Common.GAException.Builder exceptionBuilder = Common.GAException.newBuilder();
                try {
                    JsonFormat.parser().merge(json, exceptionBuilder);
                } catch (InvalidProtocolBufferException e) {
                    log.warn("Failure when processing GAException: BODY <" + json + ">: ", e.toString());
                    throw e;
                }
                log.warn("Throwing GAException for " + json + ", status " + httpStatus);
                throw new GAWrapperException(exceptionBuilder.build(), httpStatus);
            }
        } catch (UnirestException e) {
            log.warn("problem communicating with " + url, e.getMessage());
            throw e;
        }
    }

    public void performQuery() throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
        String jsonResponse = queryServer();
        JsonFormat.parser().merge(jsonResponse, responseBuilder);
    }

    protected abstract HttpResponse<JsonNode> queryServer(String url) throws UnirestException;
}
