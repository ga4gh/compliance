package org.ga4gh.cts.api;

import org.ga4gh.ctk.*;
import org.ga4gh.ctk.services.*;
import org.ga4gh.ctk.transport.*;
import org.junit.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>Stupidly named 'zz...' so it runs last, this test class will evaluate
 * integration test completeness (are all messages, data type,and endpoints exercised)</p>
 * <p>The test depends on the DomainInformationService supplying a correct list of
 * domain data types, and the trafficLogService being fed TrafficLogMsgs for every
 * interacton with the server (which happens in the ctk-transport layer).</p>
 * <p>Created by Wayne Stidolph on 5/30/2015.</p>
 */
public class zzCheckCoverageIT {

    static URLMAPPING urls;
    static Logger log = LoggerFactory.getLogger(zzCheckCoverageIT.class);
    static Logger testlog = LoggerFactory.getLogger(CtkLogs.SYSTEST);
    static TrafficLogService trafficLogService;
    static DomainInformationService domainInformationService;

    @BeforeClass
    public static void setupServices() throws Exception {
        log.debug("setupServices to get the TrafficLogService, DomainInformationService, and 'urls'");
        trafficLogService = TrafficLogService.getService();
        domainInformationService = DomainInformationService.getService();
        urls = URLMAPPING.getInstance(); // reload defaults
    }

    @Test
    public void allIdlRequestsShouldBeSent() throws Exception {
        org.junit.Assert.assertFalse("DomainInformationService gave empty list of Request types",
                domainInformationService.getRequestTypes().isEmpty() );
        long runkey = getRunKey();
        List<String> usedRequests = trafficLogService.getUsedRequests(runkey);
        usedRequests.remove("null");  //could be from intentional error-request

        log.debug("allIdlRequestsShouldBeSent runkey " + runkey + " sees usedRequests list size is " + usedRequests.size());
        assertThat(usedRequests).containsOnlyElementsOf(domainInformationService.getRequestTypes());
    }

    @Test
    public void allIdlResponsesShouldBeReceived() throws Exception {
        List<String> expectedReturns = domainInformationService.getExpectedReturns();
        org.junit.Assert.assertFalse("DomainInformationService gave empty list of expected return types",
                expectedReturns.isEmpty() );
        long runkey = getRunKey();
        List<String> usedResponses = trafficLogService.getUsedResponses(runkey);
        testlog.debug("allIdlResponsesShouldBeReceived runkey " + runkey + " sees usedResponses list size is " + usedResponses.size());
        assertThat(usedResponses).containsOnlyElementsOf(expectedReturns);
    }

    @Ignore("Unimplemented")
    @Test
    public void allIdlDatatypesShouldBeUsed() throws Exception {
        // The DomainInformationService knows what data types are defined,
        // but we don't yet have any tracking on what data types are created
        // during the test run
    }

    @Test
    public void allEndpointsShouldBeUsed() throws Exception {
        testlog.warn("show access to testlog from tests");
        // if an endpoint is left defined, then some test should hit it.

        // First, find the 'active' endpoints the CTK knows about.
        List<String> knownEndpoints =
                domainInformationService.getActiveEndpoints(
                        URLMAPPING.getInstance().getEndpoints());
        assertThat(knownEndpoints).isNotEmpty();

        // now collect up the endpoints we actually hit
        long runkey = getRunKey();
        List<String> usedEndpoints = trafficLogService.getUsedEndpoints(runkey);

        // remove evidence of access to "/" on the target server (empty path)
        // that's not part of IDL hence not interesting for 'coverage'
        List<String> usedIdlEndpoints = usedEndpoints.stream()
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());

        log.debug("allEndpointsShouldBeUsed runkey " + runkey
                + " sees usedIdlEndpoints list size of " + usedIdlEndpoints.size());

        // let's discard any prefix and suffix "/" from both Lists
        List<String> actual = new ArrayList<>();
        for(String s : usedIdlEndpoints){
            if(s.startsWith("/")) s = s.substring(1);
            if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
            actual.add(s);
        }

        List<String> expected = new ArrayList<>();
        for(String s : knownEndpoints){
            if(s.startsWith("/")) s = s.substring(1);
            if(s.endsWith("/")) s = s.substring(0, s.length() - 1);
            expected.add(s);
        }

        // assertThat( actual ).containsOnlyElementsOf ( expected )
        assertThat(actual).containsOnlyElementsOf(expected);

    }

    /**
     * Gets the 'runKey' as set up in TestRunner and available here because
     * the ant file maps it as a <sysproperty/> in the junit task.
     *
     * @return the runKey
     */
    private long getRunKey() {
        String rk = System.getProperty("ctk.runkey");
        if(rk != null && !rk.isEmpty()){
            return Long.parseLong(rk);
        }
        log.warn("No ctk.runkey property, responding with default of -1");
        return -1;
    }
}
