package org.ga4gh.cts.api.sequenceAnnotations;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.sequenceAnnotations.SequenceAnnotationTests;
import org.ga4gh.methods.*;
import org.ga4gh.models.Call;
import org.ga4gh.models.Feature;
import org.ga4gh.models.FeatureSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test searching features.
 */
@Category(SequenceAnnotationTests.class)
public class FeaturesSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * For every {@link Feature} in the {@link List}, call the {@link Consumer}.
     * @param features the list to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllFeatures(List<Feature> features, Consumer<Feature> cons) {
        features.stream().forEach(cons::accept);
    }

    /**
     * Fetch features between two positions in the reference and count them.  The number must
     * equal what we're expecting by visual examination of the features data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfFeatures() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;
        final int expectedNumberOfFeatures = 19;
        final String parentId = "";

        final SearchFeatureSetsRequest req =
                SearchFeatureSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchFeatureSetsResponse resp = client.sequenceAnnotations.searchFeatureSets(req);

        final List<FeatureSet> featureSets = resp.getFeatureSets();
        assertThat(featureSets).isNotEmpty();
        final String id = featureSets.get(0).getId();

        final SearchFeaturesRequest vReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .build();
        final SearchFeaturesResponse vResp = client.sequenceAnnotations.searchFeatures(vReq);
        final List<Feature> searchFeatures = vResp.getFeatures();

        assertThat(searchFeatures).hasSize(expectedNumberOfFeatures);
    }

    /**
     * Check that the features we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.SequenceAnnotations#searchFeatures(SearchFeaturesRequest)}
     * search contain the expected reference name.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesForExpectedReferenceName() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;
        final String parentId = "";

        final SearchFeatureSetsRequest req =
                SearchFeatureSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchFeatureSetsResponse resp = client.sequenceAnnotations.searchFeatureSets(req);

        final List<FeatureSet> featureSets = resp.getFeatureSets();
        assertThat(featureSets).isNotEmpty();
        final String id = featureSets.get(0).getId();

        final SearchFeaturesRequest vReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .build();
        final SearchFeaturesResponse vResp = client.sequenceAnnotations.searchFeatures(vReq);
        final List<Feature> searchFeatures = vResp.getFeatures();

        checkAllFeatures(searchFeatures, v -> assertThat(v.getReferenceName()).isEqualTo(TestData.REFERENCE_NAME));
    }

    /**
     * Check that the features we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.SequenceAnnotations#searchFeatures(SearchFeaturesRequest)}
     * search are well formed.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesForWellFormedness() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;
        String parentId = "";

        final SearchFeatureSetsRequest req =
                SearchFeatureSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchFeatureSetsResponse resp = client.sequenceAnnotations.searchFeatureSets(req);

        final List<FeatureSet> featureSets = resp.getFeatureSets();
        assertThat(featureSets).isNotEmpty();
        final String id = featureSets.get(0).getId();

        final SearchFeaturesRequest vReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .build();
        final SearchFeaturesResponse vResp = client.sequenceAnnotations.searchFeatures(vReq);
        final List<Feature> searchFeatures = vResp.getFeatures();
    }

}
