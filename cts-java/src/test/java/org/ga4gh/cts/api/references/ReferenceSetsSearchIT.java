package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import ga4gh.Common.GAException;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.References.*;
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
    public void checkSearchingReferenceSetsReturnsSome() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> sets = resp.getReferenceSetsList();
        assertThat(sets).isNotEmpty();
    }


    /**
     * Fetch reference sets and make sure they're well-formed.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingReferenceSetsReturnsWellFormed() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> sets = resp.getReferenceSetsList();

        sets.stream().forEach(this::checkRefSetConstants);
    }

    /**
     * Fetch a reference set by accession, check its attributes.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkReferenceSetFoundByAccession() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAccession(TestData.REFERENCESET_ACCESSIONS.get(0))
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSetsList();
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
    public void checkReferenceSetFoundByMD5Checksum() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setMd5Checksum(TestData.REFERENCESET_MD5_CHECKSUM)
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSetsList();
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
    public void checkReferenceSetFoundByAssemblyId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAssemblyId(TestData.REFERENCESET_ASSEMBLY_ID)
                        .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSetsList();
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
        assertThat(refSet.getMd5Checksum()).isEqualTo(TestData.REFERENCESET_MD5_CHECKSUM);
        assertThat(refSet.getSourceAccessionsList()).isEqualTo(TestData.REFERENCESET_ACCESSIONS);
    }

    /**
     * Verify that all found references identify the right species (NCBI Taxonomy ID).
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkTaxonIdOfReferenceSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);

        final List<ReferenceSet> refSets = resp.getReferenceSetsList();
        assertThat(refSets).isNotEmpty();

        refSets.stream().forEach(rs -> assertThat(rs.getNcbiTaxonId()).isEqualTo(TestData.REFERENCESET_TAXON_ID));
    }
}
