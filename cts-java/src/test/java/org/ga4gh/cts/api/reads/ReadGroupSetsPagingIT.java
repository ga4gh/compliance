package org.ga4gh.cts.api.reads;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsResponse;
import ga4gh.Reads.ReadAlignment;
import ga4gh.Reads.ReadGroupSet;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/readgroupsets/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadGroupSetsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link ReadGroupSet}s
     * we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // retrieve them all - this may return fewer than "all."
        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // we will do a set comparison after retrieving them 1 at a time
        final Set<ReadGroupSet> setOfExpectedReadGroupSets = new HashSet<>(listOfReadGroupSets);
        assertThat(listOfReadGroupSets).hasSize(setOfExpectedReadGroupSets.size());

        final Set<ReadGroupSet> setOfReadGroupSetsGathered1By1 =
                new HashSet<>(setOfExpectedReadGroupSets.size());
        // page through the ReadGroupSets using the same query parameters
        String pageToken = "";
        do {
            final SearchReadGroupSetsRequest pageReq =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setPageSize(1)
                                              .setPageToken(pageToken)
                                              .build();
            final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(pageReq);
            final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSetsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReadGroupSets).hasSize(1);
            setOfReadGroupSetsGathered1By1.add(pageOfReadGroupSets.get(0));
        } while (pageToken != null && !pageToken.equals(""));

        assertThat(setOfReadGroupSetsGathered1By1).containsAll(setOfExpectedReadGroupSets);
    }

    /**
     * Check that we can page through the {@link ReadGroupSet}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)} using
     * two different chunk sizes.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByRelativelyPrimeChunksOfReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final int pageSize0 = 3;
        final int pageSize1 = 7;

        final Set<ReadGroupSet> firstSetOfReadGroups = new HashSet<>();
        // page through the ReadAlignments using the same query parameters and collect them

        String pageToken = "";
        // page by pageSize0
        do {
            final SearchReadGroupSetsRequest readGroupSetsReq =
                    SearchReadGroupSetsRequest
                            .newBuilder()
                            .setPageSize(pageSize0)
                            .setPageToken(pageToken)
                            .setDatasetId(TestData.getDatasetId())
                            .build();
            final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(readGroupSetsReq);
            final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSetsList();
            pageToken = pageResp.getNextPageToken();

            firstSetOfReadGroups.addAll(pageOfReadGroupSets);
        } while (pageToken != null && !pageToken.equals(""));

        final Set<ReadGroupSet> secondSetOfReadGroupSets = new HashSet<>();
        // page through the ReadAlignments again using the same query parameters and collect them

        // page by pageSize1
        pageToken = "";
        do {
            final SearchReadGroupSetsRequest readGroupSetsReq =
                    SearchReadGroupSetsRequest
                            .newBuilder()
                            .setPageSize(pageSize1)
                            .setPageToken(pageToken)
                            .setDatasetId(TestData.getDatasetId())
                            .build();
            final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(readGroupSetsReq);
            final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSetsList();
            pageToken = pageResp.getNextPageToken();

            secondSetOfReadGroupSets.addAll(pageOfReadGroupSets);
        } while (pageToken != null && !pageToken.equals(""));

        // assert that the sets contain the identical elements
        assertThat(secondSetOfReadGroupSets).containsAll(firstSetOfReadGroups);
        assertThat(firstSetOfReadGroups).containsAll(secondSetOfReadGroupSets);
    }

    /**
     * Check that we can have two independent sequences of paging 1 object at a time through the {@link ReadGroupSet}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}, and
     * compare the collections of objects at the end.  They should be identical.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkTwoSimultaneousPagingSequencesThroughReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final Set<ReadGroupSet> setOfReadGroupSets0 = new HashSet<>();
        final Set<ReadGroupSet> setOfReadGroupSets1 = new HashSet<>();

        // page through the ReadGroupSets using the same query parameters, and collect them

        String pageToken0 = "";
        String pageToken1 = "";
        do {
            final SearchReadGroupSetsRequest page0Req =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setPageSize(1)
                                              .setPageToken(pageToken0)
                                              .build();
            final SearchReadGroupSetsRequest page1Req =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setPageSize(1)
                                              .setPageToken(pageToken1)
                                      .build();
            final SearchReadGroupSetsResponse page0Resp = client.reads.searchReadGroupSets(page0Req);
            final List<ReadGroupSet> pageOfReadGroupSets0 = page0Resp.getReadGroupSetsList();
            setOfReadGroupSets0.addAll(pageOfReadGroupSets0);
            pageToken0 = page0Resp.getNextPageToken();

            final SearchReadGroupSetsResponse page1Resp = client.reads.searchReadGroupSets(page1Req);
            final List<ReadGroupSet> pageOfReadGroupSets1 = page0Resp.getReadGroupSetsList();
            setOfReadGroupSets1.addAll(pageOfReadGroupSets1);
            pageToken1 = page1Resp.getNextPageToken();

            assertThat(pageOfReadGroupSets0).hasSameSizeAs(pageOfReadGroupSets1);
            assertBothAreEmptyOrBothAreNot(pageToken0, pageToken1);
        } while (pageToken0 != null && !pageToken0.equals(""));

        assertThat(setOfReadGroupSets0).containsAll(setOfReadGroupSets0);
        assertThat(setOfReadGroupSets1).containsAll(setOfReadGroupSets0);
    }

    /**
     * Assert that both string arguments are empty strings, or neither is.
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
     * Check that we can page through the {@link ReadGroupSet}s we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneChunkThroughReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // page through the reads in one gulp
        checkSinglePageOfReadGroupSets(listOfReadGroupSets.size(),
                                       listOfReadGroupSets);
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // page through the reads in one too-large gulp
        checkSinglePageOfReadGroupSets(listOfReadGroupSets.size() * 2,
                                       listOfReadGroupSets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReadGroupSet}s from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param pageSize              the page size we'll request
     * @param expectedReadGroupSets all of the {@link ReadGroupSet} objects we expect to receive
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfReadGroupSets(int pageSize, List<ReadGroupSet> expectedReadGroupSets) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final SearchReadGroupSetsRequest pageReq =
                SearchReadGroupSetsRequest.newBuilder()
                                  .setDatasetId(TestData.getDatasetId())
                                  .setPageSize(pageSize)
                                  .build();
        final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(pageReq);
        final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSetsList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReadGroupSets).hasSize(expectedReadGroupSets.size());
        assertThat(expectedReadGroupSets).containsAll(pageOfReadGroupSets);

        assertThat(pageToken).isEmpty();
    }
}
