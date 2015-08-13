package org.ga4gh.cts.api.datasets;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchDatasetsRequest;
import org.ga4gh.methods.SearchDatasetsResponse;
import org.ga4gh.models.Dataset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests dealing with searching for datasets.
 *
 * @author Herb Jellinek
 */
@Category(DatasetsTests.class)
public class DatasetsSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    @Test
    public void checkComplianceDatasetIsPresent() throws AvroRemoteException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.reads.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasets();

        assertThat(datasets).isNotNull();
        assertThat(datasets.size()).isGreaterThanOrEqualTo(1);

        final Dataset dataset = datasets.get(0);
        assertThat(dataset).isNotNull();
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    @Test
    @Ignore("The server doesn't implement GET /datasets/{id} yet")
    public void fetchDatasetByName() throws AvroRemoteException {
        final Dataset dataset = client.reads.getDataset(TestData.getDatasetId());
        assertThat(dataset).isNotNull();
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    @Test
    @Ignore("The server doesn't implement GET /datasets/{id} yet")
    public void fetchDatasetWithBogusName() throws AvroRemoteException {
        final String nonexistentDatasetId = Utils.randomId();
        final Dataset dataset = client.reads.getDataset(nonexistentDatasetId);
        assertThat(dataset).isNull();
    }

}
