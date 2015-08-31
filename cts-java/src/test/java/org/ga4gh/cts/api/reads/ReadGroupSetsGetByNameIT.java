package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.models.ReadGroup;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;
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
     * Check that searching for {@link ReadGroup}s with a bogus name fails.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testSearchForBogusNameFails() throws AvroRemoteException {
        final SearchReadGroupSetsRequest request =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .setName(Utils.randomName())
                                          .build();

        final GAWrapperException g =
                Utils.catchGAWrapperException(() -> client.reads.searchReadGroupSets(request));
        assertThat(g.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Check that searching for {@link ReadGroup}s with a valid name succeeds.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testGetByGoodNameSucceeds() throws AvroRemoteException {
        // get all ReadGroupSets
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();

        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);

        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();
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
            final List<ReadGroupSet> readGroupSetsFromQuery = nameResp.getReadGroupSets();
            assertThat(readGroupSetsFromQuery).isNotNull();
            assertThat(readGroupSetsFromQuery).hasSize(1);

            assertThat(readGroupSetsFromQuery.get(0)).isEqualTo(expectedReadGroupSet);
        }
    }

}
