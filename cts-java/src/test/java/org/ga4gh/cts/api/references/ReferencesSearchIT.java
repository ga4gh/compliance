package org.ga4gh.cts.api.references;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.assertj.core.api.StrictAssertions;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.variants.VariantsTests;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReferencesRequest;
import org.ga4gh.methods.SearchReferencesResponse;
import org.ga4gh.models.Reference;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Compliance tests relating to searching reference bases.
 *
 * @author Herb Jellinek
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class ReferencesSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Search for chr1 of GRCh37 by MD5 checksum (<tt>1b22b98cdeb4a9304cb5d48026a85128</tt>)
     * and then fetch that same reference by ID.
     * <ul>
     *  <li>Query 1: <pre>/references/search md5checksums: [md5checksum] pageSize: 1</pre></li>
     *  <li>Test 1: assert that the result is a {@link SearchReferencesResponse}
     *  containing an array of {@link Reference} objects. Array must be of length (what??).
     * Assert that every Reference has <pre>length == 249250621 AND md5checksum == (md5checksum) AND
     * ncbiTaxonId == 9606 (human)</pre></li>
     * <li>Query 2: <pre>/references/(ref ID)</pre></li>
     * <li>Test 2: assert that the returned {@link Reference} has <pre>ID == ref ID</pre></li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchForExpectedReferences() throws AvroRemoteException {

//        final String expectedMd5 = "1b22b98cdeb4a9304cb5d48026a85128";
        final String expectedMd5 = "TODO";
        final int expectedLength = 8050;
//        final int expectedLength = 249250621;
        final int expectedTaxonId = 9606;
        final int expectedRefs = 1;

        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                                         .setMd5checksums(aSingle(expectedMd5))
                                         .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferences();
        assertThat(refs).hasSize(expectedRefs);

        refs.stream().forEach(ref -> StrictAssertions.assertThat(ref.getLength()).isEqualTo(expectedLength));
        refs.stream().forEach(ref -> StrictAssertions.assertThat(ref.getMd5checksum()).isEqualTo(expectedMd5));
        refs.stream().forEach(ref -> StrictAssertions.assertThat(ref.getNcbiTaxonId()).isEqualTo(expectedTaxonId));

        // do query 2 and test 2
        // open-coded loop because it's awkward to deal with possible exceptions using filter syntax
        for (Reference ref : refs) {
            final String id = ref.getId();
            final Reference fetchedRef = client.references.getReference(id);
            StrictAssertions.assertThat(fetchedRef.getId()).isEqualTo(id);
        }
    }



}
