package org.ga4gh.cts.api.variants;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.SearchVariantSetsRequest;
import org.ga4gh.methods.SearchVariantSetsResponse;
import org.ga4gh.methods.SearchVariantsRequest;
import org.ga4gh.methods.SearchVariantsResponse;
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
     * Search variants.  Fetches variants from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search datasetIds: 1 </pre>(passed in)</li>
     * <li>Test 1: assert that we received a {@link SearchVariantSetsResponse} containing
     * (how many? &gt; 0) {@link VariantSet} objects.  Get the ID of the first one.</li>
     * <li>Query 2: <pre>/variants/search variantSetIds: [variantSetId] referenceName: '22' start:
     *     51005353 end: 51015354 pageSize: 1</pre></li>
     * <li>Test 2: assert that the first result is of type {@link Variant} AND has reference name
     * == "22".</li>
     * <li>Test 3: assert that the <tt>calls</tt> field (a {@link Call}) of that {@link Variant}
     * is not null.</li>
     * <li>Test 4: assert that the <tt>genotype</tt> field of the first {@link Call} is an array
     * of integers.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchForExpectedVariants() throws AvroRemoteException {
        final String referenceName = "3";
        final long start = 60156;
        final long end = 60383;  // should be eight variants

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
                                     .setReferenceName(referenceName)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<Variant> searchVariants = vResp.getVariants();
        final Variant firstVariant = searchVariants.get(0);
        assertThat(firstVariant).isNotNull();
        assertThat(firstVariant.getReferenceName()).isEqualTo(referenceName);

        assertThat(firstVariant.getCalls()).isNotNull();
        final Call call = firstVariant.getCalls().get(0);
        assertThat(call).isNotNull();
        assertThat(call.getGenotype()).isNotEmpty();
    }

}
