package org.ga4gh.cts.api.variants;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.VariantServiceOuterClass.SearchVariantSetsRequest;
import ga4gh.VariantServiceOuterClass.SearchVariantSetsResponse;
import ga4gh.Variants.VariantSet;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Methods that test <tt>/variantsets/search</tt>.
 *
 * @author Herb Jellinek
 */
@Category(VariantsTests.class)
@RunWith(JUnitParamsRunner.class)
public class VariantSetsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Fetch variant sets and make sure we get some.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingVariantSetsReturnsSome() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder().setDatasetId(TestData.getDatasetId()).build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> sets = resp.getVariantSetsList();
        assertThat(sets).isNotEmpty();
        sets.stream().forEach(vs -> assertThat(vs.getMetadataList()).isNotNull());
    }

    /**
     * Check that we receive the expected number of {@link VariantSet}s.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkExpectedVariantSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final int expectedNumberOfVariantSets = 3;

        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSetsList();
        assertThat(variantSets).hasSize(expectedNumberOfVariantSets);
    }

    /**
     * Fetch variant sets and make sure they're well-formed.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingVariantSetsReturnsWellFormed() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder().setDatasetId(TestData.getDatasetId()).build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> sets = resp.getVariantSetsList();

        sets.stream().forEach(vs -> assertThat(vs.getMetadataList()).isNotNull());
        sets.stream().forEach(vs -> assertThat(vs.getReferenceSetId()).isNotNull());
        sets.stream().forEach(vs -> assertThat(vs.getDatasetId()).isEqualTo(TestData.getDatasetId()));
        sets.stream().forEach(vs -> assertThat(vs.getId()).isNotNull());
    }

}
