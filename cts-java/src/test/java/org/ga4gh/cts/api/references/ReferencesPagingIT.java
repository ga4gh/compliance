package org.ga4gh.cts.api.references;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.datasets.DatasetsTests;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReferencesRequest;
import org.ga4gh.methods.SearchReferencesResponse;
import org.ga4gh.models.Reference;
import org.ga4gh.models.ReferenceSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test paging through the results returned by <tt>/datasets/search</tt>.
 *
 * @author Herb Jellinek
 */
@Category(DatasetsTests.class)
public class ReferencesPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the {@link org.ga4gh.models.Reference}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences(SearchReferencesRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughDatasets() throws AvroRemoteException {

        final List<ReferenceSet> allRefSets = Utils.getAllReferenceSets(client);
        assertThat(allRefSets).isNotEmpty();

        final String refSetId = allRefSets.get(0).getId();

        // retrieve them all
        final List<Reference> listOfReferences = Utils.getAllReferences(client, refSetId);
        assertThat(listOfReferences).isNotEmpty();

        // we will remove References from this Set and assert at the end that we have zero
        final Set<Reference> setOfReferences = new HashSet<>(listOfReferences);
        assertThat(listOfReferences).hasSize(setOfReferences.size());

        // page through the References using the same query parameters
        String pageToken = null;
        for (Reference ignored : listOfReferences) {
            final SearchReferencesRequest pageReq =
                    SearchReferencesRequest.newBuilder()
                                           .setReferenceSetId(refSetId)
                                           .setPageSize(1)
                                           .setPageToken(pageToken)
                                           .build();
            final SearchReferencesResponse pageResp = client.references.searchReferences(pageReq);
            final List<Reference> pageOfReferences = pageResp.getReferences();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfReferences).hasSize(1);
            assertThat(setOfReferences).contains(pageOfReferences.get(0));

            setOfReferences.remove(pageOfReferences.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfReferences).isEmpty();
    }

    /**
     * Check that we can page through the {@link Reference}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#searchReferences(SearchReferencesRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughReferences() throws AvroRemoteException {

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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughReferences() throws AvroRemoteException {

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
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfReferences(String refSetId, int pageSize,
                                             List<Reference> expectedReferences)
            throws AvroRemoteException {

        final SearchReferencesRequest pageReq =
                SearchReferencesRequest.newBuilder()
                                       .setReferenceSetId(refSetId)
                                       .setPageSize(pageSize)
                                       .build();
        final SearchReferencesResponse pageResp = client.references.searchReferences(pageReq);
        final List<Reference> pageOfReferences = pageResp.getReferences();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfReferences).hasSize(expectedReferences.size());
        assertThat(expectedReferences).containsAll(pageOfReferences);

        assertThat(pageToken).isNull();
    }
}
