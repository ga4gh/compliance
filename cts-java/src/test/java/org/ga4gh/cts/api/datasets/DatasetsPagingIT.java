package org.ga4gh.cts.api.datasets;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.Metadata.Dataset;
import ga4gh.MetadataServiceOuterClass.SearchDatasetsRequest;
import ga4gh.MetadataServiceOuterClass.SearchDatasetsResponse;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
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
     * Check that we can page 1 by 1 through the {@link ga4gh.MetadataServiceOuterClass}s
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Metadata#searchDatasets(SearchDatasetsRequest)}.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughDatasets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // retrieve all the datasets
        final List<Dataset> listOfDatasets = Utils.getAllDatasets(client);
        assertThat(listOfDatasets).isNotEmpty();

        // we will remove Datasets from this Set and assert at the end that we have zero
        final Set<Dataset> setOfDatasets = new HashSet<>(listOfDatasets);
        assertThat(listOfDatasets).hasSize(setOfDatasets.size());

        // page through the Datasets using the same query parameters
        String pageToken = "";
        for (Dataset ignored : listOfDatasets) {
            final SearchDatasetsRequest pageReq =
                    SearchDatasetsRequest.newBuilder()
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchDatasetsResponse pageResp = client.metadata.searchDatasets(pageReq);
            final List<Dataset> pageOfDatasets = pageResp.getDatasetsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfDatasets).hasSize(1);
            assertThat(setOfDatasets).contains(pageOfDatasets.get(0));

            setOfDatasets.remove(pageOfDatasets.get(0));
        }

        assertThat(pageToken).isEmpty();
        assertThat(setOfDatasets).isEmpty();
    }

}
