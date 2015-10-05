package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchCallSetsRequest;
import org.ga4gh.methods.SearchCallSetsResponse;
import org.ga4gh.models.CallSet;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests paging through {@link CallSet}s.
 *
 * @author Herb Jellinek
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class CallSetsPagingIT {

    private static final URLMAPPING urls = URLMAPPING.getInstance();

    private static Client client = new Client(urls);

    /**
     * Check that we can page 1 by 1 through the {@link CallSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchCallSets(SearchCallSetsRequest)}.
     * <p>
     * The call to retrieve all {@link CallSet}s may return fewer than all of them, subject to
     * server-imposed limits.  The 1-by-1 paging must enumerate them all, however.  The set of "all"
     * must be a subset of those gathered one-by-one.
     * </p>
     *
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    @Test
    public void checkPagingOneByOneThroughCallSets() throws AvroRemoteException {

        final String variantSetId = Utils.getVariantSetId(client);
        // retrieve them all - this may return fewer than "all," however.
        final List<CallSet> listOfCallSets = Utils.getAllCallSets(client, variantSetId);
        assertThat(listOfCallSets).isNotEmpty();

        // we will do a set comparison after retrieving them 1 at a time
        final Set<CallSet> setOfExpectedCallSets = new HashSet<>(listOfCallSets);
        assertThat(listOfCallSets).hasSameSizeAs(setOfExpectedCallSets);

        final Set<CallSet> setOfCallSetsGathered1By1 = new HashSet<>(setOfExpectedCallSets.size());

        // page through the CallSets using the same query parameters and collect them
        String pageToken = null;
        do {
            final SearchCallSetsRequest pageReq =
                    SearchCallSetsRequest.newBuilder()
                                         .setVariantSetId(variantSetId)
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchCallSetsResponse pageResp =
                    client.variants.searchCallSets(pageReq);
            final List<CallSet> pageOfCallSets = pageResp.getCallSets();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfCallSets).hasSize(1);
            setOfCallSetsGathered1By1.add(pageOfCallSets.get(0));

        } while (pageToken != null);

        assertThat(setOfCallSetsGathered1By1).containsAll(setOfExpectedCallSets);
    }

    /**
     * Check that we can page through the {@link CallSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchCallSets(SearchCallSetsRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughCallSets() throws AvroRemoteException {
        final String variantSetId = Utils.getVariantSetId(client);
        final List<CallSet> listOfCallSets = Utils.getAllCallSets(client, variantSetId);

        // page through the variants in one gulp
        checkSinglePageOfCallSets(variantSetId,
                                  listOfCallSets.size(),
                                  listOfCallSets);
    }

    /**
     * Check that we can page through the variants we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchCallSets
     * (SearchCallSetsRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link
     * GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughCallSets() throws AvroRemoteException {
        final String variantSetId = Utils.getVariantSetId(client);
        final List<CallSet> listOfCallSets = Utils.getAllCallSets(client, variantSetId);

        checkSinglePageOfCallSets(variantSetId,
                                  listOfCallSets.size() * 2,
                                  listOfCallSets);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of {@link CallSet}s from {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchCallSets(SearchCallSetsRequest)},
     * using <tt>pageSize</tt> as the page size.
     *
     * @param variantSetId     the ID of the {@link VariantSet} we're paging through
     * @param pageSize         the page size we'll request
     * @param expectedCallSets all of the {@link CallSet} objects we expect to receive
     *
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfCallSets(String variantSetId,
                                           int pageSize,
                                           List<CallSet> expectedCallSets) throws AvroRemoteException {

        final SearchCallSetsRequest pageReq =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .setPageSize(pageSize)
                                     .build();
        final SearchCallSetsResponse pageResp = client.variants.searchCallSets(pageReq);
        final List<CallSet> pageOfCallSets = pageResp.getCallSets();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfCallSets).hasSize(expectedCallSets.size());
        assertThat(expectedCallSets).containsAll(pageOfCallSets);

        assertThat(pageToken).isNull();
    }

}
