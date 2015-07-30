package org.ga4gh.cts.api.references;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.reads.ReadsTests;
import org.ga4gh.methods.SearchReferenceSetsRequest;
import org.ga4gh.methods.SearchReferenceSetsResponse;
import org.ga4gh.models.ReferenceSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.aSingle;

/**
 * Reference sets-related compliance tests.
 *
 * @author Herb Jellinek
 */
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReferenceSetsSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Reference sets. Searches for reference set GRCh37 by accession (GCA_000001405.15) and then fetches that same
     * reference set by ID.
     * <ul>
     * <li>Query 1: <pre>/referencesets/search accessions: ["GCA_000001405.15"] pageSize: 1</pre></li>
     * <li>Test 1: assert that we received a {@link SearchReferenceSetsResponse} object containing an array of
     * {@link ReferenceSet} objects.  For each one, assert that <pre>ncbiTaxonId == 9606 AND
     * assemblyId == GRCh38</pre>.  And do this for each ID in referenceIds:</li>
     * <li>Query 2: <pre>/referencesets/(ref set ID)</pre></li>
     * <li>Test 2: assert that the ID of the returned object == ref set ID above.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchForExpectedReferenceSets() throws AvroRemoteException {
        final String accessionNumber = "GCA_000001405.15";
        final int ncbiTaxonId = 9606;
        final String assemblyId = "GRCh38";

        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                                            .setAccessions(aSingle(accessionNumber))
                                            .build();
        final SearchReferenceSetsResponse resp = client.references.searchReferenceSets(req);
        final List<ReferenceSet> refSets = resp.getReferenceSets();

        refSets.stream().forEach(rs -> assertThat(rs.getNcbiTaxonId()).isEqualTo(ncbiTaxonId));
        refSets.stream().forEach(rs -> assertThat(rs.getAssemblyId()).isEqualTo(assemblyId));

        // do query 2 and test 2
        for (ReferenceSet refSet : refSets) {
            final String id = refSet.getId();
            final ReferenceSet fetchedRefSet = client.references.getReferenceSet(id);
            assertThat(fetchedRefSet.getId()).isEqualTo(id);
        }
    }

}
