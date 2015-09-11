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
/**
 */
public class CallSetsPagingIT {

    private static final URLMAPPING urls = URLMAPPING.getInstance();

    private static Client client = new Client(urls);

    /**
     * Check that we can page 1 by 1 through the {@link CallSet}s we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.Variants#searchCallSets(SearchCallSetsRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    @Test
    public void checkPagingOneByOneThroughCallSets() throws AvroRemoteException {

        final String variantSetId = Utils.getVariantSetId(client);
        final List<CallSet> allCallSets = Utils.getAllCallSets(client, variantSetId);
        final Set<CallSet> setOfCallSets = new HashSet<>(allCallSets);

        String pageToken = null;
        for (CallSet ignored : allCallSets) {
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
            assertThat(setOfCallSets).contains(pageOfCallSets.get(0));

            setOfCallSets.remove(pageOfCallSets.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfCallSets).isEmpty();
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

        // we don't care how many there are, as long as it's at least a few
        assertThat(listOfCallSets.size()).isGreaterThanOrEqualTo(3);

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

        // we don't care how many there are, as long as it's at least a few
        assertThat(listOfCallSets.size()).isGreaterThanOrEqualTo(3);

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
