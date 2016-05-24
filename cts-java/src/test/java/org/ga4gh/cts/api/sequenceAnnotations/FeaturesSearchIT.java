package org.ga4gh.cts.api.sequenceAnnotations;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.function.Consumer;

import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesRequest;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesResponse;
import ga4gh.SequenceAnnotations.Feature;

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */

    @Test
    public void checkExpectedNumberOfFeatures() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
        final List<Feature> searchFeatures = fResp.getFeaturesList();

        assertThat(searchFeatures).hasSize(expectedNumberOfFeatures);
    }

    /**
     * Check that the features returned from a search by parentId return as expected.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFeaturesSearchByParentId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 0;
        final long end = 100000000;
        final int expectedNumberOfFeatures = 50;

        final String id = Utils.getFeatureSetId(client);

        // first search: obtain the ID of the first transcript in the test range.
        final String parentId1 = "";
        final String featureType1 = "transcript";

        final SearchFeaturesRequest fReq1 =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(id)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId1)
                        .addFeatureTypes(featureType1)
                        .build();
        final SearchFeaturesResponse fResp1 = client.sequenceAnnotations.searchFeatures(fReq1);
        final List<Feature> searchFeatures = fResp1.getFeaturesList();

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
        final List<Feature> searchFeatures2 = fResp2.getFeaturesList();

        assertThat(searchFeatures2).hasSize(expectedNumberOfFeatures);
        checkAllFeatures(searchFeatures2, f -> assertThat(
                f.getParentId()).isEqualTo(parentId2));
    }

    /**
     * Check that the features returned from a search by ontologyTerm return as expected.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFeaturesSearchByFeatureType() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 0;
        final long end = 100000000;
        final String parentId = "";
        final String featureType = "gene";
        final int expectedNumberOfFeatures = 2;

        final String id = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                                     .setFeatureSetId(id)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setParentId(parentId)
                                     .addFeatureTypes(featureType)
                                     .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final List<Feature> searchFeatures = fResp.getFeaturesList();

        assertThat(searchFeatures).hasSize(expectedNumberOfFeatures);
        checkAllFeatures(searchFeatures, f -> assertThat(
                f.getFeatureType().getTerm()).isEqualTo(featureType));

    }

}
