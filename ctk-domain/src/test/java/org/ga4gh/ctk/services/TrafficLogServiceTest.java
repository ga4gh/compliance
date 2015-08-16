package org.ga4gh.ctk.services;
import org.ga4gh.ctk.domain.*;
import org.junit.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;



/**
 * Created by Wayne Stidolph on 8/15/2015.
 */
public class TrafficLogServiceTest {

    TrafficLogService svc;

    @Before
    public void setUpService() {
        svc = TrafficLogService.getService();
        svc.clearStaticTrafficLog();
    }

    @Test
    public void testGetServiceReturnsOneInstance() throws Exception {
        TrafficLogService tls1 = TrafficLogService.getService();
        TrafficLogService tls2 = TrafficLogService.getService();
        assertThat(tls1).isNotNull().isSameAs(tls2);
    }

    @Ignore()
    @Test
    public void testSetTrafficLogRepository() throws Exception {

    }

    @Ignore
    @Test
    public void testSavePassesToRepoIfPresent() throws Exception {

    }

    @Ignore
    @Test
    public void testSaveStoresLocalIfNoRepo() throws Exception {
        TrafficLogBuilder builder = svc.getTrafficLogBuilder();

        TrafficLog tl = builder.setRunKey(1).setTestMethodKey(1).setClassSent("Req1")
                .setActionType("POST").setJsonReq("").setClassReceived("Resp1")
                .setEndpoint("an/endpoint/1").setResponseStatus(200)
                .build();

        svc.setTrafficLogRepository(null);
        svc.clearStaticTrafficLog(); // clear all
        assertThat(svc.getUsedEndpoints(1L)).hasSize(0);
        svc.save(tl);

        assertThat(svc.getUsedEndpoints(1L)).hasSize(1);
        svc.clearStaticTrafficLog(); // clear all
        assertThat(svc.getUsedEndpoints(1L)).hasSize(0);
    }

    @Ignore
    @Test
    public void testSaveLocalHandlesUnknownRunkey() throws Exception {
        long knownRunKey = svc.createTestRunKey();
        long unknownRunKey = knownRunKey + 1;

        TrafficLogBuilder builder = svc.getTrafficLogBuilder();

        TrafficLog tl = builder.setRunKey(unknownRunKey).setTestMethodKey(1).setClassSent("Req1")
                .setActionType("POST").setJsonReq("").setClassReceived("Resp1")
                .setEndpoint("an/endpoint/1").setResponseStatus(200)
                .build();

        svc.setTrafficLogRepository(null);
        svc.clearStaticTrafficLog(); // clear all
        svc.save(tl); // with unknown key

        assertThat(svc.getUsedEndpoints(unknownRunKey)).hasSize(1);
        assertThat(svc.getUsedEndpoints(knownRunKey)).hasSize(0);
    }

    @Test
    public void testCollectDistinctLoggedValues() throws Exception {

    }

    @Test
    public void testGetUsedEndpoints() throws Exception {
        assertThat(svc.getUsedEndpoints(13)).isEmpty();
        TrafficLog tl = new TrafficLog();
        tl.setRunKey(13);
        tl.setEndpoint("foo");
        svc.save(tl);
        tl = new TrafficLog();
        tl.setRunKey(13);
        tl.setEndpoint("bar");
        svc.save(tl);
        tl=null;
        List<String> ue = svc.getUsedEndpoints(13);
        assertThat(ue).containsOnlyElementsOf(Arrays.asList("foo", "bar"));
    }

    @Test
    public void testRunsKeptSeperate() throws Exception {
        assertThat(svc.getUsedEndpoints(13)).isEmpty();
        TrafficLog tl_13 = new TrafficLog();
        tl_13.setRunKey(13);
        tl_13.setEndpoint("foo");
        svc.save(tl_13);

        assertThat(svc.getUsedEndpoints(14)).isEmpty();
        TrafficLog tl_14 = new TrafficLog();
        tl_14.setRunKey(14);
        tl_14.setEndpoint("bar");
        svc.save(tl_14);

        assertThat(svc.getUsedEndpoints(13)).doesNotContainAnyElementsOf(svc.getUsedEndpoints(14));
    }

