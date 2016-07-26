package org.ga4gh.cts.api.variantAnnotation;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ga4gh.AlleleAnnotations.VariantAnnotationSet;

import static org.assertj.core.api.Assertions.assertThat;

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */

    @Test
    public void checkSearchingVariantAnnotationSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */

    @Test
    public void checkGetVariantAnnotationSetById() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkVariantAnnotationAnalysis() throws ParseException, InvalidProtocolBufferException, UnirestException, GAWrapperException {

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
        assertThat(vSet.getAnalysis().getSoftware(0)).isEqualTo(software);

    }
}
