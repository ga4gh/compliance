package org.ga4gh.cts.api.peers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.PeerServiceOuterClass;
import ga4gh.PeerServiceOuterClass.ListPeersRequest;
import ga4gh.PeerServiceOuterClass.ListPeersResponse;
import ga4gh.PeerServiceOuterClass.GetInfoResponse;
import ga4gh.PeerServiceOuterClass.GetInfoRequest;
import ga4gh.PeerServiceOuterClass.AnnouncePeerRequest;
import ga4gh.PeerServiceOuterClass.AnnouncePeerResponse;
import ga4gh.PeerServiceOuterClass.Peer;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

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
     * Tests that the service will receive a well formed announce message without throwing an error.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkValidAnnounce() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final Peer peer = Peer.newBuilder().setUri("http://1kgenomes.ga4gh.org").build();
        final AnnouncePeerRequest req =
                AnnouncePeerRequest.newBuilder()
                        .setPeer(peer)
                        .build();

        final AnnouncePeerResponse resp = client.peers.announcePeer(req);
        assertThat(resp).isNotNull();
        assertThat(resp.getSuccess()).isTrue();
    }

    /**
     * Tests that the service returns an expected info response at the /info endpoint including the protocol version.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkGetInfo() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final GetInfoResponse resp = client.peers.getInfo();
        assertThat(resp).isNotNull();
        assertThat(resp.getProtocolVersion()).isNotEmpty();
    }

}
