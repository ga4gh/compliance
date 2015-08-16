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

    @Ignore
    @Test
    public void testSetTrafficLogRepository() throws Exception {

    }

    @Ignore
    @Test
    public void testSave() throws Exception {

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
    public void testLogTraffic() throws Exception {

    }

    @Test
    public void testCreateTestRunKey() throws Exception {

    }

    @Test
    public void testIsKnownRunkey() throws Exception {
        TrafficLog tl_133 = new TrafficLog();
        tl_133.setRunKey(133L);
        svc.save(tl_133);

        TrafficLog tl_134 = new TrafficLog();
        tl_134.setRunKey(134L);
        svc.save(tl_134);

        assertThat(svc.isKnownRunkey(133)).isTrue();
        assertThat(svc.isKnownRunkey(134)).isTrue();
        assertThat(svc.isKnownRunkey(0)).isFalse();
        assertThat(svc.isKnownRunkey(1)).isFalse();
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