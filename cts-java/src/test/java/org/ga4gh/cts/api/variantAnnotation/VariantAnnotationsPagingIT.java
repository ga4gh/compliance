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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsResponse;
import ga4gh.AlleleAnnotations.VariantAnnotation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/variantannotations/search</tt> paging behavior.
 *
 */
@Category(VariantAnnotationTests.class)
public class VariantAnnotationsPagingIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the variantannotations we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.VariantAnnotations#searchVariantAnnotations(SearchVariantAnnotationsRequest)}.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughVariantAnnotations() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 10177;
        final long end = 11008;

        // Find a compliance variantAnnotationSet id.

        final String variantAnnotationSetId = Utils.getVariantAnnotationSetByName(client, TestData.VARIANT_ANNOTATION_SET_NAMES.get(0)).getId();
        final List<VariantAnnotation> listOfVariantAnnotations = Utils.getAllVariantAnnotationsInRange(client, variantAnnotationSetId, start, end);

        // We will remove VariantAnnotations from this Set and assert at the end that we have zero
        final Set<VariantAnnotation> setOfVariantAnnotations = new HashSet<>(listOfVariantAnnotations);
        assertThat(listOfVariantAnnotations).hasSize(setOfVariantAnnotations.size());

        // Page through the variantAnnotations using the same query parameters.
        String pageToken = "";
        for (VariantAnnotation ignored : listOfVariantAnnotations) {
            final SearchVariantAnnotationsRequest pageReq =
                    SearchVariantAnnotationsRequest.newBuilder()
                                         .setVariantAnnotationSetId(variantAnnotationSetId)
                                         .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                                         .setStart(start).setEnd(end)
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchVariantAnnotationsResponse pageResp = client.variantAnnotations.searchVariantAnnotations(pageReq);
            final List<VariantAnnotation> pageOfVariantAnnotations = pageResp.getVariantAnnotationsList();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfVariantAnnotations).hasSize(1);
            assertThat(setOfVariantAnnotations).contains(pageOfVariantAnnotations.get(0));

            setOfVariantAnnotations.remove(pageOfVariantAnnotations.get(0));
        }

        assertThat(pageToken).isEmpty();
        assertThat(setOfVariantAnnotations).isEmpty();
    }
}
