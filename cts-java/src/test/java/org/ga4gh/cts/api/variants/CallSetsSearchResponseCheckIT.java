package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.CallSet;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Callsets-related tests.
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class CallSetsSearchResponseCheckIT implements CtkLogs {

    private static final URLMAPPING urls = URLMAPPING.getInstance();

    private static Client client = new Client(urls);

    /**
     * Fetch call sets and make sure we do get some back.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchForExpectedCallSets() throws AvroRemoteException {
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSets()).isNotEmpty();
        for (VariantSet set : vResp.getVariantSets()) {
            final String id = set.getId();

            final SearchCallSetsRequest csReq =
                    SearchCallSetsRequest.newBuilder()
                                         .setVariantSetId(id)
                                         .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);

            assertThat(csResp.getCallSets()).isNotEmpty();
        }
    }
    /**
     * Make sure VariantSet records exist for each CallSet.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchCallSetsByVariantSet() throws AvroRemoteException {
        // Get all the variant sets
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        // Make sure there are variant sets there
        assertThat(vResp.getVariantSets()).isNotEmpty();
        for (VariantSet set : vResp.getVariantSets()) {
            final String id = set.getId();

            // Find CallSets that go with this VariantSet
            final SearchCallSetsRequest csReq =
                    SearchCallSetsRequest.newBuilder()
                            .setVariantSetId(id)
                            .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);
            for (CallSet cs : csResp.getCallSets()) {
                for (String vsid : cs.getVariantSetIds()) {
                    // Look up VariantSets by id
                    VariantSet v = client.variants.getVariantSet(vsid);
                    assertThat(v).isNotNull();
                    assertThat(v.getId()).isEqualTo(id);
                }
            }
        }
    }
    /**
     * Test getting a call set with a valid ID.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void getCallSetWithValidIDShouldSucceed() throws AvroRemoteException {
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSets()).isNotEmpty();

        // grab the first VariantSet and use it as source of CallSets
        final VariantSet variantSet = vResp.getVariantSets().get(0);
        final String variantSetId = variantSet.getId();

        final SearchCallSetsRequest callSetsSearchRequest =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .build();
        final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);

        // grab one of the CallSets returned from the search
        assertThat(csResp.getCallSets()).isNotEmpty();
        final CallSet callSetFromSearch = csResp.getCallSets().get(0);
        final String callSetId = callSetFromSearch.getId();
        assertThat(callSetId).isNotNull();

        // fetch the CallSet with that ID and compare with the one from the search
        final CallSet callSetFromGet = client.variants.getCallSet(callSetId);
        assertThat(callSetFromGet).isNotNull();
        assertThat(callSetFromGet.getId()).isEqualTo(callSetId);
        assertThat(callSetFromGet).isEqualTo(callSetFromSearch);
    }

    /**
     * Test getting a call set with an invalid ID.  It should fail with NOT_FOUND.
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void getCallSetWithInvalidIDShouldFail() throws AvroRemoteException {
        final String nonexistentCallSetId = Utils.randomId();

        // fetch the CallSet with that ID
        final GAWrapperException gae =
                Utils.catchGAWrapperException(() -> client.variants.getCallSet(nonexistentCallSetId));
        assertThat(gae.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }


}
