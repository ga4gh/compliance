package org.ga4gh.cts.api.reads;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadsResponse;
import ga4gh.Reads.ReadAlignment;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
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
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughReads() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

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
        String pageToken = "";
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .addAllReadGroupIds(aSingle(readGroupId))
                                      .setPageSize(1)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignmentsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReads).hasSize(1);
            setOfReadsGathered1By1.add(pageOfReads.get(0));
        } while (pageToken != null && !pageToken.equals(""));

        assertThat(setOfReadsGathered1By1).containsAll(setOfExpectedReads);
    }

    /**
     * Check that we can have two independent sequences of page 1 by 1 through the {@link ReadAlignment}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}, and
     * compare the collections of objects at the end.  They should be identical.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkTwoSimultaneousPagingSequencesThroughReads() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        final Set<ReadAlignment> setOfReads0 = new HashSet<>();
        final Set<ReadAlignment> setOfReads1 = new HashSet<>();

        // page through the ReadAlignments using the same query parameters, and collect them

        String pageToken0 = "";
        String pageToken1 = "";
        do {
            final SearchReadsRequest page0Req =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .addAllReadGroupIds(aSingle(readGroupId))
                                      .setPageSize(1)
                                      .setPageToken(pageToken0)
                                      .build();
            final SearchReadsRequest page1Req =
                    SearchReadsRequest.newBuilder()
                                      .setReferenceId(referenceId)
                                      .addAllReadGroupIds(aSingle(readGroupId))
                                      .setPageSize(1)
                                      .setPageToken(pageToken1)
                                      .build();
            final SearchReadsResponse page0Resp = client.reads.searchReads(page0Req);
            final List<ReadAlignment> pageOfReads0 = page0Resp.getAlignmentsList();
            setOfReads0.addAll(page0Resp.getAlignmentsList());
            pageToken0 = page0Resp.getNextPageToken();

            final SearchReadsResponse page1Resp = client.reads.searchReads(page1Req);
            final List<ReadAlignment> pageOfReads1 = page0Resp.getAlignmentsList();
            setOfReads1.addAll(page1Resp.getAlignmentsList());
            pageToken1 = page1Resp.getNextPageToken();

            assertThat(pageOfReads0).hasSameSizeAs(pageOfReads1);
            assertBothAreEmptyOrBothAreNot(pageToken0, pageToken1);
        } while (pageToken0 != null && !pageToken0.equals(""));

        assertThat(setOfReads0).containsAll(setOfReads1);
        assertThat(setOfReads1).containsAll(setOfReads0);
    }

    /**
     * Assert that both string arguments are empty, or neither is.
     * @param token0 a string to test
     * @param token1 a string to test
     */
    private void assertBothAreEmptyOrBothAreNot(String token0, String token1) {
        if ("".equals(token0)) {
            assertThat(token1).isEmpty();
        }
        if ("".equals(token1)) {
            assertThat(token0).isEmpty();
        }
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s (familiarly, "reads")
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)} using
     * two different chunk sizes.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByRelativelyPrimeChunksOfReads() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final int pageSize0 = 3;
        final int pageSize1 = 7;

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        final Set<ReadAlignment> firstSetOfReads = new HashSet<>();
        // page through the ReadAlignments using the same query parameters and collect them
        String pageToken = "";
        // page by pageSize0
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .addAllReadGroupIds(aSingle(readGroupId))
                                      .setReferenceId(referenceId)
                                      .setPageSize(pageSize0)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignmentsList();
            pageToken = pageResp.getNextPageToken();

            firstSetOfReads.addAll(pageOfReads);
        } while (pageToken != null && !pageToken.equals(""));

        final Set<ReadAlignment> secondSetOfReads = new HashSet<>();
        // page through the ReadAlignments again using the same query parameters and collect them
        pageToken = "";
        // page by pageSize1
        do {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                      .addAllReadGroupIds(aSingle(readGroupId))
                                      .setReferenceId(referenceId)
                                      .setPageSize(pageSize1)
                                      .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignmentsList();
            pageToken = pageResp.getNextPageToken();

            secondSetOfReads.addAll(pageOfReads);
        } while (pageToken != null && !pageToken.equals(""));

        // assert that the sets contain the identical elements
        assertThat(secondSetOfReads).containsAll(firstSetOfReads);
        assertThat(firstSetOfReads).containsAll(secondSetOfReads);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReadAlignment}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param refId         the ID of the {@link ga4gh.References}
     * @param readGroupId   the ID of the {@link ga4gh.Reads.ReadGroup}
     * @param pageSize      the page size we'll request
     * @param expectedReads all of the {@link ReadAlignment} objects we expect to receive
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfReads(String refId,
                                        String readGroupId,
                                        int pageSize,
                                        List<ReadAlignment> expectedReads) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadsRequest pageReq =
                SearchReadsRequest.newBuilder()
                                  .setReferenceId(refId)
                                  .addAllReadGroupIds(aSingle(readGroupId))
                                  .setPageSize(pageSize)
                                  .build();
        final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
        final List<ReadAlignment> pageOfReads = pageResp.getAlignmentsList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReads).hasSize(expectedReads.size());
        assertThat(expectedReads).containsAll(pageOfReads);

        assertThat(pageToken).isEmpty();
    }
}
