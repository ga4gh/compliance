package org.ga4gh.cts.api.rnaquantification;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchRnaQuantificationsRequest;
import org.ga4gh.methods.SearchRnaQuantificationsResponse;
import org.ga4gh.methods.SearchExpressionLevelsRequest;
import org.ga4gh.methods.SearchExpressionLevelsResponse;
import org.ga4gh.methods.SearchQuantificationGroupsRequest;
import org.ga4gh.methods.SearchQuantificationGroupsResponse;
import org.ga4gh.models.RnaQuantification;
import org.ga4gh.models.ExpressionLevel;
import org.ga4gh.models.QuantificationGroup;
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
        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaQuantifications.searchRnaQuantifications(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantifications();
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

        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaQuantifications.searchRnaQuantifications(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantifications();
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

        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaQuantifications.searchExpressionLevels(req);

        final List<ExpressionLevel> expressionLevels = resp.getExpressionLevels();
        assertThat(expressionLevels).hasSize(expectedNumberOfExpressionLevels);
    }

    /**
     * Fetch quantification groups and count them.  The number must
     * equal what we're expecting by visual examination of the data.
     *
     * @throws AvroRemoteException if there's a communication problem or
     * server exception ({@link GAException})
     */
    @Test
    public void checkExpectedNumberOfQuantificationGroups() throws AvroRemoteException {
        final int expectedNumberOfQuantificationGroups = 4;

        final SearchQuantificationGroupsRequest req =
                SearchQuantificationGroupsRequest.newBuilder()
                        .setRnaQuantificationId(getTestDataRnaQuantificationId())
                        .build();
        final SearchQuantificationGroupsResponse resp = client.rnaQuantifications.searchQuantificationGroups(req);

        final List<QuantificationGroup> quantificationGroups = resp.getQuantificationGroups();
        assertThat(quantificationGroups).hasSize(expectedNumberOfQuantificationGroups);
    }
}
