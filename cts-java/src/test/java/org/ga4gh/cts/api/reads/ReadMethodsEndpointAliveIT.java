package org.ga4gh.cts.api.reads;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.RespCode;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.*;
import org.ga4gh.models.ReadGroup;
import org.ga4gh.models.ReadGroupSet;
import org.ga4gh.models.ReferenceSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

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

    /**
     * Utility method to fetch the ID of a reference to which we can map the reads we're testing.
     * @return the ID of a reference
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    private static String getValidReferenceId() throws AvroRemoteException {
        final SearchReferenceSetsRequest refSetReq = SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse refSetResp = client.references.searchReferenceSets(refSetReq);
        assertThat(refSetResp).isNotNull();
        assertThat(refSetResp.getReferenceSets()).isNotNull().isNotEmpty();
        final ReferenceSet refSet = refSetResp.getReferenceSets().get(0);

        final List<String> refIds = refSet.getReferenceIds();
        assertThat(refIds).isNotNull().isNotEmpty();
        return refIds.get(0);
    }

    /**
     * Utility method to fetch the ID of an arbitrary ReadGroup.
     * @return the ID of an arbitrary ReadGroup
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    private static String getReadGroupId() throws AvroRemoteException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest
                        .newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSets();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        List<ReadGroup> readGroups = readGroupSet.getReadGroups();
        assertThat(readGroups).isNotEmpty().isNotNull();
        final ReadGroup readGroup = readGroups.get(0);
        assertThat(readGroup).isNotNull();
        return readGroup.getId();
    }

    /**
     * Show that a SearchReadsRequest is accepted and
     * returns a parseable Response.
     *
     * @throws Exception the exception
     */
    @Test
    public void defaultReadsRequestGetsNullAlignments() throws Exception {

        // first get a valid reference to map our read to
        final String refId = getValidReferenceId();

        final String readGroupId = getReadGroupId();

        // then do a read search
        final SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(readGroupId))
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();
        final SearchReadsResponse rtn = client.reads.searchReads(srr);
        assertThat(rtn.getAlignments()).isNullOrEmpty();
    }

    /**
     * Check that an unmatched {@link SearchReadsRequest} results in a thrown exception.
     *
     * @param readGroupId the ReadGroup ID, passed in using the @Parameters annotation
     * @throws Exception the exception
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    @Parameters({
            "myNonsenseId", "foo", ""
    })
    public void unmatchedReadGroupIdElicitsErrorMsg(String readGroupId) throws Exception {
        // get a valid reference to map our read to
        final String refId = getValidReferenceId();

        final SearchReadsRequest searchReadsReq =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(readGroupId))
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();

        final GAWrapperException t =
                catchGAWrapperException(() -> client.reads.searchReads(searchReadsReq));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
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
     * @param readGroupId the readgroup ID
     * @param expStatus the exp status
     * @throws Exception the exception
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    // Normally we'd just pass in the strings here, but the parameters are used
    // to create final TAP output streams, so the parameter values need to be
    // legal filenames ... this means, no colons in the readGroupIds!
    // The workaround is simple enough, and actually seems a bit more readable,
    // so we'll pass in a (valid) key to a static Map (see readGroupIdMap just below),
    // and in the test method we'll look up the actual parameter.
    @Parameters({
            "TWO_GOOD, NOT_IMPLEMENTED",
            "ONE_GOOD_ONE_BAD, NOT_IMPLEMENTED",
            "TWO_BAD, NOT_IMPLEMENTED",
            "THREE_GOOD, NOT_IMPLEMENTED"
    })
    public void multipleReadGroupsNotSupported(String readGroupId, RespCode expStatus) throws Exception {
        String replacedReadGroupId = readGroupIdMap.get(readGroupId);
        SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(Arrays.asList(replacedReadGroupId.split(";")))
                                  .build();
        final GAWrapperException e = catchGAWrapperException(() -> client.reads.searchReads(srr));
        assertThat(e.getHttpStatusCode()).isEqualTo(expStatus.getCode());
    }

    /**
     * Map from string acceptable as a file name (for TAP) and the actual
     * parameters string the test method wants, to get around problem that
     * GA4GH readGroupID are not necessarily valid filenames
     */
    private static Map<String,String> readGroupIdMap;
    static {
        readGroupIdMap = new HashMap<>();
        readGroupIdMap.put("TWO_GOOD", "low-coverage:HG00534;low-coverage:HG00533");
        readGroupIdMap.put("ONE_GOOD_ONE_BAD", "low-coverage:HG00534;BAD_ID");
        readGroupIdMap.put("TWO_BAD", "DUMB_ID;BAD_ID");
        readGroupIdMap.put("THREE_GOOD", "low-coverage:HG00534;low-coverage:HG00533;low-coverage:HG00533");
    }

    /**
     * Check that attempting to supply 0 readgroup IDs in a {@link SearchReadsRequest} is not OK.
     *
     * @throws Exception the exception
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void emptyReadGroupIdIsNotFound() throws Exception {
        // first get a valid reference to map our read to
        final String refId = getValidReferenceId();

        final SearchReadsRequest srr =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(Collections.emptyList())
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();
        final GAWrapperException t = catchGAWrapperException(() -> client.reads.searchReads(srr));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
    }

}
