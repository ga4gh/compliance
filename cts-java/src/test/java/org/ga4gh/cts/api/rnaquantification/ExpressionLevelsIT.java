package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
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

}
