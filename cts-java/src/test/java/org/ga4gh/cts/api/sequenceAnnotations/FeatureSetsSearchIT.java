package org.ga4gh.cts.api.sequenceAnnotations;

import org.apache.avro.AvroRemoteException;
import org.assertj.core.api.StrictAssertions;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.models.FeatureSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  Tests for <tt>POST /featureSets/search</tt>.
 *
 * @author Maciek Smuga-Otto
 */
@Category(SequenceAnnotationTests.class)
public class FeatureSetsSearchIT {


    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check SequenceAnnotationSets contain some analysis data.
     *
     *@throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkSearchingFeatureSets() throws AvroRemoteException {

        // Seek a list of SequenceAnnotationSets for the compliance dataset.
        final List<FeatureSet> featureSets =  Utils.getAllFeatureSets(client);

        // check some are available
        assertThat(featureSets).isNotEmpty();

        // Check the featureSetId is as expected.
        featureSets.stream()
                .forEach(sas -> StrictAssertions.assertThat(sas.getName()).isNotNull());
    }


}


