package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.RnaQuantificationServiceOuterClass;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.cts.api.Utils;
import ga4gh.RnaQuantificationOuterClass.ExpressionLevel;
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
        final int expectedNumberOfExpressionLevels = 4;
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final String rnaQuantificationId = Utils.getRnaQuantificationId(client, rnaQuantificationSetId);

        final RnaQuantificationServiceOuterClass.SearchExpressionLevelsRequest req =
                RnaQuantificationServiceOuterClass.SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .build();
        final RnaQuantificationServiceOuterClass.SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);

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
        // check filter by feature ids
        // get an rna quant
        // search expression levels without filter, assert they don't have the same feature ID
        // pick a feature ID from the search results
        // search expression levels with filter ID filter, assert they have the right feature ID

        // pick 2 different feature IDs
        // search expression levels with filter IDs filter, assert the results are from the request

        // check filter by threshold
        // get an rna quant
        // search expression levels without a threshold and keep that number
        // search expression levels with a threshold and assert that none returned are over threshold
        // assert filtered response less than total response
    }




}
