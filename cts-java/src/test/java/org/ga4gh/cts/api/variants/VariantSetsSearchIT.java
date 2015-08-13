package org.ga4gh.cts.api.variants;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.SearchVariantSetsRequest;
import org.ga4gh.methods.SearchVariantSetsResponse;
import org.ga4gh.models.VariantSet;
import org.ga4gh.models.VariantSetMetadata;
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
     * Search variant sets. Fetches variant sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search &lt;dataset ID&gt;</pre></li>
     * <li>Test 1: assert that we received a {@link SearchVariantSetsResponse} containing an array of
     * {@link VariantSet} of length &gt; 0 (should have a definite #, actually)</li>
     * <li>Test 2: assert that the 'metadata' field of that {@link VariantSet} is of type
     * {@link VariantSetMetadata}.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchVariantSets() throws AvroRemoteException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder().setDatasetId(TestData.getDatasetId()).build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> sets = resp.getVariantSets();
        assertThat(sets).isNotEmpty();
        sets.stream().forEach(vs -> assertThat(vs.getMetadata()).isNotNull());
    }

}
