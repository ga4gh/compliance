package org.ga4gh.cts.api.variantAnnotation;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.VariantAnnotation;
import org.ga4gh.models.VariantAnnotationSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughVariantAnnotations() throws AvroRemoteException {
        final long start = 10177;
        final long end = 11008;

        // Find a compliance variantAnnotationSet id.
        final String variantAnnotationSetId = Utils.getVariantAnnotationSetId(client);
        final List<VariantAnnotation> listOfVariantAnnotations = Utils.getAllVariantAnnotationsInRange(client, variantAnnotationSetId, start, end);

        // We will remove VariantAnnotations from this Set and assert at the end that we have zero
        final Set<VariantAnnotation> setOfVariantAnnotations = new HashSet<>(listOfVariantAnnotations);
        assertThat(listOfVariantAnnotations).hasSize(setOfVariantAnnotations.size());

        // Page through the variantAnnotations using the same query parameters.
        String pageToken = null;
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
            final List<VariantAnnotation> pageOfVariantAnnotations = pageResp.getVariantAnnotations();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfVariantAnnotations).hasSize(1);
            assertThat(setOfVariantAnnotations).contains(pageOfVariantAnnotations.get(0));

            setOfVariantAnnotations.remove(pageOfVariantAnnotations.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfVariantAnnotations).isEmpty();
    }
}
