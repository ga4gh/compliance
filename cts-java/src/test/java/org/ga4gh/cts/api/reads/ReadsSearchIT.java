package org.ga4gh.cts.api.reads;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.*;
import org.ga4gh.models.ReadAlignment;
import org.ga4gh.models.ReadGroup;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

/**
 * Verify that data returned from <tt>/reads/search</tt> queries meets expectations.
 */
@Category(ReadsTests.class)
public class ReadsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Call <tt>/reads/search</tt> with a range that contains zero reads, and verify that it returns none.
     * (Adapted from an old JavaScript test.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchRangeWithNoReadsReturnsZeroResults() throws AvroRemoteException {
        final String refId = Utils.getValidReferenceId(client);

        final long emptyRangeStart = 0; // is this range actually empty?
        final long emptyRangeEnd = 100;

        final SearchReadsRequest srReq =
                SearchReadsRequest.newBuilder()
                        .setReferenceId(refId)
                        .setReadGroupIds(aSingle(Utils.getReadGroupId(client)))
                        .setStart(emptyRangeStart)
                        .setEnd(emptyRangeEnd)
                        .build();
        final SearchReadsResponse srResp = client.reads.searchReads(srReq);

        final List<ReadAlignment> alignments = srResp.getAlignments();
        assertThat(alignments).isEmpty();
    }

    /**
     * Call <tt>/reads/search</tt> with a range that contains multiple reads, and verify
     * they are well-formed.
     * (Adapted from an old JavaScript test.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchReadsProducesWellFormedReads() throws AvroRemoteException {

        // first get a valid reference
        final String refId = Utils.getValidReferenceId(client);

        final long start = 150;
        final long end = 160;

        final SearchReadsRequest srReq =
                SearchReadsRequest.newBuilder()
                                  .setReferenceId(refId)
                                  .setReadGroupIds(aSingle(Utils.getReadGroupId(client)))
                                  .setStart(start)
                                  .setEnd(end)
                                  .build();
        final SearchReadsResponse srResp = client.reads.searchReads(srReq);

        final List<ReadAlignment> alignments = srResp.getAlignments();
        alignments.stream().forEach(read -> assertThat(read.getNextMatePosition()).isNotNull());
        alignments.stream()
                  .forEach(read -> assertThat(read.getNextMatePosition()
                                                  .getReferenceName()).isEqualTo(TestData.REFERENCE_NAME));
        alignments.stream().forEach(read -> assertThat(read.getAlignment()).isNotNull());
        alignments.stream().forEach(read -> assertThat(read.getAlignment().getCigar()).isNotNull());
    }

    /**
     * Fetch every {@link ReadGroupSet} named in {@link TestData#EXPECTED_READGROUP_NAMES} using
     * <tt>searchReadGroupSets</tt> and verify that it returns the expected {@link ReadGroupSet}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void searchReadGroupSetsMustReturnReadGroupSetsWithExpectedNames() throws
            AvroRemoteException {

        for (String expectedReadGroupSetName : TestData.EXPECTED_READGROUPSETS_NAMES) {

            final SearchReadGroupSetsRequest req =
                    SearchReadGroupSetsRequest.newBuilder()
                                              .setDatasetId(TestData.getDatasetId())
                                              .setName(expectedReadGroupSetName)
                                              .build();
            final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);

            final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

            assertThat(readGroupSets).hasSize(1);
            final ReadGroupSet readGroupSet = readGroupSets.get(0);
            assertThat(readGroupSet.getName()).isEqualTo(expectedReadGroupSetName);
            assertThat(readGroupSet.getDatasetId()).isEqualTo(TestData.getDatasetId());
        }
    }

    /**
     * Verify that passing zero read group names in a {@link SearchReadsRequest} fails.
     *
     * @throws Exception if there's a problem we didn't catch
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void searchReadsWithNoIdsFails() throws Exception {

        // first get a valid reference
        final String refId = Utils.getValidReferenceId(client);

        final SearchReadsRequest request =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(Collections.emptyList())
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();

        final GAWrapperException t = catchGAWrapperException(() -> client.reads.searchReads(request));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
    }

    /**
     * Verify that passing one read group name in a {@link SearchReadsRequest}
     * returns valid reads.
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void searchReadsWithOneReadGroupIdSucceeds() throws Exception {

        // first get a valid reference
        final String refId = Utils.getValidReferenceId(client);

        // get a ReadGroup id
        final String readGroupId = Utils.getReadGroupId(client);

        final SearchReadsRequest request =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(aSingle(readGroupId))
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();
        final SearchReadsResponse response = client.reads.searchReads(request);

        assertThat(response.getAlignments()).isNotNull();

        response.getAlignments().stream()
                .forEach(readAlignment ->
                                 assertThat(readAlignment.getAlignedSequence()).isNotNull()
                                                                               .matches(TestData.ALIGNED_SEQUENCE_CONTENTS_PATTERN));
    }

    /**
     * Verify that passing all known read group names in a {@link SearchReadsRequest}
     * returns all matching read groups.
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void searchReadsWithAllIdsReturnsReadsForEach() throws Exception {
        // first get a valid reference
        final String refId = Utils.getValidReferenceId(client);

        // get all ReadGroupSets
        final List<ReadGroupSet> allReadGroupSets = Utils.getAllReadGroupSets(client);

        // collect all IDs from the ReadGroups
        final List<String> allReadGroupIds =
                allReadGroupSets.stream()
                                .flatMap(readGroupSet ->
                                                 readGroupSet.getReadGroups()
                                                             .stream())
                                .map(ReadGroup::getId)
                                .collect(Collectors.toList());

        final SearchReadsRequest request =
                SearchReadsRequest.newBuilder()
                                  .setReadGroupIds(allReadGroupIds)
                                  .setStart(0L)
                                  .setEnd(150L)
                                  .setReferenceId(refId)
                                  .build();
        final SearchReadsResponse response = client.reads.searchReads(request);

        assertThat(response.getAlignments()).isNotNull();

        response.getAlignments().stream()
                .forEach(readAlignment ->
                                 assertThat(readAlignment.getAlignedSequence()).isNotNull()
                                                                               .matches(TestData.ALIGNED_SEQUENCE_CONTENTS_PATTERN));
    }

    /**
     * <p>Verify aligned sequences contain only the permitted symbols.</p>
     * <p>In any {@link ReadAlignment} in our compliance test data,
     * the <tt>alignedSequence</tt> field can only contain
     * {@link TestData#ALIGNED_SEQUENCE_CONTENTS_PATTERN}.
     *
     * @throws Exception if there's a problem
     */
    @Test
    public void readsResponseMatchesACTGNPattern() throws Exception {

        final String refId = Utils.getValidReferenceId(client);

        for (String readGroupSetName : TestData.EXPECTED_READGROUPSETS_NAMES) {
            for (String readGroupName : TestData.EXPECTED_READGROUPSET_READGROUP_NAMES.get(readGroupSetName)) {
                final String readGroupId = Utils.getReadGroupIdForName(client, readGroupSetName, readGroupName);
                final SearchReadsRequest request =
                        SearchReadsRequest.newBuilder()
                                          .setReadGroupIds(aSingle(readGroupId))
                                          .setReferenceId(refId)
                                          .setStart(0L)
                                          .setEnd(150L)
                                          .build();
                final SearchReadsResponse response = client.reads.searchReads(request);

                assertThat(response.getAlignments()).isNotNull();

                response.getAlignments().stream()
                        .forEach(readAlignment ->
                                         assertThat(readAlignment.getAlignedSequence()).isNotNull()
                                                                                       .matches(TestData.ALIGNED_SEQUENCE_CONTENTS_PATTERN));
            }
        }
    }

}
