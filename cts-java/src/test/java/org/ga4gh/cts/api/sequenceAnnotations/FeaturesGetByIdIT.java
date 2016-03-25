package org.ga4gh.cts.api.sequenceAnnotations;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.sequenceAnnotations.SequenceAnnotationTests;
import org.ga4gh.methods.*;
import org.ga4gh.models.Feature;
import org.ga4gh.models.Strand;
import org.ga4gh.models.FeatureSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFeaturesGetResultsMatchSearchResults() throws AvroRemoteException {
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
        final List<Feature> features = fResp.getFeatures();

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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkFetchedCorrectGene() throws AvroRemoteException {
        final long start = 0;
        final long end = 100;
        final String parentId = "";
        final String featureType = "gene";
        final List<String> featureTypes = new ArrayList<String>(){{add(featureType);}};

        final String featureSetId = Utils.getFeatureSetId(client);

        final SearchFeaturesRequest fReq =
                SearchFeaturesRequest.newBuilder()
                        .setFeatureSetId(featureSetId)
                        .setReferenceName(TestData.REFERENCE_NAME)
                        .setStart(start).setEnd(end)
                        .setParentId(parentId)
                        .setFeatureTypes(featureTypes)
                        .build();
        final SearchFeaturesResponse fResp = client.sequenceAnnotations.searchFeatures(fReq);
        final Feature geneFromSearch = fResp.getFeatures().get(0);
        final Feature gene = client.sequenceAnnotations.getFeature(geneFromSearch.getId());

        assertThat(gene).isNotNull();
        assertThat(gene.getId()).isNotEmpty();

        assertThat(gene.getReferenceName()).isEqualTo("ref_brca1");
        assertThat(gene.getStart()).isEqualTo(1);
        assertThat(gene.getEnd()).isEqualTo(81189);
        assertThat(gene.getParentId()).isEqualTo("");
        assertThat(gene.getChildIds()).hasSize(31);
        assertThat(gene.getStrand()).isEqualTo(Strand.NEG_STRAND);

        assertThat(gene.getFeatureType().getTerm()).isEqualTo("gene");
        assertThat(gene.getFeatureType().getId()).isEqualTo("SO:0000704");

        assertThat(gene.getAttributes().getVals().get("gene_name").get(0)).isEqualTo("BRCA1");
        assertThat(gene.getAttributes().getVals().get("gene_id").get(0)).isEqualTo("ENSG00000012048.15");
    }

}

