package org.ga4gh.cts.api.variantAnnotation;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.VariantAnnotationSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

    /**
     * Check to see if variant annotation sets can be gotten by their ID.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkGetVariantAnnotationSetById() throws AvroRemoteException {
        // Find a compliance variant annotation set ID using search
        final String variantAnnotationSetId = Utils.getVariantAnnotationSetId(client);

        // Get a variant annotation set using GET
        final VariantAnnotationSet vSet = client.variantAnnotations.getVariantAnnotationSet(variantAnnotationSetId);

        assertThat(vSet).isNotNull();
        assertThat(vSet.getId()).isEqualTo(variantAnnotationSetId);

    }
    /**
     * Check to see if a variant annotation set contains the expected analysis infromation.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkVariantAnnotationAnalysis() throws AvroRemoteException, ParseException {

        // Seek a list of VariantAnnotationSets for the compliance dataset.
        final List<VariantAnnotationSet> variantAnnotationSets =  Utils.getAllVariantAnnotationSets(client);

        // Use the first VariantAnnotationSet
        final VariantAnnotationSet vSet = variantAnnotationSets.get(0);

        // Check the Analysis record within the VariantAnnotationSet matches the test data
        final String name        = "compliance1";
        final String description = "variant annotation test data";
        final String created     = "2015-11-18";
        // TODO make a more robust ISO8601 parser
        // Date formatting can be ISO compliant but still break these
        // However, the second, df2 matches python's datetime.isoformat function
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String software    = "SnpEff";

        assertThat(vSet.getAnalysis().getName()).isEqualTo(name);
        assertThat(vSet.getAnalysis().getDescription()).isEqualTo(description);
        assertThat(df2.parse(vSet.getAnalysis().getCreated())).isEqualTo(df1.parse(created));
        assertThat(vSet.getAnalysis().getSoftware().get(0)).isEqualTo(software);

    }
}
