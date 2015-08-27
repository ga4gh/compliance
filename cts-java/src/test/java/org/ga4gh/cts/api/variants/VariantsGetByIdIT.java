package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.*;
import org.ga4gh.models.Variant;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /variants/{id}</tt>.
 *
 * @author Herb Jellinek
 */
@Category(VariantsTests.class)
@RunWith(JUnitParamsRunner.class)
public class VariantsGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that Variants that we obtain by way of {@link SearchVariantsRequest} match the ones
     * we get via <tt>GET /variants/{id}</tt>.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkVariantsGetResultsMatchSearchResults() throws AvroRemoteException {
        final long start = 60156;
        final long end = 60383;
        final String referenceName = "3";

        // first get a variant set ID
        final SearchVariantSetsRequest searchVariantSetsReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse searchVariantSetsResp =
                client.variants.searchVariantSets(searchVariantSetsReq);

        final List<VariantSet> variantSets = searchVariantSetsResp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        final String variantSetId = variantSets.get(0).getId();

        // then gather the variants that are part of the VariantSet with that ID
        final SearchVariantsRequest req =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .setReferenceName(referenceName)
                                     .setStart(start)
                                     .setEnd(end)
                                     .build();
        final SearchVariantsResponse resp = client.variants.searchVariants(req);

        final List<Variant> variants = resp.getVariants();
        assertThat(variants).isNotEmpty();

        for (final Variant variantFromSearch : variants) {
            final Variant variantFromGet = client.variants.getVariant(variantFromSearch.getId());
            assertThat(variantFromGet).isNotNull();

            assertThat(variantFromGet).isEqualTo(variantFromSearch);
        }

    }


}