    @Test
    public void testGetUsedRequests() throws Exception {
        assertThat(svc.getUsedRequests(13)).isEmpty();
        TrafficLog tl_13 = new TrafficLog();
        tl_13.setRunKey(13);
        tl_13.setClassSent("foo");
        svc.save(tl_13);

        assertThat(svc.getUsedRequests(13)).containsOnlyElementsOf(Arrays.asList("foo"));
    }

    @Test
    public void testGetUsedResponses() throws Exception {
        assertThat(svc.getUsedRequests(13)).isEmpty();
        TrafficLog tl_13 = new TrafficLog();
        tl_13.setRunKey(13);
        tl_13.setClassReceived("blob");
        svc.save(tl_13);

        assertThat(svc.getUsedResponses(13)).containsOnlyElementsOf(Arrays.asList("blob"));
    }

    @Test
    public void testGetTrafficLogs() throws Exception {
        TrafficLogBuilder builder = svc.getTrafficLogBuilder();

        builder.setRunKey(1).setTestMethodKey(1).setClassSent("Req1")
                .setActionType("POST").setJsonReq("").setClassReceived("Resp1")
                .setEndpoint("an/endpoint/1").setResponseStatus(200);
        builder.build().save();

        builder.setClassSent("Req2").setClassReceived("Resp2").setEndpoint("an/endpoint/2");
        builder.build().save();

        builder.setClassSent("Req3").setClassReceived("Resp3").setEndpoint("an/endpoint/3");
        builder.build().save();

        builder.setRunKey(2L);
        builder.setClassSent("Req1").setClassReceived("Resp1").setEndpoint("an/endpoint/3");
        builder.build().save();

        builder.setRunKey(2L);
        builder.setClassSent("Req4").setClassReceived("Resp4").setEndpoint("an/endpoint/4");
        builder.build().save();

        List<TrafficLog> mr1 = svc.getTrafficLogs(1L);
        List<TrafficLog> mr2 = svc.getTrafficLogs(2L);

        assertThat(mr1).hasSize(3);
        assertThat(mr1).extracting("classReceived").containsOnly("Resp1","Resp2","Resp3");
        assertThat(mr2).hasSize(2);
    }

    @Test
    public void testCreateTestRunKey() throws Exception {
        long rk = svc.createTestRunKey();
        assertThat(svc.isKnownRunkey(rk)).isTrue();
    }

    @Ignore
    @Test public void testCreatedRunKeyIsUnique() throws Exception {

    }

    @Test
    public void testIsKnownRunkey() throws Exception {
        svc.setTrafficLogRepository(null);
        svc.clearStaticTrafficLog();
        long rk = svc.createTestRunKey();
        assertThat(svc.isKnownRunkey(rk)).isTrue();
        assertThat(svc.isKnownRunkey(-1 * rk)).isFalse();
    }

    @Test
    public void testClearStaticTrafficLog() throws Exception {
        TrafficLog tl_133 = new TrafficLog();
        tl_133.setRunKey(133L);
        svc.save(tl_133);

        TrafficLog tl_134 = new TrafficLog();
        tl_134.setRunKey(134L);
        svc.save(tl_134);

        assertThat(svc.isKnownRunkey(133L)).isTrue();
        svc.clearStaticTrafficLog(133L);
        assertThat(svc.isKnownRunkey(133L)).isFalse();
        assertThat(svc.isKnownRunkey(134L)).isTrue();

        svc.clearStaticTrafficLog();
        assertThat(svc.isKnownRunkey(133L)).isFalse();
        assertThat(svc.isKnownRunkey(134L)).isFalse();
    }
}