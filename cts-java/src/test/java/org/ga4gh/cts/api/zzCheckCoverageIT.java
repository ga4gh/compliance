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
 * <p>The test depends on the naming conventions (messages are compiled into the
 * top-level org.ga4gh package, names are suffixed Request/Response) when it scans the
 * classpath to learn the names of the available messages, methods, and data types.</p>
 * <p>Created by Wayne Stidolph on 5/30/2015.</p>
 */
public class zzCheckCoverageIT {

    static URLMAPPING urls;
    static Logger log = LoggerFactory.getLogger(zzCheckCoverageIT.class);
    static Logger testlog = LoggerFactory.getLogger(CtkLogs.SYSTEST);
    static TestActivityDataService testActivityDataService;
    static DomainInformationService domainInformationService;

    @BeforeClass
    public static void setupServices() throws Exception {
        log.info("setupServices to get the TestActivityDataService, DomainInformationService, and 'urls'");
        testActivityDataService = TestActivityDataService.getService();
        domainInformationService = DomainInformationService.getService();
        urls = URLMAPPING.getInstance(); // reload defaults
    }

    @Test
    public void allIdlRequestsShouldBeSent() throws Exception {
        org.junit.Assert.assertFalse("DomainInformationService gave empty list of Request types",
                domainInformationService.getRequestTypes().isEmpty() );
        long runkey = getRunKey();
        List<String> usedRequests = testActivityDataService.getUsedRequests(runkey);
        log.debug("allIdlRequestsShouldBeSent runkey " + runkey + " sees usedRequests list size is " + usedRequests.size());
        assertThat(usedRequests).containsOnlyElementsOf(domainInformationService.getRequestTypes());
    }

    @Test
    public void allIdlResponsesShouldBeReceived() throws Exception {
        org.junit.Assert.assertFalse("DomainInformationService gave empty list of Response types",
                domainInformationService.getResponseTypes().isEmpty() );
        long runkey = getRunKey();
        List<String> usedResponses = testActivityDataService.getUsedResponses(runkey);
        testlog.debug("allIdlResponsesShouldBeReceived runkey " + runkey + " sees usedResponses list size is " + usedResponses.size());
        assertThat(usedResponses).containsOnlyElementsOf(domainInformationService.getResponseTypes());
    }

    @Ignore("Unimplemented")
    @Test
    public void allIdlDatatypesShouldBeUsed() throws Exception {
        // plan is to attach the Tables of what-was-used to the
        // TestContext, then here to refer to that and make assertions
    }

    @Test
    public void allEndpointsShouldBeUsed() throws Exception {
        testlog.warn("show access to testlog from tests");
        // if an endpoint is left defined, then some test should hit it.

        // First, find the 'active' endpoints the CTK knows about.
        List<String> sortedEndpoints =
                domainInformationService.getActiveEndpoints(
                        URLMAPPING.getInstance().getEndpoints());
        assertThat(sortedEndpoints).isNotEmpty();

        // now collect up the endpoints we actually hit
        long runkey = getRunKey();
        List<String> usedEndpoints = testActivityDataService.getUsedEndpoints(runkey);

        // remove evidence of access to "/" on the target server, that's not
        // part of IDL hence not interesting from a 'coverage' standpoint
        List<String> usedIdlEndpoints = usedEndpoints.stream()
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());

        log.debug("allEndpointsShouldBeUsed runkey " + runkey
                + " sees usedIdlEndpoints list size of " + usedIdlEndpoints.size());

        // assertThat( actual ).containsOnlyElementsOf ( expected )
        assertThat(usedIdlEndpoints).containsOnlyElementsOf(sortedEndpoints);

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
