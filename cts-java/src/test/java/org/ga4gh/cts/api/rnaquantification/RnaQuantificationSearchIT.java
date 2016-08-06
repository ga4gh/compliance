package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;

import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;

import ga4gh.RnaQuantificationServiceOuterClass.SearchRnaQuantificationsRequest;
import ga4gh.RnaQuantificationServiceOuterClass.SearchRnaQuantificationsResponse;
import ga4gh.RnaQuantificationOuterClass.RnaQuantification;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /rnaquantifications/search</tt>.
 */
@Category(RnaQuantificationTests.class)
@RunWith(JUnitParamsRunner.class)
public class RnaQuantificationSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

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
        final int expectedNumberOfRnaQuantifications = 1;
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setRnaQuantificationSetId(rnaQuantificationSetId)
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuants = resp.getRnaQuantificationsList();
        assertThat(rnaQuants).hasSize(expectedNumberOfRnaQuantifications);
    }

}
