package org.ga4gh.ctk.domain;

import com.google.common.base.*;
import org.ga4gh.ctk.services.*;

import javax.persistence.*;

/**
 * <p>DAO for moving transport events to/from persistence</p>
 * Created by Wayne Stidolph on 7/24/2015.
 */
@Entity
public class TrafficLog {

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
     * Set its visibility to private in order to discourage using it directly.
     */
    private TrafficLog() {
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

    @Override
    public String toString() {
        return String.join(" ", classSent, actionType, "<"+jsonReq+">",
                            ""+responseStatus, classReceived, endpoint,
                            idParam, ""+runKey, ""+testMethodKey);
    }

    public String getClassSent() {
        return classSent;
    }

    public String getActionType() {
        return actionType;
    }

    public String getJsonReq() {
        return jsonReq;
    }

    public String getClassReceived() {
        return classReceived;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return this.endpoint;
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

    public long getRunKey() {
        return runKey;
    }

    public int getTestMethodKey() { return testMethodKey;}

    private static TrafficLogService trafficLogService;

    public static void setTrafficLogService(TrafficLogService trafficLogService) {
        TrafficLog.trafficLogService = trafficLogService;
    }

    public void save() {
        if (trafficLogService == null)
            trafficLogService = trafficLogService.getService();
        trafficLogService.save(this);
    }

}
