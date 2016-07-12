package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.VariantServiceOuterClass.SearchVariantSetsRequest;
import ga4gh.VariantServiceOuterClass.SearchVariantSetsResponse;
import ga4gh.Variants.VariantSet;
import org.ga4gh.ctk.CtkLogs;
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
 * Test the <tt>/variantsets/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(VariantsTests.class)
public class VariantSetsPagingIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link VariantSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariantSets(SearchVariantSetsRequest)}.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughVariantSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // retrieve them all first
        final List<VariantSet> listOfVariantSets = Utils.getAllVariantSets(client);
        assertThat(listOfVariantSets).isNotEmpty();

        // we will remove VariantSets from this Set and assert at the end that we have zero
        final Set<VariantSet> setOfVariants = new HashSet<>(listOfVariantSets);
        assertThat(listOfVariantSets).hasSize(setOfVariants.size());

        // page through the VariantSets using the same query parameters
        String pageToken = "";
        for (VariantSet ignored : listOfVariantSets) {
            final SearchVariantSetsRequest pageReq =
                    SearchVariantSetsRequest.newBuilder()
                                            .setDatasetId(TestData.getDatasetId())
                                            .setPageSize(1)
                                            .setPageToken(pageToken)
                                            .build();
            final SearchVariantSetsResponse pageResp = client.variants.searchVariantSets(pageReq);
            final List<VariantSet> pageOfVariantSets = pageResp.getVariantSetsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfVariantSets).hasSize(1);
            assertThat(setOfVariants).contains(pageOfVariantSets.get(0));

            setOfVariants.remove(pageOfVariantSets.get(0));
        }

        assertThat(pageToken).isEmpty();
        assertThat(setOfVariants).isEmpty();
    }

    /**
     * Check that we can page through the {@link VariantSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariantSets(SearchVariantSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneChunkThroughVariantSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<VariantSet> listOfVariantSets = Utils.getAllVariantSets(client);

        // page through the variants in one gulp
        checkSinglePageOfVariantSets(listOfVariantSets.size(),
                                     listOfVariantSets);
    }

    /**
     * Check that we can page through the {@link VariantSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariantSets(SearchVariantSetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughVariantSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<VariantSet> listOfVariantSets = Utils.getAllVariantSets(client);

        checkSinglePageOfVariantSets(listOfVariantSets.size() * 2,
                                     listOfVariantSets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link VariantSet}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariantSets
     * (SearchVariantSetsRequest)},
     * using <tt>pageSize</tt> as the page size.
     *
     * @param pageSize         the page size we'll request
     * @param expectedVariantSets all of the {@link VariantSet} objects we expect to receive
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfVariantSets(int pageSize,
                                              List<VariantSet> expectedVariantSets) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchVariantSetsRequest pageReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .setPageSize(pageSize)
                                        .build();
        final SearchVariantSetsResponse pageResp = client.variants.searchVariantSets(pageReq);
        final List<VariantSet> pageOfVariantSets = pageResp.getVariantSetsList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfVariantSets).hasSize(expectedVariantSets.size());
        assertThat(expectedVariantSets).containsAll(pageOfVariantSets);

        assertThat(pageToken).isEmpty();
    }

}
