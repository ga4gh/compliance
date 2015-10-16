package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadsRequest;
import org.ga4gh.methods.SearchReadsResponse;
import org.ga4gh.models.ReadAlignment;
import org.ga4gh.models.Reference;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Test the <tt>/reads/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link ReadAlignment}s (familiarly, "reads")
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}.
     * <p>
     * The call to retrieve all {@link ReadAlignment}s may return fewer than all of them, subject to
     * server-imposed limits.  The 1-by-1 paging must enumerate them all, however.  The set of "all"
     * must be a subset of those gathered one-by-one.
     * </p>
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        // retrieve them all - this may return fewer than "all."
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // we will do a set comparison after retrieving them 1 at a time
        final Set<ReadAlignment> setOfExpectedReads = new HashSet<>(listOfReads);
        assertThat(listOfReads).hasSize(setOfExpectedReads.size());

        final Set<ReadAlignment> setOfReadsGathered1By1 = new HashSet<>(setOfExpectedReads.size());
        // page through the ReadAlignments using the same query parameters and collect them
        String pageToken = null;
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .setPageSize(1)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReads).hasSize(1);
            setOfReadsGathered1By1.add(pageOfReads.get(0));
        } while (pageToken != null);

        assertThat(setOfReadsGathered1By1).containsAll(setOfExpectedReads);
    }

    /**
     * Check that we can have two independent sequences of page 1 by 1 through the {@link ReadAlignment}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}, and
     * compare the collections of objects at the end.  They should be identical.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkTwoSimultaneousPagingSequencesThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        final Set<ReadAlignment> setOfReads0 = new HashSet<>();
        final Set<ReadAlignment> setOfReads1 = new HashSet<>();

        // page through the ReadAlignments using the same query parameters, and collect them

        String pageToken0 = null;
        String pageToken1 = null;
        do {
            final SearchReadsRequest page0Req =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .setReadGroupIds(aSingle(readGroupId))
                                      .setPageSize(1)
                                      .setPageToken(pageToken0)
                                      .build();
            final SearchReadsRequest page1Req =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .setReadGroupIds(aSingle(readGroupId))
                                      .setPageSize(1)
                                      .setPageToken(pageToken1)
                                      .build();
            final SearchReadsResponse page0Resp = client.reads.searchReads(page0Req);
            final List<ReadAlignment> pageOfReads0 = page0Resp.getAlignments();
            setOfReads0.addAll(page0Resp.getAlignments());
            pageToken0 = page0Resp.getNextPageToken();

            final SearchReadsResponse page1Resp = client.reads.searchReads(page1Req);
            final List<ReadAlignment> pageOfReads1 = page0Resp.getAlignments();
            setOfReads1.addAll(page1Resp.getAlignments());
            pageToken1 = page1Resp.getNextPageToken();

            assertThat(pageOfReads0).hasSameSizeAs(pageOfReads1);
            assertBothAreNullOrBothAreNot(pageToken0, pageToken1);
        } while (pageToken0 != null);

        assertThat(setOfReads0).containsAll(setOfReads1);
        assertThat(setOfReads1).containsAll(setOfReads0);
    }

    /**
     * Assert that both string arguments are null, or neither is.
     * @param token0 a string to test
     * @param token1 a string to test
     */
    private void assertBothAreNullOrBothAreNot(String token0, String token1) {
        if (token0 == null) {
            assertThat(token1).isNull();
        }
        if (token1 == null) {
            assertThat(token0).isNull();
        }
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s (familiarly, "reads")
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)} using
     * two different chunk sizes.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByRelativelyPrimeChunksOfReads() throws AvroRemoteException {

        final int pageSize0 = 3;
        final int pageSize1 = 7;

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        final Set<ReadAlignment> firstSetOfReads = new HashSet<>();
        // page through the ReadAlignments using the same query parameters and collect them
        String pageToken = null;
        // page by pageSize0
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .setReadGroupIds(aSingle(readGroupId))
                                      .setReferenceId(referenceId)
                                      .setPageSize(pageSize0)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
            pageToken = pageResp.getNextPageToken();

            firstSetOfReads.addAll(pageOfReads);
        } while (pageToken != null);

        final Set<ReadAlignment> secondSetOfReads = new HashSet<>();
        // page through the ReadAlignments again using the same query parameters and collect them
        pageToken = null;
        // page by pageSize1
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .setReadGroupIds(aSingle(readGroupId))
                                      .setReferenceId(referenceId)
                                      .setPageSize(pageSize1)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
            pageToken = pageResp.getNextPageToken();

            secondSetOfReads.addAll(pageOfReads);
        } while (pageToken != null);

        // assert that the sets contain the identical elements
        assertThat(secondSetOfReads).containsAll(firstSetOfReads);
        assertThat(firstSetOfReads).containsAll(secondSetOfReads);
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // page through the reads in one gulp
        checkSinglePageOfReads(referenceId, readGroupId, listOfReads.size(),
                               listOfReads);
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // page through the reads in one too-large gulp
        checkSinglePageOfReads(referenceId, readGroupId, listOfReads.size() * 2,
                               listOfReads);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReadAlignment}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param refId         the ID of the {@link Reference}
     * @param readGroupId   the ID of the {@link org.ga4gh.models.ReadGroup}
     * @param pageSize      the page size we'll request
     * @param expectedReads all of the {@link ReadAlignment} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfReads(String refId,
                                        String readGroupId,
                                        int pageSize,
                                        List<ReadAlignment> expectedReads)
            throws AvroRemoteException {

        final SearchReadsRequest pageReq =
                SearchReadsRequest.newBuilder()
                                  .setReferenceId(refId)
                                  .setReadGroupIds(aSingle(readGroupId))
                                  .setPageSize(pageSize)
                                  .build();
        final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
        final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReads).hasSize(expectedReads.size());
        assertThat(expectedReads).containsAll(pageOfReads);

        assertThat(pageToken).isNull();
    }
}
