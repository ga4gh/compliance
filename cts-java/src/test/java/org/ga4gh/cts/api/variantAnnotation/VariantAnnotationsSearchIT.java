package org.ga4gh.cts.api.variantAnnotation;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.function.Consumer;

import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsResponse;
import ga4gh.AlleleAnnotations.TranscriptEffect;
import ga4gh.AlleleAnnotations.VariantAnnotation;
import ga4gh.Metadata.OntologyTerm;
import ga4gh.Variants.Variant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests dealing with searching for VariantAnnotations.
 *
 */
@Category(VariantAnnotationTests.class)
public class VariantAnnotationsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    // Define test region
    final long start = 10177;
    final long end = 11008;
    final int expectedNumberOfAnnotations = 11;

    /**
     * For every {@link VariantAnnotation} in the {@link List}, call the {@link Consumer}.
     * @param variantAnnotations list of variantannotations to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllVariantAnnotations(List<VariantAnnotation> variantAnnotations, Consumer<VariantAnnotation> cons) {
        variantAnnotations.stream().forEach(cons::accept);
    }


    /**
     * For every {@link TranscriptEffect} in the {@link VariantAnnotation}s in the {@link List}, call the {@link Consumer}.
     * @param variantAnnotations list of VariantAnnotations to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllTranscriptEffects(List<VariantAnnotation> variantAnnotations, Consumer<TranscriptEffect> cons) {
        variantAnnotations.stream().forEach(v -> v.getTranscriptEffectsList()
                                        .stream()
                                        .forEach(cons::accept));
    }

    /**
     * Check VariantAnnotation results.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingVariantAnnotations() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
 
        // Obtain a VariantAnnotationSet from the compliance dataset.
        final String variantAnnotationSetId =
                Utils.getVariantAnnotationSetByName(client, TestData.VARIANT_ANNOTATION_SET_NAMES.get(0)).getId();


        // Seek variant annotation records for the extracted VariantAnnotationSet.
        final SearchVariantAnnotationsRequest req =
                SearchVariantAnnotationsRequest.newBuilder()
                                               .setVariantAnnotationSetId(variantAnnotationSetId)
                                               .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                                               .setStart(start)
                                               .setEnd(end)
                                               .build();

        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);
        
        final List<VariantAnnotation> variantAnnotations = resp.getVariantAnnotationsList();

        //Check something was returned
        assertThat(variantAnnotations).isNotEmpty();

        //Check the correct number were returned
        assertThat(variantAnnotations).hasSize(expectedNumberOfAnnotations);

        //Check a variant id is returned for each record.
        checkAllVariantAnnotations(variantAnnotations,
                                   v -> assertThat(v.getId()).isNotNull());
 
        //Check a variant id is returned for each record.
        checkAllVariantAnnotations(variantAnnotations, 
                                   v -> assertThat(v.getVariantId()).isNotNull());

        //Check the returned variant annotation set value is as expected for all annotations in the list.
        checkAllVariantAnnotations(variantAnnotations, 
                                   v -> assertThat(v.getVariantAnnotationSetId()).isEqualTo(variantAnnotationSetId));

    }

    /**
     * Check TranscriptEffects records have the appropriate mandatory data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkTranscriptEffects() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // Obtain a VariantAnnotationSet from the compliance dataset.
        final String variantAnnotationSetId = Utils.getVariantAnnotationSetId(client);


        // Seek variant annotation records for the extracted VariantAnnotationSet.
        final SearchVariantAnnotationsRequest req =
                SearchVariantAnnotationsRequest.newBuilder()
                                               .setVariantAnnotationSetId(variantAnnotationSetId)
                                               .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                                               .setStart(start)
                                               .setEnd(end)
                                               .build();

        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);

        final List<VariantAnnotation> variantAnnotations = resp.getVariantAnnotationsList();

        //Check transcriptEffect record has values for required fields for all annotations in the list.
        checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getFeatureId()).isNotNull());
        checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getAlternateBases()).isNotNull());
        checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getEffectsList()).isNotNull());

    }
// To be re-instated when features are available
//    /**
//     * Check filtering by feature id
//     *
//     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
//    */
//    @Test
//    public void checkSearchingVariantAnnotationsByFeature() throws AvroRemoteException {
//
//       final List<String> filterFeatures = aSingle("NR_046018.2");
//
//        // Obtain a VariantAnnotationSet from the compliance dataset.
//       final String variantAnnotationSetId = Utils.getVariantAnnotationSetId(client);
//
//
//        // Seek variant annotation records for the extracted VariantAnnotationSet.
//        final SearchVariantAnnotationsRequest req =
//                SearchVariantAnnotationsRequest.newBuilder()
//                                               .setVariantAnnotationSetId(variantAnnotationSetId)
//                                               .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
//                                               .setStart(start)
//                                               .setEnd(end)
//                                               .setFeatureIds(filterFeatures)
//                                               .build();
//
//        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);
//
//        final List<VariantAnnotation> variantAnnotations = resp.getVariantAnnotations();
//        assertThat(variantAnnotations).isNotEmpty();
//
//        //Check the correct number were returned
//        assertThat(variantAnnotations).hasSize(expectedNumberOfAnnotations);
//
//        //Check transcriptEffect records all list the required feature
//        checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getFeatureId()).isEqualTo(filterFeatures.get(0)));
//    }

    /**
     * Check returned annotation at specific location
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingSingleVariantAnnotation() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // Obtain a VariantAnnotationSet from the compliance dataset.
        final String variantAnnotationSetId =
                Utils.getVariantAnnotationSetByName(client, TestData.VARIANT_ANNOTATION_SET_NAMES.get(1)).getId();
        final long single_start = 69540;
        final long single_end = 69541;


        // Seek a single variant annotation record from the extracted VariantAnnotationSet.
        final SearchVariantAnnotationsRequest req =
                SearchVariantAnnotationsRequest.newBuilder()
                                               .setVariantAnnotationSetId(variantAnnotationSetId)
                                               .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                                               .setStart(single_start)
                                               .setEnd(single_end)
                                               .build();

        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);

        final List<VariantAnnotation> variantAnnotations = resp.getVariantAnnotationsList();
        assertThat(variantAnnotations).hasSize(1);

        //Check transcriptEffect records all contain the expected data.
        final String alternateBases = "G";
        final String featureId  = "NM_001005484.1";
        final String hgvsg      = "1:g.69541A>G";
        final String hgvsc      = "NM_001005484.1:c.451A>G";
        final String hgvsp      = "NM_001005484.1:p.Ser151Gly";
        final int cdnaStart     = 450;
        final int cdsStart      = 450;
        final int proteinStart  = 150;

        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getAlternateBases()).isEqualTo(alternateBases));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getFeatureId()).isEqualTo(featureId));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getHgvsAnnotation().getGenomic()).isEqualTo(hgvsg));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getHgvsAnnotation().getTranscript()).isEqualTo(hgvsc));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getHgvsAnnotation().getProtein()).isEqualTo(hgvsp));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getCdnaLocation().getStart()).isEqualTo(cdnaStart));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getCdsLocation().getStart()).isEqualTo(cdsStart));
        checkAllTranscriptEffects(variantAnnotations, t-> assertThat(t.getProteinLocation().getStart()).isEqualTo(proteinStart));
    }
    /**
     * Check the VariantIds contained in VariantAnnotations records return Variant records.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkVariantAnnotationVariants() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // Obtain a VariantAnnotationSet from the compliance dataset.
        final String variantAnnotationSetId =
                Utils.getVariantAnnotationSetByName(client, TestData.VARIANT_ANNOTATION_SET_NAMES.get(0)).getId();

        // Seek variant annotation records for the extracted VariantAnnotationSet.
        final SearchVariantAnnotationsRequest req =
                SearchVariantAnnotationsRequest.newBuilder()
                                               .setVariantAnnotationSetId(variantAnnotationSetId)
                                               .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                                               .setStart(start)
                                               .setEnd(end)
                                               .build();

        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);
        assertThat(resp.getVariantAnnotationsList()).isNotEmpty();
        //Check a valid variant id is returned for each record.
        for( VariantAnnotation variantAnn : resp.getVariantAnnotationsList() ){
            final String  variantId = variantAnn.getVariantId();

            // Look up Variant by id to check it exists
            Variant v = client.variants.getVariant(variantId);
            assertThat(v).isNotNull();
            assertThat(v.getId()).isEqualTo(variantId);
            assertThat(v.getStart()).isGreaterThanOrEqualTo(start);
        }
    }

    /**
     * Check whether filtering by effect ID finds the expected annotations.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkFilteringByEffectTerm() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final OntologyTerm term = OntologyTerm.newBuilder().setId("SO:0001819").setTerm("synonymous_variant").setSourceName("source").setSourceVersion("0").build();
        // Obtain a VariantAnnotationSet from the compliance dataset.
        final String variantAnnotationSetId =
                Utils.getVariantAnnotationSetByName(client, TestData.VARIANT_ANNOTATION_SET_NAMES.get(1)).getId();

        // Seek variant annotation records for the extracted VariantAnnotationSet.
        final SearchVariantAnnotationsRequest req =
                SearchVariantAnnotationsRequest.newBuilder()
                        .setVariantAnnotationSetId(variantAnnotationSetId)
                        .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                        .setStart(0)
                        .setEnd(100000)
                        .addEffects(term)
                        .build();

        final SearchVariantAnnotationsResponse resp = client.variantAnnotations.searchVariantAnnotations(req);

        // There are 7 records with the "synonymous_variant" term in the test data.
        assertThat(resp.getVariantAnnotationsList()).hasSize(7);
        //Check the transcript effect field is not empty for each annotation.
        for( VariantAnnotation variantAnn : resp.getVariantAnnotationsList() ){
            assertThat(variantAnn.getTranscriptEffectsList()).isNotEmpty();
        }
    }


}
