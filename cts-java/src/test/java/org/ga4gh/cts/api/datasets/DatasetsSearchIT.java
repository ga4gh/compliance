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

    /**
     * Check that the compliance dataset is present.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void checkComplianceDatasetIsPresent() throws AvroRemoteException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.metadata.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasets();

        assertThat(datasets).isNotNull();
        assertThat(datasets.size()).isGreaterThanOrEqualTo(1);

        // check that there is exactly one dataset with the ID of the test data
        assertThat(datasets.stream().filter(ds -> ds.getId().equals(TestData.getDatasetId())).count())
                .isEqualTo(1);
    }

    /**
     * Check that we can retrieve the compliance dataset via <tt>/datasets/{id}</tt>.
     *
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void fetchDatasetById() throws AvroRemoteException {
        final Dataset dataset = client.metadata.getDataset(TestData.getDatasetId());
        assertThat(dataset).isNotNull();
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    /**
     * Try to fetch a dataset with a bogus ID and make sure it fails.
     *
     * @throws AvroRemoteException if something goes wrong
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void fetchDatasetWithBogusId() throws AvroRemoteException {
        final String nonexistentDatasetId = Utils.randomId();

        // this should throw a "no such dataset" GAException
        final GAWrapperException didThrow =
                catchGAWrapperException(() -> client.metadata.getDataset(nonexistentDatasetId));

        assertThat(didThrow.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * For every dataset returned from <tt>/datasets/search</tt>, pass its ID to <tt>/datasets/{id}</tt>
     * and verify that the {@link Dataset} objects are identical.
     *
     * @throws AvroRemoteException if something goes wrong
     */
    @Test
    public void checkSearchResultAgainstGet() throws AvroRemoteException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.metadata.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasets();

        for (Dataset ds : datasets) {
            final Dataset dataset = client.metadata.getDataset(ds.getId());
            assertThat(ds).isEqualTo(dataset);
        }
    }

}
