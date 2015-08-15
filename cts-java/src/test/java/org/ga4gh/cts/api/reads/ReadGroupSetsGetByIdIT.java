package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the <pre>/readgroups/{id}</pre> endpoint.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadGroupSetsGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that the {@link ReadGroupSet}s we get via search (1) have valid IDs in them; (2)
     * those IDs can be used to fetch identical {@link ReadGroupSet}s.
     *
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void testGetByIdMatchesSearch() throws AvroRemoteException {
        final SearchReadGroupSetsRequest request =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();

        // search for all ReadGroupSets
        final SearchReadGroupSetsResponse response = client.reads.searchReadGroupSets(request);
        assertThat(response).isNotNull();
        assertThat(response.getReadGroupSets()).isNotEmpty();

        // for each one, fetch it by its ID and compare the result to the one we got by searching
        for (ReadGroupSet fromSearch : response.getReadGroupSets()) {
            assertThat(fromSearch).isNotNull();
            final String searchReadGroupSetId = fromSearch.getId();
            final ReadGroupSet fromGet = client.reads.getReadGroupSet(searchReadGroupSetId);
            assertThat(fromGet).isEqualTo(fromSearch);
        }
    }

}
