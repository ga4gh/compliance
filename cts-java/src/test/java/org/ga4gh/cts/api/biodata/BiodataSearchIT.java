package org.ga4gh.cts.api.biodata;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.BioSample;
import org.ga4gh.models.Individual;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the BioData endpoint
 */
@Category(BiodataTests.class)
public class BiodataSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Tests the /biosamples/search endpoint to ensure that searching
     * by name returns properly formed results.
     * @throws AvroRemoteException
     */
    @Test
    public void checkSearchBioSamplesByName() throws AvroRemoteException {
        // search biosamples for a known name
        final SearchBioSamplesRequest req =
                SearchBioSamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.BIOSAMPLE_NAME)
                        .build();

        final SearchBioSamplesResponse resp = client.biodata.searchBiosamples(req);
        assertThat(resp).isNotNull();
        for (BioSample b : resp.getBiosamples()) {
            assertThat(b.getName()).isEqualTo(TestData.BIOSAMPLE_NAME);
            assertThat(b.getId()).isNotEmpty();
        }
    }

    /**
     * Tests the /biosamples/search endpoint to ensure that searching
     * by individual ID returns properly formed results.
     * @throws AvroRemoteException
     */
    @Test
    public void checkSearchBioSamplesByIndividual() throws AvroRemoteException {
        // search biosamples for a known name
        final SearchIndividualsRequest ireq =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.INDIVIDUAL_NAME)
                        .build();

        final SearchIndividualsResponse iresp = client.biodata.searchIndividuals(ireq);
        final String individualId = iresp.getIndividuals().get(0).getId();

        final SearchBioSamplesRequest req =
                SearchBioSamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setIndividualId(individualId)
                        .build();

        final SearchBioSamplesResponse resp = client.biodata.searchBiosamples(req);
        assertThat(resp).isNotNull();
        for (BioSample b : resp.getBiosamples()) {
            assertThat(b.getIndividualId()).isEqualTo(individualId);
        }
    }
    /**
     * Tests that for each BioSample returned via search there exists an
     * equivalent record received by GET biosamples/id.
     * @throws AvroRemoteException
     */
    @Test
    public void checkGetBioSample() throws AvroRemoteException {
        final SearchBioSamplesRequest req =
                SearchBioSamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();

        final SearchBioSamplesResponse resp = client.biodata.searchBiosamples(req);
        assertThat(resp).isNotNull();
        for (BioSample b : resp.getBiosamples()) {
            final BioSample found = client.biodata.getBioSample(b.getId());
            assertThat(found).isEqualTo(b);
        }
    }

    /**
     * Tests the /individuals/search endpoint to ensure that searching
     * by name returns properly formed results.
     * @throws AvroRemoteException
     */
    @Test
    public void checkSearchIndividualsByName() throws AvroRemoteException {
        final SearchIndividualsRequest req =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(TestData.INDIVIDUAL_NAME)
                        .build();

        final SearchIndividualsResponse resp = client.biodata.searchIndividuals(req);
        assertThat(resp).isNotNull();
        for (Individual i : resp.getIndividuals()) {
            assertThat(i.getName()).isEqualTo(TestData.INDIVIDUAL_NAME);
            assertThat(i.getId()).isNotEmpty();
        }
    }
    /**
     * Tests that for each Individual returned via search there exists an
     * equivalent record received by GET individuals/id.
     * @throws AvroRemoteException
     */
    @Test
    public void checkGetIndividual() throws AvroRemoteException {
        final SearchIndividualsRequest req =
                SearchIndividualsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();

        final SearchIndividualsResponse resp = client.biodata.searchIndividuals(req);
        assertThat(resp).isNotNull();
        for (Individual i : resp.getIndividuals()) {
            final Individual found = client.biodata.getIndividual(i.getId());
            assertThat(found).isEqualTo(i);
        }
    }

}
