package org.ga4gh.ctk.domain;

public class TrafficLogBuilder {
    private String classSent;
    private String actionType;
    private String jsonSent;
    private String endpoint;
    private String classReceived;
    private String idParam;
    private int responseStatus;
    private long runKey;
    private int testMethodKey;

    public TrafficLogBuilder setClassSent(String classSent) {
        this.classSent = classSent;
        return this;
    }

    public TrafficLogBuilder setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public TrafficLogBuilder setJsonSent(String jsonSent) {
        this.jsonSent = jsonSent;
        return this;
    }

    public TrafficLogBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public TrafficLogBuilder setClassReceived(String classReceived) {
        this.classReceived = classReceived;
        return this;
    }

    public TrafficLogBuilder setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    public TrafficLogBuilder setRunKey(long runKey){
        this.runKey = runKey;
        return this;
    }

    public TrafficLogBuilder setTestMEthodKey(int testMethodKey){
        this.testMethodKey = testMethodKey;
        return this;
    }

    public TrafficLogBuilder setIdParam(String idParam){
        this.idParam = idParam;
        return this;
    }

    public TrafficLog build() {
        return new TrafficLog(classSent, actionType, jsonSent, classReceived, endpoint, idParam, responseStatus, runKey, testMethodKey);
    }

}