package org.ga4gh.ctk.transport.protobuf;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.ga4gh.ctk.transport.WireTracker;

public class Post<T extends GeneratedMessage.Builder> extends Base<T> {

    private final String json;

    public Post(String urlRoot, String path, MessageOrBuilder request, T responseBuilder, WireTracker wireTracker) throws InvalidProtocolBufferException {
        super(urlRoot, path, responseBuilder,wireTracker);
        json = JsonFormat.printer().print(request);
    }

    protected HttpResponse<JsonNode> queryServer(String url) throws UnirestException {
        if (log.isDebugEnabled()) {
            log.debug("begin jsonPost to " + url + " of " + json);
        }
        if (wireTracker != null) {
            wireTracker.bodySent = json;
        }
        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .body(json)
                .asJson();
        if (log.isDebugEnabled()) {
            log.debug("exit jsonPost to " + url + " with status " + response.getStatusText());
        }
        return response;
    }
}
