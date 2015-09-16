package org.ga4gh.cts.api.references;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.datasets.DatasetsTests;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReferenceSetsRequest;
import org.ga4gh.methods.SearchReferenceSetsResponse;
import org.ga4gh.models.ReferenceSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test paging through the results returned by <tt>/references/search</tt>.
 *
 * @author Herb Jellinek
 */
@Category(DatasetsTests.class)
public class ReferenceSetsPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link ReferenceSet}s we receive from
     * {@link Client.References#searchReferenceSets(SearchReferenceSetsRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughReferenceSets() throws AvroRemoteException {

        // retrieve them all
        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();

        // we will remove ReferenceSets from this Set and assert at the end that we have zero
        final Set<ReferenceSet> setOfReferenceSets = new HashSet<>(listOfReferenceSets);
        assertThat(listOfReferenceSets).hasSize(setOfReferenceSets.size());

        // page through the ReferenceSets using the same query parameters
        String pageToken = null;
        for (ReferenceSet ignored : listOfReferenceSets) {
            final SearchReferenceSetsRequest pageReq =
                    SearchReferenceSetsRequest.newBuilder()
                                           .setPageSize(1)
                                           .setPageToken(pageToken)
                                           .build();
            final SearchReferenceSetsResponse pageResp = client.references.searchReferenceSets(pageReq);
            final List<ReferenceSet> pageOfReferenceSets = pageResp.getReferenceSets();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReferenceSets).hasSize(1);
            assertThat(setOfReferenceSets).contains(pageOfReferenceSets.get(0));

            setOfReferenceSets.remove(pageOfReferenceSets.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfReferenceSets).isEmpty();
    }

    /**
     * Check that we can page through the {@link ReferenceSet}s we receive from
     * {@link Client.References#searchReferenceSets(SearchReferenceSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughReferenceSets() throws AvroRemoteException {

        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();

        // page through the reference sets in one gulp
        checkSinglePageOfReferenceSets(listOfReferenceSets.size(),
                                       listOfReferenceSets);
    }

    /**
     * Check that we can page through the {@link ReferenceSet}s we receive from
     * {@link Client.References#searchReferenceSets(SearchReferenceSetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReferenceSets() throws AvroRemoteException {

        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();

        checkSinglePageOfReferenceSets(listOfReferenceSets.size() * 2,
                                       listOfReferenceSets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link ReferenceSet}s from
     * {@link Client.References#searchReferenceSets(SearchReferenceSetsRequest)} using
     * <tt>pageSize</tt> as the page size.
     *
     * @param pageSize              the page size we'll request
     * @param expectedReferenceSets all of the {@link ReferenceSet} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfReferenceSets(int pageSize,
                                                List<ReferenceSet> expectedReferenceSets)
            throws AvroRemoteException {

        final SearchReferenceSetsRequest pageReq =
                SearchReferenceSetsRequest.newBuilder()
                                       .setPageSize(pageSize)
                                       .build();
        final SearchReferenceSetsResponse pageResp = client.references.searchReferenceSets(pageReq);
        final List<ReferenceSet> pageOfReferenceSets = pageResp.getReferenceSets();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReferenceSets).hasSize(expectedReferenceSets.size());
        assertThat(expectedReferenceSets).containsAll(pageOfReferenceSets);

        assertThat(pageToken).isNull();
    }
}
