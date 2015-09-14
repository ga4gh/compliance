package org.ga4gh.cts.api.variants;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchVariantSetsRequest;
import org.ga4gh.methods.SearchVariantSetsResponse;
import org.ga4gh.models.VariantSet;
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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughVariantSets() throws AvroRemoteException {

        // retrieve them all first
        final List<VariantSet> listOfVariantSets = Utils.getAllVariantSets(client);
        assertThat(listOfVariantSets).isNotEmpty();

        // we will remove VariantSets from this Set and assert at the end that we have zero
        final Set<VariantSet> setOfVariants = new HashSet<>(listOfVariantSets);
        assertThat(listOfVariantSets).hasSize(setOfVariants.size());

        // page through the VariantSets using the same query parameters
        String pageToken = null;
        for (VariantSet ignored : listOfVariantSets) {
            final SearchVariantSetsRequest pageReq =
                    SearchVariantSetsRequest.newBuilder()
                                            .setDatasetId(TestData.getDatasetId())
                                            .setPageSize(1)
                                            .setPageToken(pageToken)
                                            .build();
            final SearchVariantSetsResponse pageResp = client.variants.searchVariantSets(pageReq);
            final List<VariantSet> pageOfVariantSets = pageResp.getVariantSets();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfVariantSets).hasSize(1);
            assertThat(setOfVariants).contains(pageOfVariantSets.get(0));

            setOfVariants.remove(pageOfVariantSets.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfVariants).isEmpty();
    }

    /**
     * Check that we can page through the {@link VariantSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariantSets(SearchVariantSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughVariantSets() throws AvroRemoteException {

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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughCalls() throws AvroRemoteException {

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
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfVariantSets(int pageSize,
                                              List<VariantSet> expectedVariantSets)
            throws AvroRemoteException {

        final SearchVariantSetsRequest pageReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .setPageSize(pageSize)
                                        .build();
        final SearchVariantSetsResponse pageResp = client.variants.searchVariantSets(pageReq);
        final List<VariantSet> pageOfVariantSets = pageResp.getVariantSets();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfVariantSets).hasSize(expectedVariantSets.size());
        assertThat(expectedVariantSets).containsAll(pageOfVariantSets);

        assertThat(pageToken).isNull();
    }

}
