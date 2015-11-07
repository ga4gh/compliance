package org.ga4gh.cts.api.variants;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchCallSetsRequest;
import org.ga4gh.models.CallSet;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verify basic operations on variants, variantsets, and callsets.
 */
@Category(VariantsTests.class)
public class VariantsMethodsEndpointAliveIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that searching {@link CallSet}s with the ID of a nonexistent {@link VariantSet} fails.
     *
     * @throws Exception if something goes wrong
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testSearchCallSetsForNonexistentVariantSetFails() throws Exception {
        final SearchCallSetsRequest scsr =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(Utils.randomId())
                                     .build();

        final GAWrapperException gae =
                Utils.catchGAWrapperException(() -> client.variants.searchCallSets(scsr));

        assertThat(gae.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

}
