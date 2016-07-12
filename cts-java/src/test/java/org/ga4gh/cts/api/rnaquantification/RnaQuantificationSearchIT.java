package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;

import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;

import ga4gh.RnaQuantificationServiceOuterClass.SearchRnaQuantificationsRequest;
import ga4gh.RnaQuantificationServiceOuterClass.SearchRnaQuantificationsResponse;
import ga4gh.RnaQuantificationServiceOuterClass.SearchExpressionLevelsRequest;
import ga4gh.RnaQuantificationServiceOuterClass.SearchExpressionLevelsResponse;
import ga4gh.RnaQuantificationServiceOuterClass.SearchQuantificationGroupsRequest;
import ga4gh.RnaQuantificationServiceOuterClass.SearchQuantificationGroupsResponse;
import ga4gh.RnaQuantificationOuterClass.RnaQuantification;
import ga4gh.RnaQuantificationOuterClass.ExpressionLevel;
import ga4gh.RnaQuantificationOuterClass.QuantificationGroup;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /rnaquantification/search</tt>.
 */
@Category(RnaQuantificationTests.class)
@RunWith(JUnitParamsRunner.class)
public class RnaQuantificationSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Helper function to get an rnaQuantificationId from the dataset.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private String getTestDataRnaQuantificationId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantificationsList();
        assertThat(rnaQuants).isNotEmpty();
        return rnaQuants.get(0).getId();
    }

    /**
     * Fetch rna quantifications and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpectedNumberOfRnaQuantifications() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final int expectedNumberOfRnaQuantifications = 4;

        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantificationsList();
        assertThat(rnaQuants).hasSize(expectedNumberOfRnaQuantifications);
    }

    /**
     * Fetch expression levels and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpectedNumberOfExpressionLevels() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final int expectedNumberOfExpressionLevels = 4;

        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);

        final List<ExpressionLevel> expressionLevels = resp.getExpressionLevelsList();
        assertThat(expressionLevels).hasSize(expectedNumberOfExpressionLevels);
    }

    /**
     * Fetch feature groups and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpectedNumberOfFeatureGroups() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final int expectedNumberOfFeatureGroups = 4;

        final SearchQuantificationGroupsRequest req =
                SearchQuantificationGroupsRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchQuantificationGroupsResponse resp = client.rnaquantifications.searchFeatureGroup(req);

        final List<QuantificationGroup> featureGroups = resp.getQuantificationGroupsList();
        assertThat(featureGroups).hasSize(expectedNumberOfFeatureGroups);
    }
}
