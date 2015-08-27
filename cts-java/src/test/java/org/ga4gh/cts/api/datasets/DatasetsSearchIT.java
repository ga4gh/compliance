package org.ga4gh.cts.api.datasets;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchDatasetsRequest;
import org.ga4gh.methods.SearchDatasetsResponse;
import org.ga4gh.models.Dataset;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

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
        // XXX this is no longer meaningful
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    @Test
    public void fetchDatasetById() throws AvroRemoteException {
        // XXX getDataset isn't implemented on the ref server
        final Dataset dataset = client.reads.getDataset(TestData.getDatasetId());
        assertThat(dataset).isNotNull();
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    /**
     * Try to fetch a dataset with a bogus ID and make sure it fails.
     * @throws AvroRemoteException if something goes wrong
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void fetchDatasetWithBogusId() throws AvroRemoteException {
        final String nonexistentDatasetId = Utils.randomId();

        // this should throw a "no such dataset" GAException
        final GAWrapperException didThrow =
                catchGAWrapperException(() -> client.reads.getDataset(nonexistentDatasetId));

        assertThat(didThrow.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    @Test
    public void checkSearchResultAgainstGet() throws AvroRemoteException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.reads.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasets();

        for (Dataset ds : datasets) {
            // XXX getDataset isn't implemented on the ref server
            final Dataset dataset = client.reads.getDataset(ds.getId());
            assertThat(ds).isEqualTo(dataset);
        }
    }

}
