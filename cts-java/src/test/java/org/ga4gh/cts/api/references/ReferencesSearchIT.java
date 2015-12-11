package org.ga4gh.cts.api.references;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
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
 * @author Maciek Smuga-Otto
 */
@RunWith(JUnitParamsRunner.class)
@Category(ReferencesTests.class)
public class ReferencesSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Fetch all references in the reference set, check there are some.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingAllReferencesReturnsSome() throws AvroRemoteException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferences();
        assertThat(refs).isNotEmpty();
    }

    /**
     * Fetch references and make sure they're well-formed.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingAllReferencesReturnsWellFormed() throws AvroRemoteException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> sets = resp.getReferences();

        sets.stream().forEach(rs -> assertThat(rs.getId()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getName()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getLength()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getMd5checksum()).isNotNull());
        sets.stream().forEach(rs -> assertThat(rs.getSourceAccessions()).isNotNull());
    }

    /**
     * Fetch a reference by accession, check its attributes.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceFoundByAccession() throws AvroRemoteException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setAccession(TestData.REFERENCE_BRCA1_ACCESSION)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferences();
        assertThat(refs).hasSize(1);

        final Reference ref = refs.get(0);
        assertThat(ref.getName()).isEqualTo(TestData.REFERENCE_BRCA1_NAME);
        assertThat(ref.getLength()).isEqualTo(TestData.REFERENCE_BRCA1_LENGTH);
        assertThat(ref.getMd5checksum()).isEqualTo(TestData.REFERENCE_BRCA1_MD5_CHECKSUM);
        assertThat(ref.getSourceAccessions().get(0)).isEqualTo(TestData.REFERENCE_BRCA1_ACCESSION);
    }

    /**
     * Check that no reference is fetched on an invalid accession.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingReferencesWithInvalidAccessionReturnsEmpty() throws AvroRemoteException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final String accession = Utils.randomName();
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setAccession(accession)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferences();
        assertThat(refs).isEmpty();
    }

    /**
     * Fetch a reference by its MD5 checksum, check its attributes.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceFoundByMD5Checksum() throws AvroRemoteException {
        final String referenceSetId =
                Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID);
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                        .setReferenceSetId(referenceSetId)
                        .setMd5checksum(TestData.REFERENCE_BRCA1_MD5_CHECKSUM)
                        .build();
        final SearchReferencesResponse resp = client.references.searchReferences(req);

        final List<Reference> refs = resp.getReferences();
        assertThat(refs).hasSize(1);

        final Reference ref = refs.get(0);
        assertThat(ref.getName()).isEqualTo(TestData.REFERENCE_BRCA1_NAME);
        assertThat(ref.getLength()).isEqualTo(TestData.REFERENCE_BRCA1_LENGTH);
        assertThat(ref.getMd5checksum()).isEqualTo(TestData.REFERENCE_BRCA1_MD5_CHECKSUM);
        assertThat(ref.getSourceAccessions().get(0)).isEqualTo(TestData.REFERENCE_BRCA1_ACCESSION);
    }
}
