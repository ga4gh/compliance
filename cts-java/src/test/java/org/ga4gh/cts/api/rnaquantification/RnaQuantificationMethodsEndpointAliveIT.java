package org.ga4gh.cts.api.rnaquantification;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.models.RnaQuantification;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verify basic operations on rnaquantifications.
 */
@Category(RnaQuantificationTests.class)
public class RnaQuantificationMethodsEndpointAliveIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that searching {@link CallSet}s with the ID of a nonexistent {@link RnaQuantification} fails.
     *
     * @throws Exception if something goes wrong
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testSearchCallSetsForNonexistentRnaQuantificationFails() throws Exception {
        final SearchCallSetsRequest scsr =
                SearchCallSetsRequest.newBuilder()
                                     .setRnaQuantificationId(Utils.randomId())
                                     .build();

        final GAWrapperException gae =
                Utils.catchGAWrapperException(() -> client.rnaquantification.searchCallSets(scsr));

        assertThat(gae.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

}
