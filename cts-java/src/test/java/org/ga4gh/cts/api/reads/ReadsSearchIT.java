package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * <p>Verify that data returned from reads/search queries meets expectations.</p>
 *
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */
@Category(ReadsTests.class)
public class ReadsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Search reads. Looks up a read group set for NA12878 from the specified dataset, then fetches
     * reads.
     * <ul>
     * <li>Query 1: <pre>/readgroupsets/search datasetId: "compliance-dataset1" name:
     *     '(name)'</pre></li>
     * <li>Test 1: assert that we received a {@link SearchReadGroupSetsResponse} containing an
     * array of {@link ReadGroupSet}, length 1, with name '(name)'. Pull field 'id' from the
     * first returned readGroups.</li>
     * <li>Query 2: <pre>/reads/search readGroupIds: [id] referenceName: '(name)' start:
     *     150 end: 160</pre></li>
     * <li>Test 2: assert that the result is a {@link SearchReadsResponse} containing an
     * array of &gt; 0 {@link ReadAlignment} objects.</li>
     * <li>Test 3: assert that each of the {@link ReadAlignment} objects contains a
     * nextMatePosition of type {@link Position} with reference name == "(name)" AND
     * alignment of type {@link LinearAlignment} with field cigar holding a {@link CigarUnit}.</li>
     * </ul>
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchReads() throws AvroRemoteException {

        final String expectedReadGroupSetName = TestData.EXPECTED_READGROUPSETS_NAMES[0];
        final String referenceName = TestData.EXPECTED_REFERENCE_NAMES[0];
        final long start = 150;
        final long end = 160;

        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .setName(expectedReadGroupSetName)
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);

        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

        // test 1
        assertThat(readGroupSets).hasSize(1);
        final ReadGroupSet readGroupSet = readGroupSets.get(0); // need this below
        readGroupSets.stream()
                     .forEach(rgs -> assertThat(rgs.getName())
                             .isEqualTo(expectedReadGroupSetName));

        // query 2
        final String readGroupSetId = readGroupSet.getId();
        final SearchReadsRequest srReq =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(readGroupSetId))
                                  .setStart(start)
                                  .setEnd(end)
                                  .build();
        final SearchReadsResponse srResp = client.reads.searchReads(srReq);

        // test 2
        final List<ReadAlignment> alignments = srResp.getAlignments();
        assertThat(alignments).isNotEmpty();

        assertThat(alignments).doesNotContain(Utils.nullReadAlignment);

        // test 3
        alignments.stream().forEach(read -> assertThat(read.getNextMatePosition()).isNotNull());
        alignments.stream()
                  .forEach(read -> assertThat(read.getNextMatePosition()
                                                  .getReferenceName()).isEqualTo(referenceName));
        alignments.stream().forEach(read -> assertThat(read.getAlignment()).isNotNull());
        alignments.stream().forEach(read -> assertThat(read.getAlignment().getCigar()).isNotNull());
    }

    /**
     * Verify that passing zero read group names in a {@link SearchReadsRequest}
     * returns all read groups.
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void searchReadsWithNoNamesReturnsAll() throws Exception {
        final SearchReadsRequest request =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(Collections.<String>emptyList())
                                  .build();
        final SearchReadsResponse response = client.reads.searchReads(request);

        assertThat(response.getAlignments()).isNotNull();

        for (ReadAlignment gar : response.getAlignments()) {
            assertThat(gar.getAlignedSequence()).isNotNull().matches("[ACTGN]+");
        }
    }

    /**
     * Verify that passing all known read group names in a {@link SearchReadsRequest}
     * returns all matching read groups.
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void searchReadsWithAllNamesReturnsAllMatching() throws Exception {
        final SearchReadsRequest request =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(TestData.EXPECTED_READGROUP_NAMES)
                                  .build();
        final SearchReadsResponse response = client.reads.searchReads(request);

        assertThat(response.getAlignments()).isNotNull();

        for (ReadAlignment gar : response.getAlignments()) {
            assertThat(gar.getAlignedSequence()).isNotNull().matches("[ACTGN]+");
        }
    }

    /**
     * <p>Verify alignedSequences match pattern.</p>
     * <p>In any {@link SearchReadsResponse}, the <tt>alignedSequence</tt> field can only contain
     * <tt>[ACTGN]+</tt>: No spaces, no other letters, no lowercase, no null.</p>
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void readsResponseMatchesACTGNPattern() throws Exception {
        for (String readGroupName : TestData.EXPECTED_READGROUP_NAMES) {
            SearchReadsRequest request = SearchReadsRequest.newBuilder()
                                                           .setReadGroupIds(aSingle(readGroupName))
                                                           .build();
            SearchReadsResponse response = client.reads.searchReads(request);

            assertThat(response.getAlignments()).isNotNull();

            for (ReadAlignment gar : response.getAlignments()) {
                assertThat(gar.getAlignedSequence()).isNotNull().matches("[ACTGN]+");
            }
        }
    }
}
