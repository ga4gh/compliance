package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.VariantServiceOuterClass.SearchVariantsRequest;
import ga4gh.VariantServiceOuterClass.SearchVariantsResponse;
import ga4gh.Variants.Variant;
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
 * Test the <tt>/variants/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(VariantsTests.class)
public class VariantsPagingIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the variants we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughVariants() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> listOfVariants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        // we will remove Variants from this Set and assert at the end that we have zero
        final Set<Variant> setOfVariants = new HashSet<>(listOfVariants);
        assertThat(listOfVariants).hasSize(setOfVariants.size());

        // page through the variants using the same query parameters
        String pageToken = "";
        for (Variant ignored : listOfVariants) {
            final SearchVariantsRequest pageReq =
                    SearchVariantsRequest.newBuilder()
                                         .setVariantSetId(variantSetId)
                                         .setReferenceName(TestData.REFERENCE_NAME)
                                         .setStart(start).setEnd(end)
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchVariantsResponse pageResp = client.variants.searchVariants(pageReq);
            final List<Variant> pageOfVariants = pageResp.getVariantsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfVariants).hasSize(1);
            assertThat(setOfVariants).contains(pageOfVariants.get(0));

            setOfVariants.remove(pageOfVariants.get(0));
        }

        assertThat(pageToken).isEmpty();
        assertThat(setOfVariants).isEmpty();
    }

    /**
     * Check that we can page through the variants we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneChunkThroughVariants() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> listOfVariants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        // page through the variants in one gulp
        checkSinglePageOfVariants(variantSetId, start, end,
                                  listOfVariants.size(),
                                  listOfVariants);
    }

    /**
     * Check that we can page through the variants we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughVariants() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> listOfVariants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        checkSinglePageOfVariants(variantSetId, start, end,
                                  listOfVariants.size() * 2,
                                  listOfVariants);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of variants from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants
     * (SearchVariantsRequest)}, using <tt>pageSize</tt> as the page size.
     *
     * @param variantSetId     the ID of the {@link VariantSet} we're paging through
     * @param start            the start value for the range we're searching
     * @param end              the end value for the range we're searching
     * @param pageSize         the page size we'll request
     * @param expectedVariants all of the {@link Variant} objects we expect to receive
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfVariants(String variantSetId,
                                           long start, long end,
                                           int pageSize,
                                           List<Variant> expectedVariants) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final SearchVariantsRequest pageReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setPageSize(pageSize)
                                     .build();
        final SearchVariantsResponse pageResp = client.variants.searchVariants(pageReq);
        final List<Variant> pageOfVariants = pageResp.getVariantsList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfVariants).hasSize(expectedVariants.size());
        assertThat(expectedVariants).containsAll(pageOfVariants);

        assertThat(pageToken).isEmpty();
    }

}
