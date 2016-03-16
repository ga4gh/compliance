package org.ga4gh.cts.api.biodata;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.datasets.DatasetsTests;
import org.ga4gh.methods.*;
import org.ga4gh.models.BioSample;
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
    public void checkSearchBioSamples() throws AvroRemoteException {
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
        // check proper key values

    }

    /**
     * Tests that for each BioSample returned via search there exists an
     * equivalent record received by GET biosamples/id.
     * @throws AvroRemoteException
     */
    @Test
    public void checkGetBioSample() throws AvroRemoteException {
        // search biosamples for a known name
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

}
