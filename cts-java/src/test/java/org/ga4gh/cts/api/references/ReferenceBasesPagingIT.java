package org.ga4gh.cts.api.references;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import ga4gh.ReferenceServiceOuterClass.ListReferenceBasesRequest;
import ga4gh.ReferenceServiceOuterClass.ListReferenceBasesResponse;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkPagingOneByOneThroughReferenceBases() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final String refId = Utils.getValidReferenceId(client);

        final long sequenceStart = 2L;
        final long sequenceEnd = 15L;

        final ListReferenceBasesRequest req = ListReferenceBasesRequest.newBuilder()
                .setStart(sequenceStart).setEnd(sequenceEnd)
                .build();
        final ListReferenceBasesResponse resp = client.references.getReferenceBases(refId, req);
        final String expectedSequence = resp.getSequence();
        assertThat(expectedSequence).hasSize((int)(sequenceEnd - sequenceStart));

        // walk through the bases by incrementing start and end
        long offset = sequenceStart;
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private String getFullSequence() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
     * using a size equal to the size of the full sequence, and receive the full sequence.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkRequestingRightSizedBaseSequence() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void checkRequestingTooLargeBaseSequenceFails() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    private void checkChunkOfBases(long chunkSize, String expectedSequence) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
