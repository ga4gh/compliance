package org.ga4gh.ctk.domain;

import com.google.common.base.*;
import org.ga4gh.ctk.services.*;

import javax.persistence.*;

/**
 * <p>DAO for moving transport events to/from persistence</p>
 * <ul>
 *     <li>id: entity identifier</li>
 *     <li>runkey: group logs from same "CTS run"</li>
 *     <li>testMethodKey: group interactions from same test method execution, for multi-interaction tests</li>
 *     <li>classSent: the IDL class sent from CTK to target (the request)</li>
 *     <li>actionType: HTTP verb used</li>
 *     <li>jsonReq: the JSON stringbody sent to the target server</li>
 *     <li>classReceived: the IDL-generated class into which the response was deserialized</li>
 *     <li>the endpoint on the target server (after the URL root)</li>>
 * </ul>
 * Created by Wayne Stidolph on 7/24/2015.
 */
@Entity
public class TrafficLog {

    private static TrafficLogService trafficLogService;
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Long runKey;
    @Column
    private int testMethodKey;
    @Column
    private String classSent;
    @Column
    private String actionType;
    @Column
    private String jsonReq; // as sent to server
    @Column
    private String classReceived;
    @Column
    private String endpoint;
    @Column(nullable = true)
    private String idParam;
    @Column
    private int responseStatus;

    /**
     * Provide an empty, default constructor for conventional JPA requirements.
     * And for easier test setup. Mark as Deprecated so people don't just use it
     * (but leave it public so the unit tests can make a quick dumb TrafficLog)
     */
    @Deprecated
    public TrafficLog() {
    }

    public TrafficLog(String classSent,
                      String actionType, // HTTP verb
                      String jsonReq,
                      String classReceived,
                      String endpoint,
                      String idParam,
                      int responseStatus,
                      long runKey,
                      int testMethodKey) {
        this();
        this.classSent = classSent;
        this.actionType = actionType;
        this.jsonReq = jsonReq;
        this.classReceived = classReceived;
        this.responseStatus = responseStatus;
        this.endpoint = endpoint;
        this.idParam = idParam;
        this.runKey = runKey;
        this.testMethodKey = testMethodKey;
    }

    /**
     * Instantiates a new Traffic log from a TrafficLog toString() output.
     *
     * @param fromString the from string
     */
    public static TrafficLog createTrafficLog(String fromString) {
        String[] pieces = fromString.split(" ");
        return new TrafficLog(
                pieces[0],
                pieces[1],
                CharMatcher.anyOf("<>").trimFrom(pieces[2]),
                pieces[4],
                pieces[5],
                pieces[6],
                Integer.parseInt(pieces[3]),
                Long.parseLong(pieces[7]),
                Integer.parseInt(pieces[8]));
    }

    public static TrafficLogService getTrafficLogService() {
        return trafficLogService;
    }

    public static void setTrafficLogService(TrafficLogService trafficLogService) {
        TrafficLog.trafficLogService = trafficLogService;
    }

    @Override
    public String toString() {
        return String.join(" ", classSent, actionType, "<" + jsonReq + ">",
                "" + responseStatus, classReceived, endpoint,
                idParam, "" + runKey, "" + testMethodKey);
    }

    public String getClassSent() {
        return classSent;
    }

    public void setClassSent(String classSent) {
        this.classSent = classSent;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getJsonReq() {
        return jsonReq;
    }

    public void setJsonReq(String jsonReq) {
        this.jsonReq = jsonReq;
    }

    public String getClassReceived() {
        return classReceived;
    }

    public void setClassReceived(String classReceived) {
        this.classReceived = classReceived;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIdParam() {
        return idParam;
    }

    public void setIdParam(String idParam) {
        this.idParam = idParam;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public long getRunKey() {
        return runKey;
    }

    public void setRunKey(Long runKey) {
        this.runKey = runKey;
    }

    public void setRunKey(long runKey) {
        this.runKey = runKey;
    }

    public int getTestMethodKey() {
        return testMethodKey;
    }

    public void setTestMethodKey(int testMethodKey) {
        this.testMethodKey = testMethodKey;
    }

    public void save() {
        if (trafficLogService == null)
            trafficLogService = trafficLogService.getService();
        trafficLogService.save(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
