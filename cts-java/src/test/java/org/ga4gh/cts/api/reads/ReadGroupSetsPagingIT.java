package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.models.ReadAlignment;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/readgroupsets/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadGroupSetsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link ReadGroupSet}s
     * we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughReadGroupSets() throws AvroRemoteException {


        // retrieve all the reads
        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // we will remove ReadGroupSets from this Set and assert at the end that we have zero
        final Set<ReadGroupSet> setOfReadGroupSets = new HashSet<>(listOfReadGroupSets);
        assertThat(listOfReadGroupSets).hasSize(setOfReadGroupSets.size());

        // page through the ReadGroupSets using the same query parameters
        String pageToken = null;
        for (ReadGroupSet ignored : listOfReadGroupSets) {
            final SearchReadGroupSetsRequest pageReq =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setPageSize(1)
                                              .setPageToken(pageToken)
                                              .build();
            final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(pageReq);
            final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSets();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReadGroupSets).hasSize(1);
            assertThat(setOfReadGroupSets).contains(pageOfReadGroupSets.get(0));

            setOfReadGroupSets.remove(pageOfReadGroupSets.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfReadGroupSets).isEmpty();
    }

    /**
     * Check that we can page through the {@link ReadGroupSet}s we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughReadGroupSets() throws AvroRemoteException {

        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // page through the reads in one gulp
        checkSinglePageOfReadGroupSets(listOfReadGroupSets.size(),
                                       listOfReadGroupSets);
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReadGroupSets() throws AvroRemoteException {

        final List<ReadGroupSet> listOfReadGroupSets = Utils.getAllReadGroupSets(client);
        assertThat(listOfReadGroupSets).isNotEmpty();

        // page through the reads in one too-large gulp
        checkSinglePageOfReadGroupSets(listOfReadGroupSets.size() * 2,
                                       listOfReadGroupSets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReadGroupSet}s from
     * {@link Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param pageSize              the page size we'll request
     * @param expectedReadGroupSets all of the {@link ReadGroupSet} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfReadGroupSets(int pageSize,
                                                List<ReadGroupSet> expectedReadGroupSets)
            throws AvroRemoteException {

        final SearchReadGroupSetsRequest pageReq =
                SearchReadGroupSetsRequest.newBuilder()
                                  .setDatasetId(TestData.getDatasetId())
                                  .setPageSize(pageSize)
                                  .build();
        final SearchReadGroupSetsResponse pageResp = client.reads.searchReadGroupSets(pageReq);
        final List<ReadGroupSet> pageOfReadGroupSets = pageResp.getReadGroupSets();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReadGroupSets).hasSize(expectedReadGroupSets.size());
        assertThat(expectedReadGroupSets).containsAll(pageOfReadGroupSets);

        assertThat(pageToken).isNull();
    }
}
