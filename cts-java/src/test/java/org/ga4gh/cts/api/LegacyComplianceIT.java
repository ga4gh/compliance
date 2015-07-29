package org.ga4gh.cts.api;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.*;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.reads.ReadsTests;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests are equivalents of the ones in <a href="https://github.com/ga4gh/compliance">the
 * GA4GH compliance repo</a>.  Those were written in JavaScript and were meant to execute against
 * a predefined set of servers, user-selectable at runtime.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class LegacyComplianceIT implements CtkLogs {

    private static final String DATASET_ID = "dataset1";

    /**
     * We often want our results one at a time.
     */
    private static final int PAGE_COUNT = 1;

    private static Client client;

    /**
     * Make it easy to create lists of a single {@link String} element, which we do a lot.
     * @param s the String
     * @return the resulting List&lt;String&gt;
     */
    private List<String> aSingle(String s) {
        return Collections.singletonList(s);
    }

    @BeforeClass
    public static void setupTransport() throws Exception {
        client = new Client(URLMAPPING.getInstance());
    }

    /**
     * Search read group sets.  Fetches read group sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/readgroupsets/search &lt;dataset ID&gt;</pre>
     * <li>Test 1: assert that we received a result of type {@link GASearchReadGroupSetsResponse},
     * and that every {@link GAReadGroupSet} it contains has field datasetId == &lt;dataset ID&gt;</li>
     * <li>Test 2: every {@link GAReadGroup} in that {@link GAReadGroupSet} has: an 'experiment'
     * of type GAExperiment; datasetId == &lt;dataset ID&gt;; a program of type {@link GAProgram}
     * which is not empty.
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchReadGroupSets() throws AvroRemoteException {
        final GASearchReadGroupSetsRequest req =
                GASearchReadGroupSetsRequest.newBuilder().
                        setDatasetIds(aSingle(DATASET_ID)).
                                                    build();
        final GASearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<GAReadGroupSet> readGroupSets = resp.getReadGroupSets();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getDatasetId()).isEqualTo(DATASET_ID));

        // use a local var to avoid varargs confusion:
        final GAProgram aNullOfTheRightType = null;
        for (GAReadGroupSet readGroupSet : readGroupSets) {
            for (GAReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup).isNotNull();
                assertThat(readGroup.getDatasetId()).isEqualTo(DATASET_ID);
                assertThat(readGroup.getPrograms()).isNotEmpty();
                assertThat(readGroup.getPrograms()).doesNotContain(aNullOfTheRightType);
            }
        }
    }

    /**
     * Search variant sets. Fetches variant sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search &lt;dataset ID&gt;</pre></li>
     * <li>Test 1: assert that we received a {@link GASearchVariantSetsResponse} containing an array of
     * {@link GAVariantSet} of length &gt; 0 (should have a definite #, actually)</li>
     * <li>Test 2: assert that the 'metadata' field of that {@link GAVariantSet} is of type
     * {@link GAVariantSetMetadata}.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchVariantSets() throws AvroRemoteException {
        final GASearchVariantSetsRequest req =
                GASearchVariantSetsRequest.newBuilder().setDatasetIds(aSingle(DATASET_ID)).build();
        final GASearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<GAVariantSet> sets = resp.getVariantSets();
        assertThat(sets).isNotEmpty();
        sets.stream().forEach(vs -> assertThat(vs.getMetadata()).isNotNull());
    }

    /**
     * Search variants.  Fetches variants from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search datasetIds: 1 </pre>(passed in)</li>
     * <li>Test 1: assert that we received a {@link GASearchVariantSetsResponse} containing
     * (how many? &gt; 0) {@link GAVariantSet} objects.  Get the ID of the first one.</li>
     * <li>Query 2: <pre>/variants/search variantSetIds: [variantSetId] referenceName: '22' start:
     *     51005353 end: 51015354 pageSize: 1</pre></li>
     * <li>Test 2: assert that the first result is of type {@link GAVariant} AND has reference name
     * == "22".</li>
     * <li>Test 3: assert that the <tt>calls</tt> field (a {@link GACall}) of that {@link GAVariant}
     * is not null.</li>
     * <li>Test 4: assert that the <tt>genotype</tt> field of the first {@link GACall} is an array
     * of integers.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchVariants() throws AvroRemoteException {
        final String referenceName = "3";
        final long start = 60156;
        final long end = 60383;  // should be eight variants

        final GASearchVariantSetsRequest req =
                GASearchVariantSetsRequest.newBuilder().
                        setDatasetIds(aSingle(DATASET_ID)).
                                                  build();
        final GASearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<GAVariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        final String id = variantSets.get(0).getId();

        final GASearchVariantsRequest vReq =
                GASearchVariantsRequest.newBuilder().
                        setVariantSetIds(aSingle(id)).
                        setReferenceName(referenceName).
                                               setStart(start).setEnd(end).
                                               build();
        final GASearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        final List<GAVariant> searchVariants = vResp.getVariants();
        final GAVariant firstVariant = searchVariants.get(0);
        assertThat(firstVariant).isNotNull();
        assertThat(firstVariant.getReferenceName()).isEqualTo(referenceName);

        assertThat(firstVariant.getCalls()).isNotNull();
        final GACall call = firstVariant.getCalls().get(0);
        assertThat(call).isNotNull();
        assertThat(call.getGenotype()).isNotEmpty();
    }

    /**
     * Search call sets.  Fetches call sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search datasetIds: (passed in)</pre></li>
     * <li>Test 1: assert that we received a {@link GASearchVariantSetsResponse} containing an
     * array of {@link GAVariantSet} objects.  For each of the GAVariantSet objects, grab the
     * <pre>id</pre> and pass it to....</li>
     * <li>Query 2: <pre>/callsets/search variantSetIds: id</pre></li>
     * <li>Test 2: assert that the returned object is a {@link GASearchCallSetsResponse}, and
     * that it contains &gt; 0 {@link GACallSet} objects. We can check that the call sets have
     * distinct ID values.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchCallSets() throws AvroRemoteException {
        final GASearchVariantSetsRequest vReq =
                GASearchVariantSetsRequest.newBuilder()
                                          .setDatasetIds(aSingle(DATASET_ID)).
                                                  build();
        final GASearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSets()).isNotEmpty();
        for (GAVariantSet set : vResp.getVariantSets()) {
            final String id = set.getId();

            final GASearchCallSetsRequest csReq =
                    GASearchCallSetsRequest.newBuilder()
                                           .setVariantSetIds(aSingle(id)).build();
            final GASearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);

            assertThat(csResp.getCallSets()).isNotEmpty();
        }
    }

    /**
     * Reference sets. Searches for reference set GRCh37 by accession (GCA_000001405.15) and then fetches that same
     * reference set by ID.
     * <ul>
     * <li>Query 1: <pre>/referencesets/search accessions: ["GCA_000001405.15"] pageSize: 1</pre></li>
     * <li>Test 1: assert that we received a {@link GASearchReferenceSetsResponse} object containing an array of
     * {@link GAReferenceSet} objects.  For each one, assert that <pre>ncbiTaxonId == 9606 AND
     * assemblyId == GRCh38</pre>.  And do this for each ID in referenceIds:</li>
     * <li>Query 2: <pre>/referencesets/(ref set ID)</pre></li>
     * <li>Test 2: assert that the ID of the returned object == ref set ID above.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void referenceSets() throws AvroRemoteException {
        final String accessionNumber = "GCA_000001405.15";
        final int ncbiTaxonId = 9606;
        final String assemblyId = "GRCh38";

        final GASearchReferenceSetsRequest req = GASearchReferenceSetsRequest.newBuilder().
                setAccessions(aSingle(accessionNumber)).setPageSize(PAGE_COUNT).build();
        final GASearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);
        final List<GAReferenceSet> refSets = resp.getReferenceSets();

        refSets.stream().forEach(rs -> assertThat(rs.getNcbiTaxonId()).isEqualTo(ncbiTaxonId));
        refSets.stream().forEach(rs -> assertThat(rs.getAssemblyId()).isEqualTo(assemblyId));

        // do query 2 and test 2
        for (GAReferenceSet refSet : refSets) {
            final String id = refSet.getId();
            final GAReferenceSet fetchedRefSet = client.references.getReferenceSet(id);
            assertThat(fetchedRefSet.getId()).isEqualTo(id);
        }
    }

    /**
     * References. Searches for chr1 of GRCh37 by MD5 checksum (<tt>1b22b98cdeb4a9304cb5d48026a85128</tt>)
     * and then fetches that same reference by ID.
     * <ul>
     *  <li>Query 1: <pre>/references/search md5checksums: [md5checksum] pageSize: 1</pre></li>
     *  <li>Test 1: assert that the result is a {@link GASearchReferencesResponse}
     *  containing an array of {@link GAReference} objects. Array must be of length (what??).
     * Assert that every GAReference has <pre>length == 249250621 AND md5checksum == (md5checksum) AND
     * ncbiTaxonId == 9606 (human)</pre></li>
     * <li>Query 2: <pre>/references/(ref ID)</pre></li>
     * <li>Test 2: assert that the returned {@link GAReference} has <pre>ID == ref ID</pre></li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void references() throws AvroRemoteException {

//        final String expectedMd5 = "1b22b98cdeb4a9304cb5d48026a85128";
        final String expectedMd5 = "TODO";
        final int expectedLength = 8050;
//        final int expectedLength = 249250621;
        final int expectedTaxonId = 9606;
        final int expectedRefs = 1;

        final GASearchReferencesRequest req =
                GASearchReferencesRequest.newBuilder().
                        setMd5checksums(aSingle(expectedMd5)).
                                                 setPageSize(PAGE_COUNT).build();
        final GASearchReferencesResponse resp = client.references.searchReferences(req);

        final List<GAReference> refs = resp.getReferences();
        assertThat(refs).hasSize(expectedRefs);

        refs.stream().forEach(ref -> assertThat(ref.getLength()).isEqualTo(expectedLength));
        refs.stream().forEach(ref -> assertThat(ref.getMd5checksum()).isEqualTo(expectedMd5));
        refs.stream().forEach(ref -> assertThat(ref.getNcbiTaxonId()).isEqualTo(expectedTaxonId));

        // do query 2 and test 2
        // open-coded loop because it's awkward to deal with possible exceptions using filter syntax
        for (GAReference ref : refs) {
            final String id = ref.getId();
            final GAReference fetchedRef = client.references.getReference(id);
            assertThat(fetchedRef.getId()).isEqualTo(id);
        }
    }

    /**
     * Reference bases.  Searches for chr1 of GRCh37 by MD5 checksum and then fetches 10 bases for
     * that reference at offset 15000.
     * <ul>
     *     <li>Query 1: <pre>/references/search md5checksums: [1b22b98cdeb4a9304cb5d48026a85128]
     *     pageSize: 1</pre></li>
     *     <li>Test 1: assert that we received a {@link GASearchReferencesResponse} object</li>
     *     <li>Query 2: <pre>/references/(ref id)/bases start: 15000 end: 15010</pre></li>
     *     <li>Test 2: assert that we received a {@link GAReference} object with fields
     *     <pre>offset == 15000 AND sequence == "ATCCGACATC"</pre></li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void referenceBases() throws AvroRemoteException {
//        final String expectedMd5 = "1b22b98cdeb4a9304cb5d48026a85128";
        final String expectedMd5 = "TODO";
        final long start = 15000;
        final long end = 15010;

        final long expectedOffset = 15000;
        final String expectedSequence = "ATCCGACATC";

        final GASearchReferencesRequest req =
                GASearchReferencesRequest.newBuilder().
                        setMd5checksums(aSingle(expectedMd5)).
                                                 setPageSize(PAGE_COUNT).build();
        final GASearchReferencesResponse resp = client.references.searchReferences(req);

        assertThat(resp).isNotNull();
        final List<GAReference> refs = resp.getReferences();
        assertThat(refs).isNotEmpty();

        for (GAReference ref : refs) {
            // query 2
            final GAListReferenceBasesRequest basesReq = GAListReferenceBasesRequest.newBuilder().
                    setStart(start).setEnd(end).build();
            GAListReferenceBasesResponse basesResp =
                    client.references.getReferenceBases(ref.getId(), basesReq);
            assertThat(basesResp.getOffset()).isEqualTo(expectedOffset);
            assertThat(basesResp.getSequence()).isEqualTo(expectedSequence);
        }
    }

    /**
     * Search reads. Looks up a read group set for NA12878 from the specified dataset, then fetches
     * reads.
     * <ul>
     *     <li>Query 1: <pre>/readgroupsets/search datasetIds: 1 </pre> (passed in) <pre> name:
     *     'NA12878', pageSize: 1</pre></li>
     *     <li>Test 1: assert that we received a {@link GASearchReadGroupSetsResponse} containing an
     *     array of {@link GAReadGroupSet}, length 1, with name 'NA12878'. Pull field 'id' from the
     *     first returned readGroups.</li>
     *     <li>Query 2: <pre>/reads/search readGroupIds: [id] referenceName: '22' start:
     *     51005353 end: 51005354</pre></li>
     *     <li>Test 2: assert that the result is a {@link GASearchReadsResponse} containing an
     *     array of &gt; 0 {@link GAReadAlignment} objects.</li>
     *     <li>Test 3: assert that each of the {@link GAReadAlignment} objects contains a
     *     nextMatePosition of type {@link GAPosition} with reference name == "22" AND
     *     alignment of type {@link GALinearAlignment} with field cigar holding a {@link GACigarUnit}.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchReads() throws AvroRemoteException {

        final String datasetName = "NA12878";
        final String expectedReadGroupSetName = "NA12878";
        final String referenceName = "22"; // the chromosome
        final long start = 51005353;
        final long end = 51005354;

        final GASearchReadGroupSetsRequest req = GASearchReadGroupSetsRequest.newBuilder().
                setDatasetIds(aSingle(DATASET_ID)).setName(datasetName).
                setPageSize(PAGE_COUNT).build();
        final GASearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);

        final List<GAReadGroupSet> readGroupSets = resp.getReadGroupSets();

        // test 1
        assertThat(readGroupSets).hasSize(1);
        final GAReadGroupSet readGroupSet = readGroupSets.get(0); // need this below
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getName())
                .isEqualTo(expectedReadGroupSetName));

        // query 2
        final String readGroupSetId = readGroupSet.getId();
        final GASearchReadsRequest srReq =
                GASearchReadsRequest.newBuilder().
                        setReadGroupIds(aSingle(readGroupSetId)).
                                            setReferenceName(referenceName).
                                            setStart(start).setEnd(end).build();
        final GASearchReadsResponse srResp = client.reads.searchReads(srReq);

        // test 2
        final List<GAReadAlignment> alignments = srResp.getAlignments();
        assertThat(alignments).isNotEmpty();

        // use a local var to avoid varargs confusion in the doesNotContain call:
        final GAReadAlignment nullRead = null;
        assertThat(alignments).doesNotContain(nullRead);

        // test 3
        alignments.stream().forEach(read -> assertThat(read.getNextMatePosition()).isNotNull());
        alignments.stream()
                  .forEach(read -> assertThat(read.getNextMatePosition()
                                                  .getReferenceName()).isEqualTo(referenceName));
        alignments.stream().forEach(read -> assertThat(read.getAlignment()).isNotNull());
        alignments.stream().forEach(read -> assertThat(read.getAlignment().getCigar()).isNotNull());
    }

}
