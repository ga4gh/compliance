package org.ga4gh.cts.api.sequenceAnnotations;

import java.util.ArrayList;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.sequenceAnnotations.SequenceAnnotationTests;
import org.ga4gh.methods.*;
import org.ga4gh.cts.api.Utils;
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
        final long start = 62162;
        final long end = 62239;
        final String parentId = "";
        final int expectedNumberOfFeatures = 69;

        final String id = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final List<Feature> searchFeatures = fResp.getFeatures();

        assertThat(searchFeatures).hasSize(expectedNumberOfFeatures);
    }

    /**
     * Check that the features returned from a search by parentId return as expected.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesSearchByParentId() throws AvroRemoteException {
        final long start = 0;
        final long end = 100000000;
        final int expectedNumberOfFeatures = 20;

        final String id = Utils.getFeatureSetId(client);

        // first search: obtain the ID of the first transcript in the test range.
        final String parentId1 = "";
        final String featureType1 = "transcript";
        final List<String> featureTypes1 = new ArrayList<String>(){{add(featureType1);}};

        final SearchFeaturesRequest fReq1 =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(id)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId1)
                        .setFeatureTypes(featureTypes1)
                        .build();
        final SearchFeaturesResponse fResp1 = client.sequenceAnnotations.searchFeatures(fReq1);
        final List<Feature> searchFeatures = fResp1.getFeatures();

        final String parentId2 = searchFeatures.get(0).getId();

        // second search: obtain the direct children of this transcript:
        final SearchFeaturesRequest fReq2 =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId2)
                                     .build();
        final SearchFeaturesResponse fResp2 = client.sequenceAnnotations.searchFeatures(fReq2);
        final List<Feature> searchFeatures2 = fResp2.getFeatures();

        assertThat(searchFeatures2).hasSize(expectedNumberOfFeatures);
        checkAllFeatures(searchFeatures2, f -> assertThat(
                f.getParentId()).isEqualTo(parentId2));
    }

    /**
     * Check that the features returned from a search by ontologyTerm return as expected.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesSearchByFeatureType() throws AvroRemoteException {
        final long start = 0;
        final long end = 100000000;
        final String parentId = "";
        final String featureType = "gene";
        final List<String> featureTypes = new ArrayList<String>(){{add(featureType);}};
        final int expectedNumberOfFeatures = 2;

        final String id = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .setFeatureTypes(featureTypes)
                                     .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final List<Feature> searchFeatures = fResp.getFeatures();

        assertThat(searchFeatures).hasSize(expectedNumberOfFeatures);
        checkAllFeatures(searchFeatures, f -> assertThat(
                f.getFeatureType().getTerm()).isEqualTo(featureType));

    }

}
