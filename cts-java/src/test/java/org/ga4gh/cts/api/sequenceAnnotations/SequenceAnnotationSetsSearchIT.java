package org.ga4gh.cts.api.sequenceAnnotations;

import org.apache.avro.AvroRemoteException;
import org.assertj.core.api.StrictAssertions;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.models.FeatureSet;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Maciek Smuga-Otto
 */
public class SequenceAnnotationSetsSearchIT {


    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check SequenceAnnotationSets contain some analysis data.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingSequenceAnnotationSets() throws AvroRemoteException {

        // Seek a list of SequenceAnnotationSets for the compliance dataset.
        final List<FeatureSet> sequenceAnnotationSets =  Utils.getAllFeatureSets(client);

        // check some are available
        assertThat(sequenceAnnotationSets).isNotEmpty();

        // Check the variantSetId is as expected.
        sequenceAnnotationSets.stream()
                .forEach(sas -> StrictAssertions.assertThat(sas.getName()).isNotNull());
    }


}


