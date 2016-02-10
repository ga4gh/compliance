package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Variants.*;
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughCallSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final String variantSetId = Utils.getVariantSetId(client);
        // retrieve them all
        final List<CallSet> listOfCallSets = Utils.getAllCallSets(client, variantSetId);
        assertThat(listOfCallSets).isNotEmpty();

        // we will do a set comparison after retrieving them one at a time
        final Set<CallSet> setOfExpectedCallSets = new HashSet<>(listOfCallSets);
        assertThat(listOfCallSets).hasSameSizeAs(setOfExpectedCallSets);

        final Set<CallSet> setOfCallSetsGathered1By1 = new HashSet<>(setOfExpectedCallSets.size());

        // page through the CallSets one at a time and collect them
        String pageToken = "";
        do {
            final SearchCallSetsRequest pageReq =
                    SearchCallSetsRequest.newBuilder()
                                         .setVariantSetId(variantSetId)
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchCallSetsResponse pageResp =
                    client.variants.searchCallSets(pageReq);
            final List<CallSet> pageOfCallSets = pageResp.getCallSetsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfCallSets).hasSize(1);
            setOfCallSetsGathered1By1.add(pageOfCallSets.get(0));

        } while (pageToken != null && !pageToken.equals(""));

        assertThat(setOfCallSetsGathered1By1).containsAll(setOfExpectedCallSets);
        assertThat(setOfExpectedCallSets).containsAll(setOfCallSetsGathered1By1);
    }


}
