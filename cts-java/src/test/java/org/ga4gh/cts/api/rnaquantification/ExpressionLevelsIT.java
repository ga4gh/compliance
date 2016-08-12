package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.RnaQuantificationServiceOuterClass.*;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.cts.api.Utils;
import ga4gh.RnaQuantificationOuterClass.ExpressionLevel;
import ga4gh.SequenceAnnotationServiceOuterClass.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /expressionlevels/{id}</tt>.
 */
@Category(RnaQuantificationTests.class)
@RunWith(JUnitParamsRunner.class)
public class ExpressionLevelsIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that ExpressionLevels that we obtain by way of {@link ga4gh.RnaQuantificationServiceOuterClass.SearchExpressionLevelsRequest} match the ones
     * we get via <tt>GET /expressionlevels/{id}</tt>.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpressionLevelGetResultsMatchSearchResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);
        final String expressionLevelId = Utils.getExpressionLevelId(client, rnaQuantificationId);
        final ExpressionLevel expressionLevelFromGet = client.rnaquantifications.getExpressionLevel(expressionLevelId);
        assertThat(expressionLevelFromGet).isNotNull();
        assertThat(expressionLevelFromGet.getId()).isEqualTo(expressionLevelId);
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
        final int expectedNumberOfExpressionLevels = 6;  // The threshold defaults to 0 and removes 2 values
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);

        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .setPageSize(100)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);

        final List<ExpressionLevel> expressionLevels = resp.getExpressionLevelsList();
        assertThat(expressionLevels).hasSize(expectedNumberOfExpressionLevels);
    }

    /**
     * Search expression levels using feature ID filtering. Ensures that returned
     * expression levels have the appropriate feature IDs.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFilterByFeatureIds() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);
        // Search expression levels without filter
        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);
        assertThat(resp.getExpressionLevelsCount()).isGreaterThan(0);

        final String featureId = resp.getExpressionLevelsList().get(0).getFeatureId();
        // Pick a feature ID from the search results
        // Search expression levels with filter ID filter, assert they have the right feature ID
        final SearchExpressionLevelsRequest filterReq =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .addFeatureIds(featureId)
                        .build();
        final SearchExpressionLevelsResponse filterResp = client.rnaquantifications.searchExpressionLevel(filterReq);

        assertThat(filterResp.getExpressionLevelsCount()).isGreaterThan(0);
        filterResp.getExpressionLevelsList()
                .stream()
                .forEach(expressionLevel -> assertThat(expressionLevel.getFeatureId()).isEqualTo(featureId));


        // pick 2 different feature IDs
        // search expression levels with filter IDs filter, assert the results are from the request

    }

    /**
     * Search expression levels using threshold filters.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFilterByThreshold() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);
        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);
        assertThat(resp.getExpressionLevelsCount()).isGreaterThan(0);

        // Pick a threshold from the search results
        float threshold = resp.getExpressionLevelsList().get(0).getExpression();

        // Search expression levels with threshold filter
        final SearchExpressionLevelsRequest filterReq =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .setThreshold(threshold)
                        .build();
        final SearchExpressionLevelsResponse filterResp = client.rnaquantifications.searchExpressionLevel(filterReq);

        assertThat(filterResp.getExpressionLevelsCount()).isLessThan(resp.getExpressionLevelsCount());
        assertThat(filterResp.getExpressionLevelsCount()).isGreaterThan(0);

        filterResp.getExpressionLevelsList()
                .stream()
                .forEach(expressionLevel -> assertThat(expressionLevel.getExpression()).isGreaterThan(threshold));
    }

    /**
     * Check paging through expression levels.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpressionLevelPaging() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);
        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .setPageSize(1)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);
        assertThat(resp.getExpressionLevelsCount()).isGreaterThan(0);
        assertThat(resp.getNextPageToken()).isNotEmpty();

        // Pick a threshold from the search results
        float threshold = resp.getExpressionLevelsList().get(0).getExpression();

        // Search expression levels with threshold filter
        final SearchExpressionLevelsRequest req2 =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .setPageToken(resp.getNextPageToken())
                        .build();
        final SearchExpressionLevelsResponse resp2 = client.rnaquantifications.searchExpressionLevel(req2);

        // Assert they are unique
        assertThat(resp.getExpressionLevels(0).getId()).isNotEqualTo(resp2.getExpressionLevels(0).getId());

    }
    /**
     * Check feature records exist for any feature IDs listed in expression level records.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFeatureForExpression() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);
        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);
        assertThat(resp.getExpressionLevelsCount()).isGreaterThan(0);
        for (ExpressionLevel expressionLevel: resp.getExpressionLevelsList()) {
            assertThat(client.sequenceAnnotations.getFeature(expressionLevel.getFeatureId()).getId()).isEqualTo(expressionLevel.getFeatureId());
        }
        // for every returned expression level get a feature record
        // assert that the feature returned and expression level feature ID equate
    }
}
