package org.ga4gh.cts.api.peers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.PeerServiceOuterClass.*;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests for the BioMetadata endpoints
 */
@Category(PeersTests.class)
public class PeersIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Tests the /peers/list endpoint to ensure a well formed peer (1kgenomes.ga4gh.org) is returned.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkListPeers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final ListPeersRequest req =
                ListPeersRequest.newBuilder()
                        .build();

        final ListPeersResponse resp = client.peers.listPeers(req);
        assertThat(resp.getPeersCount()).isGreaterThan(0);
    }

    /**
    /**
     * Tests that for each Individual returned via search there exists an
     * equivalent record received by GET individuals/id.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
//    @Test
//    public void checkGetIndividual() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
//        final SearchIndividualsRequest req =
//                SearchIndividualsRequest.newBuilder()
//                        .setDatasetId(TestData.getDatasetId())
//                        .build();
//
//        final SearchIndividualsResponse resp = client.bioMetadata.searchIndividuals(req);
//        assertThat(resp).isNotNull();
//        for (Individual i : resp.getIndividualsList()) {
//            final Individual found = client.bioMetadata.getIndividual(i.getId());
//            assertThat(found).isEqualTo(i);
//        }
//    }

}
