package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchVariantsRequest;
import org.ga4gh.models.Variant;
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
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfVariants = 6;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> variants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        assertThat(variants).hasSize(expectedNumberOfVariants);

        for (final Variant variantFromSearch : variants) {
            final Variant variantFromGet = client.variants.getVariant(variantFromSearch.getId());
            assertThat(variantFromGet).isNotNull();

            // XXX fails due to server issue #619
            assertThat(variantFromGet).isEqualTo(variantFromSearch);
        }

    }


}
