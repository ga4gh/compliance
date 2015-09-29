package org.ga4gh.cts.api.references;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.ListReferenceBasesRequest;
import org.ga4gh.methods.ListReferenceBasesResponse;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/references/{id}/bases</tt> paging behavior.
 *
 * @author Herb Jellinek
 */
@Category(ReferencesTests.class)
public class ReferenceBasesPagingIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can walk by single characters through the bases
     * we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#getReferenceBases(String, ListReferenceBasesRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughReferenceBases() throws AvroRemoteException {

        final String refId = Utils.getValidReferenceId(client);

        final long sequenceStart = 0L;
        final long sequenceEnd = 150L;

        final ListReferenceBasesRequest req = ListReferenceBasesRequest.newBuilder()
                .setStart(sequenceStart).setEnd(sequenceEnd)
                .build();
        final ListReferenceBasesResponse resp = client.references.getReferenceBases(refId, req);
        final String expectedSequence = resp.getSequence();
        assertThat(expectedSequence).hasSize((int)sequenceEnd);

        // walk through the bases by incrementing start and end
        long offset = 0L;
        for (char expectedChar : expectedSequence.toCharArray()) {
            final ListReferenceBasesRequest pageReq =
                    ListReferenceBasesRequest.newBuilder()
                                             .setStart(offset).setEnd(offset + 1)
                                             .build();
            final ListReferenceBasesResponse pageResp =
                    client.references.getReferenceBases(refId, pageReq);
            final String sequence = pageResp.getSequence();
            assertThat(sequence).hasSize(1);
            assertThat(sequence.charAt(0)).isEqualTo(expectedChar);
            offset = pageResp.getOffset() + 1;
        }

        assertThat(offset).isEqualTo(sequenceEnd);
    }

    /**
     * Request and return the full base sequence.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    private String getFullSequence() throws AvroRemoteException {
        final String refId = Utils.getValidReferenceId(client);

        final ListReferenceBasesRequest req = ListReferenceBasesRequest.newBuilder()
                                                                       .setStart(TestData.REFERENCE_START)
                                                                       .build();
        final ListReferenceBasesResponse resp = client.references.getReferenceBases(refId, req);
        return resp.getSequence();
    }

    /**
     * Check that we can request a sequence of bases from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#getReferenceBases(String,
     * ListReferenceBasesRequest)}
     * using a size twice as large as the full sequence, and receive the full sequence.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkRequestingRightSizedBaseSequence() throws AvroRemoteException {

        final String fullSequence = getFullSequence();
        checkChunkOfBases(TestData.REFERENCE_BRCA1_LENGTH, fullSequence);
    }

    /**
     * Check that if we request a sequence of bases from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#getReferenceBases(String,
     * ListReferenceBasesRequest)}
     * using a size larger than the full sequence, it fails with an exception with HTTP status
     * "Requested Range Not Satisfiable."
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void checkRequestingTooLargeBaseSequenceFails() throws AvroRemoteException {

        final String fullSequence = getFullSequence();

        // try it with a range that's too large by 100%
        GAWrapperException g =
                Utils.catchGAWrapperException(() -> checkChunkOfBases(TestData.REFERENCE_BRCA1_LENGTH * 2,
                                                                      fullSequence));
        assertThat(g.getHttpStatusCode()).isEqualTo(Utils.HTTP_REQUESTED_RANGE_NOT_SATISFIABLE);

        // try it with a range that's too large by one
        g = Utils.catchGAWrapperException(() -> checkChunkOfBases(TestData.REFERENCE_BRCA1_LENGTH + 1,
                                                                  fullSequence));
        assertThat(g.getHttpStatusCode()).isEqualTo(Utils.HTTP_REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * Check that we receive expected results when we request a single
     * chunk of bases from zero to <tt>chunkSize</tt> from
     * {@link org.ga4gh.ctk.transport.protocols.Client.References#getReferenceBases(String,
     * ListReferenceBasesRequest)}
     * using <tt>chunkSize</tt> as the sequence length.
     *
     * @param chunkSize        the sequence length we'll request
     * @param expectedSequence the full base sequence we expect
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkChunkOfBases(long chunkSize,
                                   String expectedSequence)
            throws AvroRemoteException {

        final String refId = Utils.getValidReferenceId(client);

        final long sequenceStart = 0L;

        final ListReferenceBasesRequest req = ListReferenceBasesRequest.newBuilder()
                                                                       .setStart(sequenceStart)
                                                                       .setEnd(chunkSize)
                                                                       .build();
        final ListReferenceBasesResponse resp = client.references.getReferenceBases(refId, req);
        final String sequence = resp.getSequence();
        assertThat(sequence).isEqualTo(expectedSequence);
    }

}
