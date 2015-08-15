package org.ga4gh.cts.api.reads;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.JUnitSoftAssertions;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.RespCode;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.ctk.transport.WireTrackerAssert;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.methods.SearchReadsRequest;
import org.ga4gh.methods.SearchReadsResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * <p>Verifies basic sanity of the reads/search API.</p>
 * <p>The {@code READS} API methods (as defined by the {@code readmethods.avdl}) are:</p>
 * <ul>
 * <li>POST reads/search of SearchReadsRequest yields SearchReadsResponse</li>
 * <li>POST /readgroupsets/search of SearchReadGroupSetsRequest yields SearchReadGroupSetsResponse</li>
 * </ul>
 *
 * <p>The test invokes a search request with null, default, and error parameters
 * on the endpoint and verifies the response. For tests with more insight into
 * the data returned (complex queries, etc) refer to the ReadsSearchIT tests.</p>
 *
 * <p>The test shows simply making an assertion about the existence of the return itself
 * (in {@code defaultReadgroupsetsRequestGetsResponse} and shows making an assertion about
 * a field of the return (in {@code defaultReadsRequestGetsNullAlignments})</p>
 *
 * <p>Created by Wayne Stidolph on 5/20/15.</p>
 */
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReadMethodsEndpointAliveIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    /**
     * Show that a SearchReadsRequest is accepted and
     * returns a parseable Response.
     *
     * @throws Exception the exception
     */
    @Test
    public void defaultReadsRequestGetsNullAlignments() throws Exception {
        // do a read search
        SearchReadsRequest srr = SearchReadsRequest.newBuilder()
                                                    .build();
        SearchReadsResponse rtn = client.reads.searchReads(srr);
        assertThat(rtn.getAlignments()).isNullOrEmpty();
    }

    /**
     * Show that a SearchReadGroupSetsRequest is accepted and
     * returns a parseable Response.
     *
     * @throws Exception the exception
     */
    @Test
    public void defaultReadgroupsetsRequestGetsResponse() throws Exception {
        SearchReadGroupSetsRequest srgs =
                SearchReadGroupSetsRequest.newBuilder().build();
        SearchReadGroupSetsResponse rtn = client.reads.searchReadGroupSets(srgs);

        assertThat(rtn).isNotNull();
    }

    /**
     * Unmatched SearchReadsRequest elicits error msg.
     *
     * @param rgid the rgid
     * @throws Exception the exception
     */
    @Test
    @Parameters({
            "myNonsenseId", "foo", ""
    })
    public void unmatchedReadgroupidElicitsErrorMsg(String rgid) throws Exception {
        SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(rgid))
                                  .build();
        WireTracker mywt = new WireTracker();
        SearchReadsResponse rtn = client.reads.searchReads(srr, mywt);

        WireTrackerAssert.assertThat(mywt)
                         .hasResponseStatus(RespCode.NOT_FOUND);
    }

    /**
     * <p>Shows that attempting to supply more than 1 readgroup IDs in a SearchReadsRequest are not accepted.</p>
     * <p>This is tested with:</p>
     * <ul>
     *     <li>a pair of valid readgroups (which returns a <b>NOT IMPLEMENTED</b>)</li>
     *     <li>one valid and one invalid readgroup</li>
     *     <li>a pair of invalid readgroupID</li>
     *     <li>a repeated set of valid readgroupID</li>
     * </ul>
     *
     * @param rgid the rgid
     * @param expStatus the exp status
     * @throws Exception the exception
     */
    @Test
    // Normally we'd just pass in the strings here, but the parameters are used
    // to create final TAP output streams, so the parameter values need to be
    // legal filenames ... this means, no colons in the readGroupIds!
    // The workaround is simple enough, and actually seems a bit more readable,
    // so we'll pass in a (valid) key to a static Map (see rgidMap just below),
    // and in the test method we'll look up the actual parameter.
    @Parameters({
            "TWO_GOOD, NOT_IMPLEMENTED",
            "ONE_GOOD_ONE_BAD, NOT_IMPLEMENTED",
            "TWO_BAD, NOT_IMPLEMENTED",
            "THREE_GOOD, NOT_IMPLEMENTED"
    })
    public void multipleReadGroupsNotSupported(String rgid, RespCode expStatus) throws Exception {
        String replacedRgid = rgidMap.get(rgid);
        SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(Arrays.asList(replacedRgid.split(";")))
                                  .build();
        WireTracker mywt = new WireTracker();
        SearchReadsResponse rtn = client.reads.searchReads(srr, mywt);
        WireTrackerAssert.assertThat(mywt)
                         .hasResponseStatus(expStatus);
        assertThat(mywt.gotParseableGAE()).isTrue();
    }

    /**
     * Map from string acceptable as a file name (for TAP) and the actual
     * parameters string the test method wants, to get around problem that
     * GA4GH readGroupID are not necessarily valid filenames
     */
    private static Map<String,String> rgidMap;
    static {
        rgidMap = new HashMap<>();
        rgidMap.put("TWO_GOOD","low-coverage:HG00534;low-coverage:HG00533");
        rgidMap.put("ONE_GOOD_ONE_BAD","low-coverage:HG00534;BAD_ID");
        rgidMap.put("TWO_BAD", "DUMB_ID;BAD_ID");
        rgidMap.put("THREE_GOOD", "low-coverage:HG00534;low-coverage:HG00533;low-coverage:HG00533");
    }

    /**
     * <p>Shows that attempting to supply 0 readgroup IDs in a SearchReadsRequest is sanely NOT_FOUND.</p>
     * @throws Exception the exception
     */
    @Test
    public void emptyReadGroupIdIsNotFound() throws Exception {
        SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(""))
                                  .build();
        WireTracker mywt = new WireTracker();
        SearchReadsResponse rtn = client.reads.searchReads(srr, mywt);
        WireTrackerAssert.assertThat(mywt)
                         .hasResponseStatus(RespCode.NOT_FOUND);
    }

}
