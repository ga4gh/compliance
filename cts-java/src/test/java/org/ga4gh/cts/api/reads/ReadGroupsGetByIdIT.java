package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.models.ReadGroup;
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
public class ReadGroupsGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that the {@link ReadGroup}s we get via search (1) have valid IDs in them; (2)
     * those IDs can be used to fetch identical {@link ReadGroup}s.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
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

        // for each one, fetch its ReadGroups and compare the result to the one we got by searching
        for (ReadGroupSet readGroupSets : response.getReadGroupSets()) {
            assertThat(readGroupSets).isNotNull();

            for (ReadGroup readGroupFromSearch : readGroupSets.getReadGroups()) {
                assertThat(readGroupFromSearch).isNotNull();
                final String searchReadGroupId = readGroupFromSearch.getId();
                final ReadGroup readGroupFromGet = client.reads.getReadGroup(searchReadGroupId);

                assertThat(readGroupFromGet).isEqualTo(readGroupFromSearch);
            }
        }
    }

}
