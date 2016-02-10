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
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests relating to searching for {@link ReadGroupSet}s by name.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadGroupSetsGetByNameIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that searching for {@link ReadGroupSet}s with a bogus name returns an empty list.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testSearchForBogusNameReturnsEmptyList() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest request =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .setName(Utils.randomName())
                                          .build();

        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(request);
        assertThat(resp.getReadGroupSetsList()).isEmpty();
    }

    /**
     * Check that searching for {@link ReadGroup}s with a valid name succeeds.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testGetByGoodNameSucceeds() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // get all ReadGroupSets
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();

        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);

        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();
        assertThat(readGroupSets).isNotNull().isNotEmpty();

        final Map<String, ReadGroupSet> byName = new HashMap<>(readGroupSets.size());
        for (ReadGroupSet readGroupSet : readGroupSets) {
            final String readGroupSetName = readGroupSet.getName();

            // we can't allow duplicate names in this test, even if they're legal
            assertThat(byName).doesNotContainKey(readGroupSetName);
            byName.put(readGroupSetName, readGroupSet);
        }

        // now search for each by name
        for (String name : byName.keySet()) {
            final ReadGroupSet expectedReadGroupSet = byName.get(name);
            // sanity check
            assertThat(expectedReadGroupSet).isNotNull();

            final SearchReadGroupSetsRequest nameReq =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setName(name)
                                              .build();
            final SearchReadGroupSetsResponse nameResp = client.reads.searchReadGroupSets(nameReq);

            // check that we found it
            final List<ReadGroupSet> readGroupSetsFromQuery = nameResp.getReadGroupSetsList();
            assertThat(readGroupSetsFromQuery).isNotNull();
            assertThat(readGroupSetsFromQuery).hasSize(1);

            assertThat(readGroupSetsFromQuery.get(0)).isEqualTo(expectedReadGroupSet);
        }
    }

}
