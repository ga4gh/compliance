package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import ga4gh.Common.GAException;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Variants.*;
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
    public void checkVariantsGetResultsMatchSearchResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfVariants = 6;

        final String variantSetId = Utils.getVariantSetId(client);
        final List<Variant> variants = Utils.getAllVariantsInRange(client, variantSetId, start, end);

        assertThat(variants).hasSize(expectedNumberOfVariants);

        for (final Variant variantFromSearch : variants) {
            final Variant variantFromGet = client.variants.getVariant(variantFromSearch.getId());
            assertThat(variantFromGet).isNotNull();

            assertThat(variantFromGet).isEqualTo(variantFromSearch);
        }

    }


}
