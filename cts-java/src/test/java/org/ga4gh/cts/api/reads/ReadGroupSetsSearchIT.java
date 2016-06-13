package org.ga4gh.cts.api.reads;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import ga4gh.Common.GAException;
import ga4gh.ReadServiceOuterClass.*;
import ga4gh.Reads.*;
import ga4gh.BioMetadata.*;
import ga4gh.BioMetadataServiceOuterClass.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
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
     * <p>When we supply a name to {@link SearchReadGroupSetsRequest}, the
     * returned objects must all have names that match.</p>
     * <p>The Schemas documentation says
     * that a {@link SearchReadGroupSetsRequest} always matches names exactly.</p>
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void readGroupSetsNameShouldRetrieveOnlyMatchingReadGroupSets() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        for (String readGroupSetName : TestData.EXPECTED_READGROUPSETS_NAMES) {
            final SearchReadGroupSetsRequest req =
                    SearchReadGroupSetsRequest.newBuilder().
                            setDatasetId(TestData.getDatasetId()).setName(readGroupSetName).build();
            SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
            final List<ReadGroupSet> rgSets = resp.getReadGroupSetsList();
            assertThat(rgSets).hasSize(1);
            assertThat(rgSets.get(0).getName()).isEqualTo(readGroupSetName);
        }
    }

    /**
     * <p>Calling {@link SearchReadGroupSetsRequest} with a nonexistent dataset ID should throw an
     * exception with status code <tt>NOT_FOUND</tt>.</p>
     *
     * <p>Pass in a well-formed but non-matching dataset ID to a SearchReadGroupSetsRequest
     * expect a valid SearchReadGroupSetsResponse with no ReadGroupSets in it.</p>
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void readgroupSetResponseForNonexistentDatasetIdShouldThrowException() {
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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupSetsShouldHaveCorrectDatasetId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getDatasetId()).isEqualTo(TestData.getDatasetId()));
    }

    /**
     * Retrieve all {@link ReadGroupSet}s and make sure they all contain an ID.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupSetsShouldContainId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();
        readGroupSets.stream().forEach(rgs -> assertThat(rgs.getId()).isNotNull());
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all have the right dataset ID.
     * (Adapted from one of the JavaScript compliance tests.)
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupsShouldHaveCorrectDatasetId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroupsList()) {
                assertThat(readGroup).isNotNull();
                assertThat(readGroup.getDatasetId()).isEqualTo(TestData.getDatasetId());
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain <tt>Program</tt> information.
     * (Adapted from one of the JavaScript compliance tests.)
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupsShouldContainPrograms() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroupsList()) {
                assertThat(readGroup.getProgramsList()).isNotEmpty();
                assertThat(readGroup.getProgramsList()).doesNotContain(Utils.nullProgram);
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain a non-null <tt>{@link ReadGroup#getInfo()}</tt>
     * field.  If it's non-null, it must perforce be a {@link Map}.  The contents of the {@link Map} don't concern
     * us.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupsShouldContainInfoMap() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroupsList()) {
                assertThat(readGroup.getInfo()).isNotNull();
            }
        }
    }

    /**
     * Retrieve all {@link ReadGroup}s and make sure they all contain a non-null <tt>{@link ReadGroup#getId()}</tt>
     * field.  If it's non-null, it must perforce be a {@link String}.  The contents of the {@link String} don't concern
     * us.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void allReadGroupsShouldContainId() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();

        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroupsList()) {
                assertThat(readGroup.getId()).isNotNull();
            }
        }
    }

    /**
     * Read group sets return a list of read groups that should all match the requested
     * biosample ID.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void checkBioSampleFilter() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final BioSample b = Utils.getBioSampleByName(client, TestData.BIOSAMPLE_NAME);
        final SearchReadGroupSetsRequest req =
                SearchReadGroupSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setBioSampleId(b.getId())
                        .build();
        final SearchReadGroupSetsResponse resp = client.reads.searchReadGroupSets(req);
        final List<ReadGroupSet> readGroupSets = resp.getReadGroupSetsList();
        assertThat(readGroupSets).isNotEmpty();
        for (ReadGroupSet readGroupSet : readGroupSets) {
            for (ReadGroup readGroup : readGroupSet.getReadGroupsList()) {
                assertThat(readGroup.getBioSampleId()).isEqualTo(b.getId());
            }
        }
    }
}
