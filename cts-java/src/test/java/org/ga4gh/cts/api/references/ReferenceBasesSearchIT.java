package org.ga4gh.cts.api.references;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.reads.ReadsTests;
import org.ga4gh.methods.*;
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
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReferenceBasesSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Retrieve all {@link Reference} objects by passing in an empty list of MD5 checksums. Check
     * that each returned {@link Reference} has a valid(-looking) MD5.  (In the future, perhaps this
     * test should compute the MD5 and compare it to what's returned.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void getAllReferencesWithEmptyMd5List() throws AvroRemoteException {

        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                                       .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        assertThat(resp).isNotNull();
        final List<Reference> refs = resp.getReferences();
        assertThat(refs).isNotEmpty();

        refs.stream().forEach(ref -> assertThat(Utils.looksLikeValidMd5(ref.getMd5checksum()))
                .isTrue());
    }

    /**
     * Reference bases.  Searches for reference by MD5 checksum and then fetches 10 bases for
     * that reference at offset 150.
     * <ul>
     *     <li>Query 1: <pre>/references/search md5checksums: [1b22b98cdeb4a9304cb5d48026a85128]
     *     pageSize: 1</pre></li>
     *     <li>Test 1: assert that we received a {@link SearchReferencesResponse} object</li>
     *     <li>Query 2: <pre>/references/(ref id)/bases start: 150 end: 160</pre></li>
     *     <li>Test 2: assert that we received a {@link Reference} object with fields
     *     <pre>offset == 150 AND sequence == "ACCCTAACCC"</pre></li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchForExpectedReferenceBases() throws AvroRemoteException {
//        final String expectedMd5 = "1b22b98cdeb4a9304cb5d48026a85128";
        final String expectedMd5 = "TODO";
        final long start = 150;
        final long end = 160;

        final long expectedOffset = 150;
        final String expectedSequence = "ACCCTAACCC";

        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                                         .setMd5checksums(aSingle(expectedMd5))
                                         .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        assertThat(resp).isNotNull();
        final List<Reference> refs = resp.getReferences();
        assertThat(refs).isNotEmpty();
        refs.stream().forEach(ref -> assertThat(ref.getMd5checksum()).isEqualTo(expectedMd5));

        for (Reference ref : refs) {
            // query 2
            final ListReferenceBasesRequest basesReq =
                    ListReferenceBasesRequest.newBuilder()
                                             .setStart(start)
                                             .setEnd(end)
                                             .build();
            final ListReferenceBasesResponse basesResp =
                    client.references.getReferenceBases(ref.getId(), basesReq);
            assertThat(basesResp.getOffset()).isEqualTo(expectedOffset);
            assertThat(basesResp.getSequence()).isEqualTo(expectedSequence);
        }
    }


}
