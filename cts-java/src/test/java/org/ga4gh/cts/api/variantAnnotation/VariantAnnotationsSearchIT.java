package org.ga4gh.cts.api.variantAnnotation;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.VariantSet;
import org.ga4gh.models.VariantAnnotationSet;
import org.ga4gh.models.VariantAnnotation;
import org.ga4gh.models.TranscriptEffect;
import org.ga4gh.models.AlleleLocation;
import org.ga4gh.models.AnalysisResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

/**
 * Tests dealing with searching for VariantAnnotations.
 *
 */
@Category(VariantAnnotationTests.class)
public class VariantAnnotationsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * For every {@link VariantAnnotation} in the {@link List}, call the {@link Consumer}.
     * @param list of variantannotations to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllVariantAnnotations(List<VariantAnnotation> variantAnnotations, Consumer<VariantAnnotation> cons) {
        variantAnnotations.stream().forEach(cons::accept);
    }


    /**
     * For every {@link TranscriptEffect} in the {@link VariantAnnotation}s in the {@link List}, call the {@link Consumer}.
     * @param list of VariantAnnotations to test
     * @param cons the test ({@link Consumer}) to run
     */
    private void checkAllTranscriptEffects(List<VariantAnnotation> variantAnnotations, Consumer<TranscriptEffect> cons) {
        variantAnnotations.stream().forEach(v -> v.getTranscriptEffects()
                                        .stream()
                                        .forEach(cons::accept));
    }

    /**
     * Check VariantAnnotation results.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
    */
    @Test
    public void checkSearchingVariantAnnotations() throws AvroRemoteException {

        final long start = 10177;
        final long end = 11008;

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
        
        final List<VariantAnnotation> variantAnnotations = resp.getVariantAnnotations();
        assertThat(variantAnnotations).isNotEmpty();
 
       //Check the returned values are as expected for all annotations in the list.
       checkAllVariantAnnotations(variantAnnotations, 
                                  v -> assertThat(v.getVariantId()).isNotNull());

       checkAllVariantAnnotations(variantAnnotations, 
                                  v -> assertThat(v.getVariantAnnotationSetId()).isEqualTo(variantAnnotationSetId));

       //Check transcriptEffect record has values for required fields for all annotations in the list.
       checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getFeatureId()).isNotNull());
       checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getAlternateBases()).isNotNull());
       checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getImpact()).isNotNull());
       checkAllTranscriptEffects(variantAnnotations, t -> assertThat(t.getEffects()).isNotNull());

    }

}
