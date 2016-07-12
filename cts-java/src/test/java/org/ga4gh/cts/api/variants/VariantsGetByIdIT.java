package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.VariantServiceOuterClass.SearchVariantsRequest;
import ga4gh.Variants.Variant;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
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
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkVariantsGetResultsMatchSearchResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfVariants = 6;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> variants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        assertThat(variants).hasSize(expectedNumberOfVariants);

        for (final Variant variantFromSearch : variants) {
            Variant variantFromGet = client.variants.getVariant(variantFromSearch.getId());
            assertThat(variantFromGet).isNotNull();

            // variantFromSearch will not contain any callsets, but variantFromGet will.
            // So we need to wipe out the callsets before doing the comparison.
            variantFromGet = variantFromGet.toBuilder().clearCalls().build();

            assertThat(variantFromGet).isEqualTo(variantFromSearch);
        }

    }


}
