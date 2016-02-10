package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReferenceServiceOuterClass.SearchReferenceSetsRequest;
import ga4gh.ReferenceServiceOuterClass.SearchReferenceSetsResponse;
import ga4gh.References.ReferenceSet;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.datasets.DatasetsTests;
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
     * <p>
     * The call to retrieve all {@link ReferenceSet}s may return fewer than all of them, subject to
     * server-imposed limits.  The 1-by-1 paging must enumerate them all, however.  The set of "all"
     * must be a subset of those gathered one-by-one.
     * </p>
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughReferenceSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // retrieve them all - this may return fewer than "all," however.
        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();

        // we will do a set comparison after retrieving them 1 at a time
        final Set<ReferenceSet> setOfExpectedReferenceSets = new HashSet<>(listOfReferenceSets);
        assertThat(listOfReferenceSets).hasSize(setOfExpectedReferenceSets.size());

        final Set<ReferenceSet> setOfReferenceSetsGathered1By1 = new HashSet<>(setOfExpectedReferenceSets.size());
        // page through the ReferenceSets using the same query parameters and collect them
        String pageToken = "";
        do {
            final SearchReferenceSetsRequest pageReq =
                    SearchReferenceSetsRequest.newBuilder()
                                           .setPageSize(1)
                                           .setPageToken(pageToken)
                                           .build();
            final SearchReferenceSetsResponse pageResp = client.references.searchReferenceSets(pageReq);
            final List<ReferenceSet> pageOfReferenceSets = pageResp.getReferenceSetsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReferenceSets).hasSize(1);
            setOfReferenceSetsGathered1By1.add(pageOfReferenceSets.get(0));

        } while (pageToken != null && !pageToken.equals(""));

        assertThat(setOfReferenceSetsGathered1By1).containsAll(setOfExpectedReferenceSets);
    }

    /**
     * Check that we can page through the {@link ReferenceSet}s we receive from
     * {@link Client.References#searchReferenceSets(SearchReferenceSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneChunkThroughReferenceSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReferenceSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfReferenceSets(int pageSize,
                                                List<ReferenceSet> expectedReferenceSets) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest pageReq =
                SearchReferenceSetsRequest.newBuilder()
                                       .setPageSize(pageSize)
                                       .build();
        final SearchReferenceSetsResponse pageResp = client.references.searchReferenceSets(pageReq);
        final List<ReferenceSet> pageOfReferenceSets = pageResp.getReferenceSetsList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReferenceSets).hasSize(expectedReferenceSets.size());
        assertThat(expectedReferenceSets).containsAll(pageOfReferenceSets);

        assertThat(pageToken).isEmpty();
    }
}
