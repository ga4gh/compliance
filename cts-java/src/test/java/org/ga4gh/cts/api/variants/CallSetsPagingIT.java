package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchCallSetsRequest;
import org.ga4gh.methods.SearchCallSetsResponse;
import org.ga4gh.models.CallSet;
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
        // retrieve them all
        final List<CallSet> listOfCallSets = Utils.getAllCallSets(client, variantSetId);
        assertThat(listOfCallSets).isNotEmpty();

        // we will do a set comparison after retrieving them one at a time
        final Set<CallSet> setOfExpectedCallSets = new HashSet<>(listOfCallSets);
        assertThat(listOfCallSets).hasSameSizeAs(setOfExpectedCallSets);

        final Set<CallSet> setOfCallSetsGathered1By1 = new HashSet<>(setOfExpectedCallSets.size());

        // page through the CallSets one at a time and collect them
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
        assertThat(setOfExpectedCallSets).containsAll(setOfCallSetsGathered1By1);
    }


}
