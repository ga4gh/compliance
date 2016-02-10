package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Variants.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test searching variants.
 */
@Category(VariantsTests.class)
public class VariantsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * For every {@link Variant} in the {@link List}, call the {@link Consumer}.
     * @param variants the list to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllVariants(List<Variant> variants, Consumer<Variant> cons) {
        variants.stream().forEach(cons::accept);
    }

    /**
     * For every {@link Call} in the {@link Variant}s in the {@link List}, call the {@link Consumer}.
     * @param variants the list to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllCalls(List<Variant> variants, Consumer<Call> cons) {
        variants.stream().forEach(v -> v.getCallsList()
                                        .stream()
                                        .forEach(cons::accept));
    }

    /**
     * Fetch variants between two positions in the reference and count them.  The number must
     * equal what we're expecting by visual examination of the variants data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpectedNumberOfVariants() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfVariants = 6;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSetsList();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariantsList();

        assertThat(searchVariants).hasSize(expectedNumberOfVariants);
    }

    /**
     * Check that the variants we receive from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * search contain the expected reference name.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkVariantsForExpectedReferenceName() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSetsList();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariantsList();

        checkAllVariants(searchVariants, v -> assertThat(v.getReferenceName()).isEqualTo(TestData.REFERENCE_NAME));
        checkAllCalls(searchVariants, c -> assertThat(c).isNotNull());
    }

    /**
     * Check that the variants we receive from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * search contain well-formed {@link Call}s.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkCallsForWellFormedness() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSetsList();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariantsList();

        checkAllCalls(searchVariants, c -> assertThat(c.getGenotypeList()).isNotNull().isNotEmpty());
        checkAllCalls(searchVariants, c -> assertThat(c.getGenotypeLikelihoodList()).isNotNull());
        checkAllCalls(searchVariants, c -> {
            assertThat(c.getInfo()).isNotNull();
            // check that the info map contains no null keys or values
            c.getInfo().keySet().stream().forEach(key -> {
                assertThat(key).isNotNull();
                assertThat(c.getInfo().get(key)).isNotNull();
            });
        });
    }

}
