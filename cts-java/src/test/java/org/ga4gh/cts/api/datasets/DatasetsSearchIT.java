package org.ga4gh.cts.api.datasets;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import ga4gh.MetadataServiceOuterClass.*;
import ga4gh.Metadata.*;
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
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkComplianceDatasetIsPresent() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.metadata.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasetsList();

        assertThat(datasets).isNotNull();
        assertThat(datasets.size()).isGreaterThanOrEqualTo(1);

        // check that there is exactly one dataset with the ID of the test data
        assertThat(datasets.stream().filter(ds -> ds.getId().equals(TestData.getDatasetId())).count())
                .isEqualTo(1);
    }

    /**
     * Check that we can retrieve the compliance dataset via <tt>/datasets/{id}</tt>.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void fetchDatasetById() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final Dataset dataset = client.metadata.getDataset(TestData.getDatasetId());
        assertThat(dataset).isNotNull();
        assertThat(dataset.getId()).isEqualTo(TestData.getDatasetId());
    }

    /**
     * Try to fetch a dataset with a bogus ID and make sure it fails.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void fetchDatasetWithBogusId() {
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchResultAgainstGet() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchDatasetsRequest sdr = SearchDatasetsRequest.newBuilder().build();
        final SearchDatasetsResponse resp = client.metadata.searchDatasets(sdr);
        final List<Dataset> datasets = resp.getDatasetsList();

        for (Dataset ds : datasets) {
            final Dataset dataset = client.metadata.getDataset(ds.getId());
            assertThat(ds).isEqualTo(dataset);
        }
    }

}
