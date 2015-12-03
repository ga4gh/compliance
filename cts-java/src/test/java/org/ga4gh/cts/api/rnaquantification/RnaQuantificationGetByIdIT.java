package org.ga4gh.cts.api.rnaquantification;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchRnaQuantificationRequest;
import org.ga4gh.models.Rnaquantification;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /rnaquantification/{id}</tt>.
 */
@Category(RnaQuantificationTests.class)
@RunWith(JUnitParamsRunner.class)
public class RnaQuantificationGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that RnaQuantifications that we obtain by way of {@link SearchRnaQuantificationRequest} match the ones
     * we get via <tt>GET /rnaquantification/{id}</tt>.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkRnaQuantificationGetResultsMatchSearchResults() throws AvroRemoteException {
        final int expectedNumberOfRnaQuantifications = 1;

        final String rnaQuantificationId = Utils.getRnaQuantificationSetId(client);
        final List<RnaQuantifications> rnaQuantifications = Utils.getRnaQuantification(client, rnaQuantificationId);

        assertThat(rnaQuantifications).hasSize(expectedNumberOfRnaQuantifications);

        for (final RnaQuantifications rnaQuantificationFromSearch : rnaQuantifications) {
            final RnaQuantifications rnaQuantificationFromGet = client.rnaquantifications.getRnaQuantification(rnaQuantificationFromSearch.getId());
            assertThat(rnaQuantificationFromGet).isNotNull();

            assertThat(rnaQuantificationFromGet).isEqualTo(rnaQuantificationFromSearch);
        }

    }


}
