package org.ga4gh.cts.api.sequenceAnnotations;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.sequenceAnnotations.SequenceAnnotationTests;
import org.ga4gh.methods.*;
import org.ga4gh.models.Feature;
import org.ga4gh.models.FeatureSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /features/{id}</tt>.
 *
 * @author Herb Jellinek
 */
@Category(SequenceAnnotationTests.class)
@RunWith(JUnitParamsRunner.class)
public class FeaturesGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that Features that we obtain by way of {@link SearchFeaturesRequest} match the ones
     * we get via <tt>GET /features/{id}</tt>.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesGetResultsMatchSearchResults() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;
        final String parentId = "";
        final int expectedNumberOfFeatures = 19;

        final String featureSetId = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest vReq =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(featureSetId)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId)
                        .build();
        final SearchFeaturesResponse vResp = client.sequenceAnnotations.searchFeatures(vReq);
        final List<Feature> features = vResp.getFeatures();

        assertThat(features).hasSize(expectedNumberOfFeatures);

        for (final Feature featureFromSearch : features) {
            final Feature featureFromGet = client.sequenceAnnotations.getFeature(featureFromSearch.getId());
            assertThat(featureFromGet).isNotNull();

            assertThat(featureFromGet).isEqualTo(featureFromSearch);
        }

    }
}
