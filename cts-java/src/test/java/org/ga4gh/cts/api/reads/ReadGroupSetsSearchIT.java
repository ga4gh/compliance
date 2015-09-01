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
import org.ga4gh.models.ReadGroup;
import org.ga4gh.models.ReadGroupSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
     * Return the number of Strings that match the possible substring.
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
    public void readGroupSetsNameShouldRetrieveOnlyMatchingReadGroupSets() throws
            AvroRemoteException {
        for (String readGroupSetName : TestData.EXPECTED_READGROUPSETS_NAMES) {
            final SearchReadGroupSetsRequest req =
                    SearchReadGroupSetsRequest.newBuilder().
                            setDatasetId(TestData.getDatasetId()).setName(readGroupSetName).build();
            SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
            final List<ReadGroupSet> rgSets = resp.getReadGroupSets();
            assertThat(rgSets).hasSize(1);
            assertThat(rgSets.get(0).getName()).isEqualTo(readGroupSetName);
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
        final GAWrapperException t = catchGAWrapperException(() -> client.reads
                .searchReadGroupSets(reqb));
        assertThat(t.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Retrieve all {@link ReadGroupSet}s and make sure they all have the right dataset ID.
     * (Adapted from one of the JavaScript compliance tests.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupSetsShouldHaveCorrectDatasetId() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getDatasetId()).isEqualTo(TestData.getDatasetId()));
    }

    /**
     * Retrieve all {@link ReadGroupSet}s and make sure they all contain an ID.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupSetsShouldContainId() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getId()).isNotNull());
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all have the right dataset ID.
     * (Adapted from one of the JavaScript compliance tests.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupsShouldHaveCorrectDatasetId() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup).isNotNull();
                assertThat(readGroup.getDatasetId()).isEqualTo(TestData.getDatasetId());
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain <tt>Program</tt> information.
     * (Adapted from one of the JavaScript compliance tests.)
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupsShouldContainPrograms() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup.getPrograms()).isNotEmpty();
                assertThat(readGroup.getPrograms()).doesNotContain(Utils.nullProgram);
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain a non-null <tt>{@link ReadGroup#info}</tt>
     * field.  If it's non-null, it must perforce be a {@link Map}.  The contents of the {@link Map} don't concern
     * us.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupsShouldContainInfoMap() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup.getInfo()).isNotNull();
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain a non-null <tt>{@link ReadGroup#id}</tt>
     * field.  If it's non-null, it must perforce be a {@link String}.  The contents of the {@link String} don't concern
     * us.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void allReadGroupsShouldContainId() throws AvroRemoteException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSets();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroups()) {
                assertThat(readGroup.getId()).isNotNull();
            }
        }
    }
}
