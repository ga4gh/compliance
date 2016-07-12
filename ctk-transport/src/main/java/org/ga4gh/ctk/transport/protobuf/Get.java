package org.ga4gh.ctk.transport.protobuf;

import com.google.protobuf.GeneratedMessage;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.ga4gh.ctk.transport.WireTracker;

import java.util.Map;

public class Get<T extends GeneratedMessage.Builder> extends Base<T> {

    private final String id;

    private final Map<String, Object> queryParams;

    public Get(String urlRoot, String path, String id, Map<String, Object> queryParams, T responseBuilder, WireTracker wireTracker) {
        super(urlRoot, path, responseBuilder, wireTracker);
        this.id = id;
        this.queryParams = queryParams;
    }

    protected HttpResponse<JsonNode> queryServer(String url) throws UnirestException {
        if (log.isDebugEnabled()) {
            log.debug("begin jsonGet to " + url + " id = " + id);
        }
        HttpResponse<JsonNode> response = Unirest.get(url)
                .header("accept", "application/json")
                .routeParam("id", id)
                .queryString(queryParams)
                .asJson();
        if (log.isDebugEnabled()) {
            log.debug("exit jsonGet to " + url + " with status " + response.getStatusText());
        }
        return response;
    }
}
