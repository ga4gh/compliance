package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import ga4gh.Variants.*;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.BioMetadata.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void searchForExpectedCallSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // Find variant sets only using datasetID
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        // Some amount of variant sets should be returned
        assertThat(vResp.getVariantSetsList()).isNotEmpty();

        // Find callsets for each of the variant sets.
        for (VariantSet set : vResp.getVariantSetsList()) {
            final String id = set.getId();

            final SearchCallSetsRequest csReq =
                    SearchCallSetsRequest.newBuilder()
                                         .setVariantSetId(id)
                                         .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);
            final List<CallSet> callSets = csResp.getCallSetsList();

            // Ensure that if a CallSet is returned, one of the variantSetIds it refers to
            // is the ID we are interested in.
            callSets.stream().forEach(cs -> assertThat(cs.getVariantSetIdsList()).contains(id));
        }
    }

    /**
     * Make sure VariantSet records exist for each CallSet.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void searchCallSetsByVariantSet() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // Get all the variant sets
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        // Make sure there are variant sets there
        assertThat(vResp.getVariantSetsList()).isNotEmpty();
        for (VariantSet set : vResp.getVariantSetsList()) {
            final String id = set.getId();

            // Find CallSets that go with this VariantSet
            final SearchCallSetsRequest csReq =
                    SearchCallSetsRequest.newBuilder()
                            .setVariantSetId(id)
                            .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);
            for (CallSet cs : csResp.getCallSetsList()) {
                for (String vsid : cs.getVariantSetIdsList()) {
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
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void getCallSetWithValidIDShouldSucceed() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSetsList()).isNotEmpty();

        // grab the first VariantSet and use it as source of CallSets
        final VariantSet variantSet = vResp.getVariantSetsList().get(0);
        final String variantSetId = variantSet.getId();

        final SearchCallSetsRequest callSetsSearchRequest =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .build();
        final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);

        // grab one of the CallSets returned from the search
        assertThat(csResp.getCallSetsList()).isNotEmpty();
        final CallSet callSetFromSearch = csResp.getCallSetsList().get(0);
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
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void getCallSetWithInvalidIDShouldFail() {
        final String nonexistentCallSetId = Utils.randomId();

        // fetch the CallSet with that ID
        final GAWrapperException gae =
                Utils.catchGAWrapperException(() -> client.variants.getCallSet(nonexistentCallSetId));
        assertThat(gae.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Tests to ensure that when requesting callsets using the BioSample Id filter that
     * only callsets with the given BioSample Id are returned.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void searchCallSetsByBioSampleId() throws GAWrapperException, UnirestException, InvalidProtocolBufferException {
        final BioSample bioSample = Utils.getBioSampleByName(client, TestData.BIOSAMPLE_NAME);

        // grab the first VariantSet and use it as source of CallSets
        final VariantSet variantSet = Utils.getVariantSetByName(client, TestData.VARIANTSET_NAME);
        final String variantSetId = variantSet.getId();

        final SearchCallSetsRequest callSetsSearchRequest =
                SearchCallSetsRequest.newBuilder()
                        .setVariantSetId(variantSetId)
                        .setBioSampleId(bioSample.getId())
                        .build();
        final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);
        assertThat(csResp.getCallSetsList()).isNotEmpty();
        for (CallSet cs: csResp.getCallSetsList()) {
            assertThat(cs.getBioSampleId()).isEqualTo(bioSample.getId());
        }
    }
}
