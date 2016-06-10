package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReferenceServiceOuterClass.SearchReferencesRequest;
import ga4gh.ReferenceServiceOuterClass.SearchReferencesResponse;
import ga4gh.References.Reference;
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
 * Test paging through the results returned by <tt>/referencesets/search</tt>.
 *
 * @author Herb Jellinek
 */
@Category(DatasetsTests.class)
public class ReferencesPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link ga4gh.References}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences(SearchReferencesRequest)}.
     * <p>
     * The call to retrieve all {@link Reference}s may return fewer than all of them, subject to
     * server-imposed limits.  The 1-by-1 paging must enumerate them all, however.  The set of "all"
     * must be a subset of those gathered one-by-one.
     * </p>
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughReferences() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<ReferenceSet> allRefSets = Utils.getAllReferenceSets(client);
        assertThat(allRefSets).isNotEmpty();

        final String refSetId = allRefSets.get(0).getId();

        // retrieve them all - this may return fewer than "all," however.
        final List<Reference> listOfReferences = Utils.getAllReferences(client, refSetId);
        assertThat(listOfReferences).isNotEmpty();

        // we will do a set comparison after retrieving them 1 at a time
        final Set<Reference> setOfExpectedReferences = new HashSet<>(listOfReferences);
        assertThat(listOfReferences).hasSize(setOfExpectedReferences.size());

        final Set<Reference> setOfReferencesGathered1By1 = new HashSet<>(setOfExpectedReferences.size());
        // page through the References using the same query parameters and collect them
        String pageToken = "";
        do {
            final SearchReferencesRequest pageReq =
                    SearchReferencesRequest.newBuilder()
                                           .setReferenceSetId(refSetId)
                                           .setPageSize(1)
                                           .setPageToken(pageToken)
                                           .build();
            final SearchReferencesResponse pageResp = client.references.searchReferences(pageReq);
            final List<Reference> pageOfReferences = pageResp.getReferencesList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReferences).hasSize(1);
            setOfReferencesGathered1By1.add(pageOfReferences.get(0));
        } while (pageToken != null && !pageToken.equals(""));

        assertThat(setOfReferencesGathered1By1).containsAll(setOfExpectedReferences);
    }

    /**
     * Check that we can page through the {@link Reference}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences(SearchReferencesRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneChunkThroughReferences() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();
        final String refSetId = listOfReferenceSets.get(0).getId();

        final List<Reference> listOfReferences =
                Utils.getAllReferences(client, refSetId);

        // page through the references in one gulp
        checkSinglePageOfReferences(refSetId, listOfReferences.size(),
                                    listOfReferences);
    }

    /**
     * Check that we can page through the {@link Reference}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences(SearchReferencesRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReferences() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<ReferenceSet> listOfReferenceSets = Utils.getAllReferenceSets(client);
        assertThat(listOfReferenceSets).isNotEmpty();
        final String refSetId = listOfReferenceSets.get(0).getId();
        final List<Reference> listOfReferences =
                Utils.getAllReferences(client,
                                       listOfReferenceSets.get(0).getId());

        checkSinglePageOfReferences(refSetId, listOfReferences.size() * 2,
                                    listOfReferences);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link Reference}s from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences
     * (SearchReferencesRequest)} using <tt>pageSize</tt> as the page size.
     *
     * @param refSetId           the ID of the {@link ReferenceSet}
     * @param pageSize           the page size we'll request
     * @param expectedReferences all of the {@link Reference} objects we expect to receive
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkSinglePageOfReferences(String refSetId, int pageSize,
                                             List<Reference> expectedReferences) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final SearchReferencesRequest pageReq =
                SearchReferencesRequest.newBuilder()
                                       .setReferenceSetId(refSetId)
                                       .setPageSize(pageSize)
                                       .build();
        final SearchReferencesResponse pageResp = client.references.searchReferences(pageReq);
        final List<Reference> pageOfReferences = pageResp.getReferencesList();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReferences).hasSize(expectedReferences.size());
        assertThat(expectedReferences).containsAll(pageOfReferences);

        assertThat(pageToken).isEmpty();
    }
}
