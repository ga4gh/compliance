package org.ga4gh.cts.api.rnaquantification;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.RnaQuantificationOuterClass.*;
import ga4gh.RnaQuantificationServiceOuterClass.*;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /rnaquantificationsets/{id}</tt>.
 */
@Category(RnaQuantificationTests.class)
@RunWith(JUnitParamsRunner.class)
public class RnaQuantificationSetsIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Verify that RnaQuantifications that we obtain by way of {@link ga4gh.RnaQuantificationServiceOuterClass.SearchRnaQuantificationSetsRequest} match the ones
     * we get via <tt>GET /rnaquantificationsets/{id}</tt>.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkRnaQuantificationSetGetResultsMatchSearchResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String rnaQuantificationSetId = Utils.getRnaQuantificationSetId(client);
        final RnaQuantificationSet rnaQuantificationSetFromGet = client.rnaquantifications.getRnaQuantificationSet(rnaQuantificationSetId);
        assertThat(rnaQuantificationSetFromGet).isNotNull();
        assertThat(rnaQuantificationSetFromGet.getId()).isEqualTo(rnaQuantificationSetId);
    }

    /**
     *
     * Checks that RNA Quantification Sets can be discovered by dataset ID.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkRnaQuantificationSetSearch() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String datasetId = Utils.getAllDatasets(client).get(0).getId();
        final SearchRnaQuantificationSetsRequest req =
                SearchRnaQuantificationSetsRequest.newBuilder()
                    .setDatasetId(datasetId).build();


        final SearchRnaQuantificationSetsResponse resp = client.rnaquantifications.searchRnaQuantificationSets(req);
        assertThat(resp.getRnaQuantificationSetsCount()).isGreaterThan(0);
    }



}
