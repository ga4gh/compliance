package org.ga4gh.cts.api.variants;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.*;
import org.ga4gh.models.Call;
import org.ga4gh.models.Variant;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test searching variants and variant sets.
 */
@Category(VariantsTests.class)
public class VariantsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Fetch variants between two positions in the reference and count them.  The number must
     * equal what we're expecting by visual examination of the variants data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfVariants() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfVariants = 6;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariants();

        assertThat(searchVariants).hasSize(expectedNumberOfVariants);
    }

    /**
     * Check that we receive the expected number of {@link VariantSet}s.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkExpectedVariantSets() throws AvroRemoteException {
        final int expectedNumberOfVariantSets = 1;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).hasSize(expectedNumberOfVariantSets);
    }

    /**
     * Check that the variants we receive from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * search contain the expected reference name.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkVariantsForExpectedReferenceName() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariants();

        searchVariants.stream().forEach(v -> assertThat(v.getReferenceName()).isEqualTo(TestData.REFERENCE_NAME));

        searchVariants.stream().forEach(v -> v.getCalls()
                                              .stream()
                                              .forEach(c -> assertThat(c).isNotNull()));
    }

    /**
     * Check that the variants we receive from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchVariants(SearchVariantsRequest)}
     * search contain well-formed {@link Call}s.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkCallsForWellFormedness() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariants();

        searchVariants.stream().forEach(v -> v.getCalls().stream()
                                              .forEach(c -> assertThat(c.getGenotype()).isNotNull()
                                                                                       .isNotEmpty()));

        searchVariants.stream().forEach(v -> v.getCalls().stream()
                                              .forEach(c -> assertThat(c.getGenotypeLikelihood())
                                                      .isNotNull()));

        searchVariants.stream().forEach(v -> v.getCalls().stream().forEach(c -> {
            assertThat(c.getInfo()).isNotNull();
            // check that the info map contains no null keys or values
            c.getInfo().keySet().stream().forEach(key -> {
                assertThat(key).isNotNull();
                assertThat(c.getInfo().get(key)).isNotNull();
            });
        }));
    }


}
