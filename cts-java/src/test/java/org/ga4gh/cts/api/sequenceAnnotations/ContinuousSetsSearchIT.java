package org.ga4gh.cts.api.sequenceAnnotations;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.assertj.core.api.StrictAssertions;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import ga4gh.SequenceAnnotations.ContinuousSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  Tests for <tt>POST /continuousSets/search</tt>.
 *
 * @author Maciek Smuga-Otto
 */
@Category(SequenceAnnotationTests.class)
public class ContinuousSetsSearchIT {


    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check SequenceAnnotationSets contain some analysis data.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkSearchingContinuousSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // Seek a list of SequenceAnnotationSets for the compliance dataset.
        final List<ContinuousSet> continuousSets = Utils.getAllContinuousSets(client);

        // check some are available
        assertThat(continuousSets).isNotEmpty();

        // Check the continuousSetId is as expected.
        continuousSets.stream()
                .forEach(sas -> StrictAssertions.assertThat(sas.getName()).isNotNull());
    }


}


