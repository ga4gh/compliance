package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.cts.api.TestData;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.References.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Compliance tests relating to searching reference bases.
 *
 * @author Maciek Smuga-Otto
 */
@Category(ReferencesTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReferenceBasesSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Retrieve all {@link Reference} objects for the default {@link ReferenceSet}. Check
     * that each returned {@link Reference} has a valid-looking MD5.  (In the future, perhaps this
     * test should compute the MD5 and compare it to what's returned.)
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkMD5ChecksumAppearanceOfAllReferences() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);

        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);
        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).isNotEmpty();

        refs.stream().forEach(ref -> assertThat(Utils.looksLikeValidMd5(ref.getMd5Checksum()))
                .isTrue());
    }

    /**
     * Fetch reference by accession and check a specified subset of its bases against the expected value.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkBaseSequenceForReferenceFoundByAccession() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final long start = 150;
        final long end = 160;
        final String referenceBrcaSubsequence150To159 = "GGGTTTATGA";
        final long expectedOffset = 150;

        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setAccession(TestData.REFERENCE_BRCA1_ACCESSION)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);
        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).hasSize(1);
        final Reference ref = refs.get(0);

        final ListReferenceBasesRequest basesReq =
                ListReferenceBasesRequest.newBuilder()
                        .setStart(start)
                        .setEnd(end)
                        .build();
        final ListReferenceBasesResponse basesResp =
                client.references.getReferenceBases(ref.getId(), basesReq);
        assertThat(basesResp.getOffset()).isEqualTo(expectedOffset);
        assertThat(basesResp.getSequence()).isEqualTo(referenceBrcaSubsequence150To159);
    }
}
