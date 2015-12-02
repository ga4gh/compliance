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
import org.ga4gh.models.AnalysisResult;
import org.ga4gh.models.Analysis;
import org.ga4gh.models.OntologyTerm;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

/**
 * Tests dealing with searching for VariantAnnotationSets.
 *
 */
@Category(VariantAnnotationTests.class)
public class VariantAnnotationSetsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check VariantAnnotationSets contain some analysis data and return the expected VariantSetId.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingVariantAnnotationSets() throws AvroRemoteException {

        // Seek a list of VariantAnnotationSets for the compliance dataset.
        final List<VariantAnnotationSet> variantAnnotationSets =  Utils.getAllVariantAnnotationSets(client);

        // check some are available
        assertThat(variantAnnotationSets).isNotEmpty();


        // The analysis object should hold exhaustive data on the annotation method so cannot be missing/empty.
        variantAnnotationSets.stream()
                             .forEach(vas -> assertThat(vas.getAnalysis().getId())
                             .isNotNull());

        // Check the variantSetId is as expected.
        variantAnnotationSets.stream()
                             .forEach(vas -> assertThat(vas.getVariantSetId()).isNotNull());

    }
}
