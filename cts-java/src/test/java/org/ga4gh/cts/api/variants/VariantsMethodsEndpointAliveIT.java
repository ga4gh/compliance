package org.ga4gh.cts.api.variants;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchCallSetsRequest;
import org.ga4gh.methods.SearchVariantsRequest;
import org.ga4gh.models.CallSet;
import org.ga4gh.models.Variant;
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
     * Check that searching {@link Variant}s with a nonexistent reference name fails.
     *
     * @throws Exception if something goes wrong
     */
    //@Ignore
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testSearchVariantsForNonexistentReferenceFails() throws Exception {
        final SearchVariantsRequest request =
                SearchVariantsRequest.newBuilder()
                                     .setReferenceName(Utils.randomName())
                                     .setVariantSetId(Utils.randomId())
                                     .setStart(0L)
                                     .setEnd(1L)
                                     .build();

        final GAWrapperException gae =
                Utils.catchGAWrapperException(() -> client.variants.searchVariants(request));

        assertThat(gae.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Check that searching {@link CallSet}s with a nonexistent {@link VariantSet} ID fails.
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
