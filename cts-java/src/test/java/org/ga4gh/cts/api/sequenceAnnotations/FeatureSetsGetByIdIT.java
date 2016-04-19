package org.ga4gh.cts.api.sequenceAnnotations;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.models.FeatureSet;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchFeatureSetsRequest;
import org.ga4gh.methods.SearchFeatureSetsResponse;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /featureSets/{id}</tt>.
 *
 * @author Maciek Smuga-Otto
 */
@Category(SequenceAnnotationTests.class)
@RunWith(JUnitParamsRunner.class)
public class FeatureSetsGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that FeatureSets that we obtain by way of {@link org.ga4gh.methods.SearchFeatureSetsRequest}
     * match the ones we get via <tt>GET /featureSets/{id}</tt>.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeatureSetsGetResults() throws AvroRemoteException {
        final int expectedNumberOfFeatureSets = 1;

        final List<FeatureSet> featureSets = Utils.getAllFeatureSets(client);

        assertThat(featureSets).hasSize(expectedNumberOfFeatureSets);

        for (final FeatureSet featureSetFromSearch : featureSets) {
            final FeatureSet featureSetFromGet = client.sequenceAnnotations.getFeatureSet(featureSetFromSearch.getId());
            assertThat(featureSetFromGet).isNotNull();

            assertThat(featureSetFromGet).isEqualTo(featureSetFromSearch);
        }

    }


}
