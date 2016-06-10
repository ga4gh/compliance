package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.References.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Compliance tests relating to searching reference bases.
 *
 * @author Maciek Smuga-Otto
 */
@RunWith(JUnitParamsRunner.class)
@Category(ReferencesTests.class)
public class ReferencesSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Fetch all references in the reference set, check there are some.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingAllReferencesReturnsSome() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).isNotEmpty();
    }

    /**
     * Fetch references and make sure they're well-formed.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingAllReferencesReturnsWellFormed() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> sets = resp.getReferencesList();

        sets.stream().forEach(rs -> assertThat(rs.getId()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getName()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getLength()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getMd5Checksum()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getSourceAccessionsList()).isNotNull());
    }

    /**
     * Fetch a reference by accession, check its attributes.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkReferenceFoundByAccession() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setAccession(TestData.REFERENCE_BRCA1_ACCESSION)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).hasSize(1);

        final Reference ref = refs.get(0);
        assertThat(ref.getName()).isEqualTo(TestData.REFERENCE_BRCA1_NAME);
        assertThat(ref.getLength()).isEqualTo(TestData.REFERENCE_BRCA1_LENGTH);
        assertThat(ref.getMd5Checksum()).isEqualTo(TestData.REFERENCE_BRCA1_MD5_CHECKSUM);
        assertThat(ref.getSourceAccessionsList()).isEqualTo(aSingle(TestData.REFERENCE_BRCA1_ACCESSION));
    }

    /**
     * Check that no reference is fetched on an invalid accession.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingReferencesWithInvalidAccessionReturnsEmpty() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final String accession = Utils.randomName();
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setAccession(accession)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).isEmpty();
    }

    /**
     * Fetch a reference by its MD5 checksum, check its attributes.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkReferenceFoundByMD5Checksum() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setMd5Checksum(TestData.REFERENCE_BRCA1_MD5_CHECKSUM)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferencesList();
        assertThat(refs).hasSize(1);

        final Reference ref = refs.get(0);
        assertThat(ref.getName()).isEqualTo(TestData.REFERENCE_BRCA1_NAME);
        assertThat(ref.getLength()).isEqualTo(TestData.REFERENCE_BRCA1_LENGTH);
        assertThat(ref.getMd5Checksum()).isEqualTo(TestData.REFERENCE_BRCA1_MD5_CHECKSUM);
        assertThat(ref.getSourceAccessionsList()).isEqualTo(aSingle(TestData.REFERENCE_BRCA1_ACCESSION));
    }
}
