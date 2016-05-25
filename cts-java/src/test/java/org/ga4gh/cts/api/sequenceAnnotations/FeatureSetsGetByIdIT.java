package org.ga4gh.cts.api.sequenceAnnotations;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import ga4gh.SequenceAnnotations.FeatureSet;
import junitparams.JUnitParamsRunner;

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
     * Verify that FeatureSets that we obtain by way of {@link ga4gh.SequenceAnnotationServiceOuterClass.SearchFeatureSetsRequest }
     * match the ones we get via <tt>GET /featureSets/{id}</tt>.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFeatureSetsGetResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
