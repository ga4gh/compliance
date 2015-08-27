package org.ga4gh.cts.api.reads;

import junitparams.JUnitParamsRunner;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.models.Program;
import org.ga4gh.models.ReadGroup;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

/**
 * <p>Validates the data returned by /readgroupsets/search.</p>
 */
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReadGroupSetsSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Returrn the number of Strings that match the possible substring.
     * @param possibleSubstring the substring we're searching for
     * @param matchWithin the {@link String[]} we're searching within
     * @return the number of elements of <tt>matchWithin</tt> that contain <tt>possibleSubstring</tt>
     */
    private static int countMatches(String possibleSubstring, String[] matchWithin) {
         return (int)Arrays.stream(matchWithin).filter(name -> name.contains(possibleSubstring)).count();
    }

    /**
     * <p>When we supply a name to {@link SearchReadGroupSetsRequest}, the
     * returned objects must all have names that match.</p>
     * <p>The Schemas documentation says
     * that a {@link SearchReadGroupSetsRequest} always matches names exactly.</p>
     *
     * @throws AvroRemoteException the exception thrown
     */
    @Test
    public void readgroupSetsNameShouldRetrieveOnlyMatchingReadGroupSets() throws
            AvroRemoteException {
        for (String rgSetName : TestData.EXPECTED_READGROUPSETS_NAMES) {
            final SearchReadGroupSetsRequest goodReq =
                    SearchReadGroupSetsRequest.newBuilder().
                            setDatasetId(TestData.getDatasetId()).setName(rgSetName).build();
            SearchReadGroupSetsResponse goodResp = client.reads.searchReadGroupSets(goodReq);
            final List<ReadGroupSet> rgSets = goodResp.getReadGroupSets();
            assertThat(rgSets).hasSize(1);
            assertThat(rgSets.get(0).getName()).isEqualTo(rgSetName);
        }
    }

    /**
     * <p>When we supply a bogus name to {@link SearchReadGroupSetsRequest}, because the
     * returned objects must all have names that match, we expect NOT_FOUND.</p>
     * <p>The Schemas documentation says
     * that a {@link SearchReadGroupSetsRequest} always matches names exactly.</p>
     *
     * @throws AvroRemoteException the exception thrown
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void readGroupSetsNonexistentNameShouldSayNotFound() throws AvroRemoteException {
        // try to fetch a read group set with a name the server can't match
        final SearchReadGroupSetsRequest badNameReq =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .setName(Utils.randomName())
                                          .build();
        final GAWrapperException t = catchGAWrapperException(() -> client.reads.searchReadGroupSets(badNameReq));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * <p>Readgroup set response for a nonexistent dataset ID should be NOT_FOUND.</p>
     *
     * <p>Pass in a well-formed but non-matching dataset ID to a SearchReadGroupSetsRequest
     * expect a valid SearchReadGroupSetsResponse with no ReadGroupSets in it.</p>
     *
     * @throws AvroRemoteException the exception thrown
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void readgroupSetResponseForNonexistentDatasetIdShouldReturnEmptyList() throws AvroRemoteException {
        SearchReadGroupSetsRequest reqb =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(Utils.randomId())
                                          .build();
        final GAWrapperException t = catchGAWrapperException(() -> client.reads.searchReadGroupSets(reqb));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Search read group sets.  Fetches read group sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/readgroupsets/search &lt;dataset ID&gt;</pre>
     * <li>Test 1: assert that we received a result of type {@link SearchReadGroupSetsResponse},
     * and that every {@link ReadGroupSet} it contains has field datasetId == &lt;dataset ID&gt;</li>
     * <li>Test 2: every {@link ReadGroup} in that {@link ReadGroupSet} has: an 'experiment'
     * of type Experiment; datasetId == &lt;dataset ID&gt;; a program of type {@link Program}
     * which is not empty.
     * </ul>
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void requestForAllReadGroupSetsShouldReturnAllWellFormed() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getDatasetId()).isEqualTo(TestData.getDatasetId()));

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup).isNotNull();
                assertThat(readGroup.getDatasetId()).isEqualTo(TestData.getDatasetId());
                assertThat(readGroup.getPrograms()).isNotEmpty();
                assertThat(readGroup.getPrograms()).doesNotContain(Utils.nullProgram);
            }
        }
    }
}
