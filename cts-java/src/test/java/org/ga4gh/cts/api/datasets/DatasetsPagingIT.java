package org.ga4gh.cts.api.datasets;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchDatasetsRequest;
import org.ga4gh.methods.SearchDatasetsResponse;
import org.ga4gh.models.Dataset;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/datasets/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(DatasetsTests.class)
public class DatasetsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link org.ga4gh.models.Dataset}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchDatasets(SearchDatasetsRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link
     * GAException})
     */
    @Test
    public void checkPagingOneByOneThroughDatasets() throws AvroRemoteException {

        // retrieve all the datasets
        final List<Dataset> listOfDatasets = Utils.getAllDatasets(client);
        assertThat(listOfDatasets).isNotEmpty();

        // we will remove Datasets from this Set and assert at the end that we have zero
        final Set<Dataset> setOfDatasets = new HashSet<>(listOfDatasets);
        assertThat(listOfDatasets).hasSize(setOfDatasets.size());

        // page through the Datasets using the same query parameters
        String pageToken = null;
        for (Dataset ignored : listOfDatasets) {
            final SearchDatasetsRequest pageReq =
                    SearchDatasetsRequest.newBuilder()
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchDatasetsResponse pageResp = client.reads.searchDatasets(pageReq);
            final List<Dataset> pageOfDatasets = pageResp.getDatasets();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfDatasets).hasSize(1);
            assertThat(setOfDatasets).contains(pageOfDatasets.get(0));

            setOfDatasets.remove(pageOfDatasets.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfDatasets).isEmpty();
    }

    /**
     * Check that we can page through the {@link Dataset}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchDatasets(SearchDatasetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link
     * GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughDatasets() throws AvroRemoteException {

        // retrieve all the datasets
        final List<Dataset> listOfDatasets = Utils.getAllDatasets(client);
        assertThat(listOfDatasets).isNotEmpty();

        // page through the reads in one gulp
        checkSinglePageOfDatasets(listOfDatasets.size(),
                                  listOfDatasets);
    }

    /**
     * Check that we can page through the {@link Dataset}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchDatasets(SearchDatasetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link
     * GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReads() throws AvroRemoteException {

        final List<Dataset> listOfDatasets = Utils.getAllDatasets(client);
        assertThat(listOfDatasets).isNotEmpty();

        // page through the reads in one too-large gulp
        checkSinglePageOfDatasets(listOfDatasets.size() * 2, listOfDatasets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link Dataset}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchDatasets(SearchDatasetsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param pageSize         the page size we'll request
     * @param expectedDatasets all of the {@link Dataset} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfDatasets(int pageSize,
                                           List<Dataset> expectedDatasets)
            throws AvroRemoteException {

        final SearchDatasetsRequest pageReq =
                SearchDatasetsRequest.newBuilder()
                                     .setPageSize(pageSize)
                                     .build();
        final SearchDatasetsResponse pageResp = client.reads.searchDatasets(pageReq);
        final List<Dataset> pageOfDatasets = pageResp.getDatasets();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfDatasets).hasSize(expectedDatasets.size());
        assertThat(expectedDatasets).containsAll(pageOfDatasets);

        assertThat(pageToken).isNull();
    }

}
