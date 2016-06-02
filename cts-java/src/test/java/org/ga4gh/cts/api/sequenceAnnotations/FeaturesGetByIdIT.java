package org.ga4gh.cts.api.sequenceAnnotations;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import ga4gh.Common.Strand;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesRequest;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesResponse;
import ga4gh.SequenceAnnotations.Feature;
import junitparams.JUnitParamsRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for <tt>GET /features/{id}</tt>.
 *
 * @author Herb Jellinek
 */
@Category(SequenceAnnotationTests.class)
@RunWith(JUnitParamsRunner.class)
public class FeaturesGetByIdIT {

    private static Client client = new Client(URLMAPPING.getInstance());


    /**
     * Verify that Features that we obtain by way of {@link SearchFeaturesRequest} match the ones
     * we get via <tt>GET /features/{id}</tt>.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFeaturesGetResultsMatchSearchResults() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 0;
        final long end = 1000000;
        final String parentId = "";
        final int expectedNumberOfFeatures = 100;

        final String featureSetId = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(featureSetId)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId)
                        .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final List<Feature> features = fResp.getFeaturesList();

        assertThat(features).hasSize(expectedNumberOfFeatures);

        for (final Feature featureFromSearch : features) {
            final Feature featureFromGet = client.sequenceAnnotations.getFeature(featureFromSearch.getId());
            assertThat(featureFromGet).isNotNull();

            assertThat(featureFromGet).isEqualTo(featureFromSearch);
        }

    }

    /**
     * Verify that the first gene obtained via <tt>GET /features/{id}</tt> has field values as expected
     * from reading the source GFF3 record.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFetchedCorrectGene() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 0;
        final long end = 100;
        final String parentId = "";
        final String featureType = "gene";

        final String featureSetId = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(featureSetId)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId)
                        .addFeatureTypes(featureType)
                        .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final Feature geneFromSearch = fResp.getFeatures(0);
        final Feature gene = client.sequenceAnnotations.getFeature(geneFromSearch.getId());

        assertThat(gene).isNotNull();
        assertThat(gene.getId()).isNotEmpty();

        assertThat(gene.getReferenceName()).isEqualTo("ref_brca1");
        assertThat(gene.getStart()).isEqualTo(1);
        assertThat(gene.getEnd()).isEqualTo(81189);
        assertThat(gene.getParentId()).isEqualTo("");
        assertThat(gene.getChildIdsList()).hasSize(31);
        assertThat(gene.getStrand()).isEqualTo(Strand.NEG_STRAND);

        assertThat(gene.getFeatureType().getTerm()).isEqualTo("gene");
        assertThat(gene.getFeatureType().getId()).isEqualTo("SO:0000704");

        assertThat(gene.getAttributes().getVals().get("gene_name").getValues(0).getStringValue()).isEqualTo("BRCA1");
        assertThat(gene.getAttributes().getVals().get("gene_id").getValues(0).getStringValue()).isEqualTo("ENSG00000012048.15");
    }

}

