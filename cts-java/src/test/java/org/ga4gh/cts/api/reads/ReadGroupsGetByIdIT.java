package org.ga4gh.cts.api.reads;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsResponse;
import ga4gh.Reads.ReadGroup;
import ga4gh.Reads.ReadGroupSet;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testGetByIdMatchesSearch() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest request =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();

        // search for all ReadGroupSets
        final SearchReadGroupSetsResponse response = client.reads.searchReadGroupSets(request);
        assertThat(response).isNotNull();
        assertThat(response.getReadGroupSetsList()).isNotEmpty();

        // for each one, fetch its ReadGroups and compare the result to the one we got by searching
        for (ReadGroupSet readGroupSets : response.getReadGroupSetsList()) {
            assertThat(readGroupSets).isNotNull();

            for (ReadGroup readGroupFromSearch : readGroupSets.getReadGroupsList()) {
                assertThat(readGroupFromSearch).isNotNull();
                final String searchReadGroupId = readGroupFromSearch.getId();
                final ReadGroup readGroupFromGet = client.reads.getReadGroup(searchReadGroupId);

                assertThat(readGroupFromGet).isEqualTo(readGroupFromSearch);
            }
        }
    }

}
