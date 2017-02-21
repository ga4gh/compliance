package org.ga4gh.cts.api.sequenceAnnotations;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.function.Consumer;

import ga4gh.SequenceAnnotationServiceOuterClass.SearchContinuousRequest;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchContinuousResponse;
import ga4gh.SequenceAnnotations.Continuous;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test searching continuous data.
 */
@Category(SequenceAnnotationTests.class)
public class ContinuousSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());


    /**
     * Fetch continuous data between two positions in the reference.  The number of values  must
     * equal what we're expecting by examination of the continuous data.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */

    @Test
    public void checkExpectedNumberOfValues() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final long start = 50083750;
        final long end = 50083800;
        final String referenceName = "chr19";
        final int expectedNumberOfValues = 25;

        final String id = Utils.getContinuousSetId(client);

        final SearchContinuousRequest fReq =
                SearchContinuousRequest.newBuilder()
                                     .setContinuousSetId(id)
                                     .setReferenceName(referenceName)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchContinuousResponse fResp = client.sequenceAnnotations.searchContinuous(fReq);
        final List<Continuous> searchContinuous = fResp.getContinuousList();

        assertThat(searchContinuous).isNotEmpty();
        Continuous msg = searchContinuous.get(0);
        assertThat(msg.getValuesList()).hasSize(expectedNumberOfValues);
    }
}
