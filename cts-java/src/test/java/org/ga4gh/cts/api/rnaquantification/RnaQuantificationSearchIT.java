package org.ga4gh.cts.api.rnaquantification;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchRnaQuantificationRequest;
import org.ga4gh.methods.SearchRnaQuantificationResponse;
import org.ga4gh.methods.SearchExpressionLevelRequest;
import org.ga4gh.methods.SearchExpressionLevelResponse;
import org.ga4gh.methods.SearchFeatureGroupRequest;
import org.ga4gh.methods.SearchFeatureGroupResponse;
import org.ga4gh.models.RnaQuantification;
import org.ga4gh.models.ExpressionLevel;
import org.ga4gh.models.FeatureGroup;
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
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    private String getTestDataRnaQuantificationId() throws AvroRemoteException {
        final SearchRnaQuantificationRequest req =
                SearchRnaQuantificationRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantification();
        assertThat(rnaQuants).isNotEmpty();
        return rnaQuants.get(0).getId();
    }

    /**
     * Fetch rna quantifications and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfRnaQuantifications() throws AvroRemoteException {
        final int expectedNumberOfRnaQuantifications = 4;

        final SearchRnaQuantificationRequest req =
                SearchRnaQuantificationRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantification();
        assertThat(rnaQuants).hasSize(expectedNumberOfRnaQuantifications);
    }

    /**
     * Fetch expression levels and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfExpressionLevels() throws AvroRemoteException {
        final int expectedNumberOfExpressionLevels = 4;

        final SearchExpressionLevelRequest req =
                SearchExpressionLevelRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchExpressionLevelResponse resp = client.rnaquantifications.searchExpressionLevel(req);

        final List<ExpressionLevel> expressionLevels = resp.getExpressionLevel();
        assertThat(expressionLevels).hasSize(expectedNumberOfExpressionLevels);
    }

    /**
     * Fetch feature groups and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfFeatureGroups() throws AvroRemoteException {
        final int expectedNumberOfFeatureGroups = 4;

        final SearchFeatureGroupRequest req =
                SearchFeatureGroupRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchFeatureGroupResponse resp = client.rnaquantifications.searchFeatureGroup(req);

        final List<FeatureGroup> featureGroups = resp.getFeatureGroup();
        assertThat(featureGroups).hasSize(expectedNumberOfFeatureGroups);
    }
}
