package org.ga4gh.cts.api.references;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReferenceSetsRequest;
import org.ga4gh.methods.SearchReferenceSetsResponse;
import org.ga4gh.models.ReferenceSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Reference sets-related compliance tests.
 *
 * @author Maciek Smuga-Otto
 */
@Category(ReferencesTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReferenceSetsSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Fetch reference sets and make sure we get some.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingReferenceSetsReturnsSome() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> sets = resp.getReferenceSets();
        assertThat(sets).isNotEmpty();
    }


    /**
     * Fetch reference sets and make sure they're well-formed.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingReferenceSetsReturnsWellFormed() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> sets = resp.getReferenceSets();

        sets.stream().forEach(this::checkRefSetRequiredFields);
    }

    /**
     * Fetch a reference set by accession, check its attributes.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceSetFoundByAccession() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAccession(TestData.REFERENCESET_ACCESSION)
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSets();
        assertThat(refSets).hasSize(1);

        final ReferenceSet refSet = refSets.get(0);
        checkRefSetConstants(refSet);
    }

    /**
     * Fetch a reference set by md5 checksum, check its attributes
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceSetFoundByMD5Checksum() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setMd5checksum(TestData.REFERENCESET_MD5_CHECKSUM)
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSets();
        assertThat(refSets).hasSize(1);

        final ReferenceSet refSet = refSets.get(0);
        checkRefSetConstants(refSet);
    }

    /**
     * Fetch a reference set by assemblyId, check its attributes.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceSetFoundByAssemblyId() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAssemblyId(TestData.REFERENCESET_ASSEMBLY_ID)
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSets();
        assertThat(refSets).hasSize(1);

        final ReferenceSet refSet = refSets.get(0);
        checkRefSetConstants(refSet);
    }

    /**
     * Check the assembly ID, MD5 value, and accessions of a given {@link ReferenceSet}.
     * @param refSet the {@link ReferenceSet}  to check
     */
    private void checkRefSetConstants(ReferenceSet refSet) {
        assertThat(refSet.getAssemblyId()).isEqualTo(TestData.REFERENCESET_ASSEMBLY_ID);
        assertThat(refSet.getMd5checksum()).isEqualTo(TestData.REFERENCESET_MD5_CHECKSUM);
        assertThat(refSet.getSourceAccessions().get(0)).isEqualTo(TestData.REFERENCESET_ACCESSION);
    }

    /**
     * Check the assembly ID, MD5 value, and accessions of a {@link ReferenceSet} is not null.
     * @param refSet the {@link ReferenceSet}  to check
     */
    private void checkRefSetRequiredFields(ReferenceSet refSet) {
        assertThat(refSet.getId()).isNotNull();
        assertThat(refSet.getMd5checksum()).isNotNull();
        assertThat(refSet.getSourceAccessions()).isNotEmpty();
        assertThat(refSet.getIsDerived()).isNotNull();
    }
    /**
     * Verify that all found references identify the right species (NCBI Taxonomy ID).
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkTaxonIdOfReferenceSets() throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSets();
        assertThat(refSets).isNotEmpty();

        refSets.stream().forEach(rs -> assertThat(rs.getNcbiTaxonId()).isEqualTo(TestData.REFERENCESET_TAXON_ID));
    }
}
