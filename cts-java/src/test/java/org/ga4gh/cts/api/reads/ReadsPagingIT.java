package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadsRequest;
import org.ga4gh.methods.SearchReadsResponse;
import org.ga4gh.models.ReadAlignment;
import org.ga4gh.models.Reference;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Test the <tt>/reads/search</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
public class ReadsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link org.ga4gh.models.ReadAlignment}s (familiarly, "reads")
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReadGroupSets(SearchReadGroupSetsRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);

        // retrieve all the reads
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // we will remove ReadAlignments from this Set and assert at the end that we have zero
        final Set<ReadAlignment> setOfReads = new HashSet<>(listOfReads);
        assertThat(listOfReads).hasSize(setOfReads.size());

        // page through the ReadAlignments using the same query parameters
        String pageToken = null;
        for (ReadAlignment ignored : listOfReads) {
            final SearchReadsRequest pageReq =
                    SearchReadsRequest.newBuilder()
                                            .setReferenceId(referenceId)
                                            .setPageSize(1)
                                            .setPageToken(pageToken)
                                      .build();
            final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
            final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReads).hasSize(1);
            assertThat(setOfReads).contains(pageOfReads.get(0));

            setOfReads.remove(pageOfReads.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfReads).isEmpty();
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // page through the reads in one gulp
        checkSinglePageOfReads(referenceId, readGroupId, listOfReads.size(),
                               listOfReads);
    }

    /**
     * Check that we can page through the {@link ReadAlignment}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReads() throws AvroRemoteException {

        final String referenceId = Utils.getValidReferenceId(client);
        final String readGroupId = Utils.getReadGroupId(client);
        final List<ReadAlignment> listOfReads = Utils.getAllReads(client, referenceId, readGroupId);
        assertThat(listOfReads).isNotEmpty();

        // page through the reads in one too-large gulp
        checkSinglePageOfReads(referenceId, readGroupId, listOfReads.size() * 2,
                               listOfReads);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReadAlignment}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Reads#searchReads(SearchReadsRequest)}
     * using <tt>pageSize</tt> as the page size.
     *
     * @param refId         the ID of the {@link Reference}
     * @param readGroupId   the ID of the {@link org.ga4gh.models.ReadGroup}
     * @param pageSize      the page size we'll request
     * @param expectedReads all of the {@link ReadAlignment} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfReads(String refId,
                                        String readGroupId,
                                        int pageSize,
                                        List<ReadAlignment> expectedReads)
            throws AvroRemoteException {

        final SearchReadsRequest pageReq =
                SearchReadsRequest.newBuilder()
                                  .setReferenceId(refId)
                                  .setReadGroupIds(aSingle(readGroupId))
                                  .setPageSize(pageSize)
                                  .build();
        final SearchReadsResponse pageResp = client.reads.searchReads(pageReq);
        final List<ReadAlignment> pageOfReads = pageResp.getAlignments();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReads).hasSize(expectedReads.size());
        assertThat(expectedReads).containsAll(pageOfReads);

        assertThat(pageToken).isNull();
    }
}
