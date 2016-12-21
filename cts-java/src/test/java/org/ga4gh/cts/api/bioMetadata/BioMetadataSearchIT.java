package org.ga4gh.cts.api.bioMetadata;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.BioMetadataServiceOuterClass.SearchBiosamplesRequest;
import ga4gh.BioMetadataServiceOuterClass.SearchBiosamplesResponse;
import ga4gh.BioMetadataServiceOuterClass.SearchIndividualsRequest;
import ga4gh.BioMetadataServiceOuterClass.SearchIndividualsResponse;
import ga4gh.BioMetadata.*;
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
@Category(BioMetadataTests.class)
public class BioMetadataSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Tests the /biosamples/search endpoint to ensure that searching
     * by name returns properly formed results.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchBiosamplesByName() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // search biosamples for a known name
        final SearchBiosamplesRequest req =
                SearchBiosamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.BIOSAMPLE_NAME)
                        .build();

        final SearchBiosamplesResponse resp = client.bioMetadata.searchBiosamples(req);
        assertThat(resp.getBiosamplesCount()).isGreaterThan(0);
        for (Biosample b : resp.getBiosamplesList()) {
            assertThat(b.getName()).isEqualTo(TestData.BIOSAMPLE_NAME);
            assertThat(b.getId()).isNotEmpty();
        }
    }

    /**
     * Tests the /biosamples/search endpoint to ensure that searching
     * by individual ID returns properly formed results.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchBiosamplesByIndividual() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // search biosamples for a known name
        final SearchIndividualsRequest ireq =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.INDIVIDUAL_NAME)
                        .build();

        final SearchIndividualsResponse iresp = client.bioMetadata.searchIndividuals(ireq);
        final String individualId = iresp.getIndividualsList().get(0).getId();

        final SearchBiosamplesRequest req =
                SearchBiosamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setIndividualId(individualId)
                        .build();

        final SearchBiosamplesResponse resp = client.bioMetadata.searchBiosamples(req);
        assertThat(resp).isNotNull();
        for (Biosample b : resp.getBiosamplesList()) {
            assertThat(b.getIndividualId()).isEqualTo(individualId);
        }
    }
    /**
     * Tests that for each Biosample returned via search there exists an
     * equivalent record received by GET biosamples/id.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkGetBiosample() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchBiosamplesRequest req =
                SearchBiosamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();

        final SearchBiosamplesResponse resp = client.bioMetadata.searchBiosamples(req);
        assertThat(resp).isNotNull();
        for (Biosample b : resp.getBiosamplesList()) {
            final Biosample found = client.bioMetadata.getBiosample(b.getId());
            assertThat(found).isEqualTo(b);
        }
    }

    /**
     * Tests the /individuals/search endpoint to ensure that searching
     * by name returns properly formed results.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchIndividualsByName() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchIndividualsRequest req =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.INDIVIDUAL_NAME)
                        .build();

        final SearchIndividualsResponse resp = client.bioMetadata.searchIndividuals(req);
        assertThat(resp).isNotNull();
        for (Individual i : resp.getIndividualsList()) {
            assertThat(i.getName()).isEqualTo(TestData.INDIVIDUAL_NAME);
            assertThat(i.getId()).isNotEmpty();
        }
    }
    /**
     * Tests that for each Individual returned via search there exists an
     * equivalent record received by GET individuals/id.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkGetIndividual() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchIndividualsRequest req =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();

        final SearchIndividualsResponse resp = client.bioMetadata.searchIndividuals(req);
        assertThat(resp).isNotNull();
        for (Individual i : resp.getIndividualsList()) {
            final Individual found = client.bioMetadata.getIndividual(i.getId());
            assertThat(found).isEqualTo(i);
        }
    }

}
