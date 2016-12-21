package org.ga4gh.cts.api;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;

import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationSetsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationSetsResponse;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsResponse;
import ga4gh.AlleleAnnotations.VariantAnnotation;
import ga4gh.AlleleAnnotations.VariantAnnotationSet;
import ga4gh.GenotypePhenotype.PhenotypeAssociationSet;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypeAssociationSetsRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypeAssociationSetsResponse;
import ga4gh.RnaQuantificationOuterClass.*;
import ga4gh.RnaQuantificationServiceOuterClass.*;
import ga4gh.Reads.*;
import ga4gh.ReadServiceOuterClass.*;
import ga4gh.References.*;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeatureSetsRequest;
import ga4gh.SequenceAnnotations.*;
import ga4gh.Variants.*;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Metadata.*;
import ga4gh.MetadataServiceOuterClass.*;
import ga4gh.BioMetadata.*;
import ga4gh.BioMetadataServiceOuterClass.*;
import org.assertj.core.api.ThrowableAssert;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.protocols.Client;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static ga4gh.SequenceAnnotationServiceOuterClass.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.catchThrowable;
import static org.assertj.core.api.StrictAssertions.fail;

/**
 * Handy test-related static methods and data.
 *
 * @author Herb Jellinek
 */
public class Utils {

    /**
     * You can't instantiate one of these.
     */
    private Utils() {
    }

    /**
     * For some reason, the class {@link java.net.HttpURLConnection} doesn't define a constant
     * for HTTP status 416, "Requested Range Not Satisfiable."  (See
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17">HTTP Status Code Definitions</a>.)
     */
    public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    /**
     * Certain AssertJ methods accept a variable number of args: <tt>assertThat(Collection).doesNotContain(...)</tt>,
     * for instance.  Sometimes we want to pass null to such a method, but the IDE complains that this is "confusing."
     * If we supply a typed value, the complaint goes away.
     * This is a null suitable for use where we might want to pass a {@link Program} to a varargs method.
     */
    public static final Program nullProgram = null;

    /**
     * Certain AssertJ methods accept a variable number of args: <tt>assertThat(Collection).doesNotContain(...)</tt>,
     * for instance.  Sometimes we want to pass null to such a method, but the IDE complains that this is "confusing."
     * If we supply a typed value, the complaint goes away.
     * This is a null suitable for use where we might want to pass a {@link ReadAlignment} to a varargs method.
     */
    public static final ReadAlignment nullReadAlignment = null;

    /**
     * Make it easy to create lists of a single element, which we do a lot.
     * @param s the single item (typically a {@link String})
     * @param <T> the class of the parameter
     * @return the resulting {@link List} containing the single element
     */
    public static <T> List<T> aSingle(T s) {
        return Collections.singletonList(s);
    }

    /**
     * Is the argument character a valid hexadecimal digit?
     * @param c the candidate character
     * @return true if it's hex, false otherwise
     */
    public static boolean isHex(char c) {
        return Character.isDigit(c) ||
                (('a' <= c) && (c <= 'f')) ||
                (('A' <= c) && (c <= 'F'));
    }

    /**
     * MD5 hash values are 32 characters long.
     */
    private static final int MD5_LENGTH = 32;

    /**
     * Is the argument a legitimate-seeming MD5 value?
     * That is, is it the right length (32 characters), and does it consist only of hex digits?
     *
     * @param possibleMd5 the supposed MD5 hash to check
     * @return true if the parameter is a plausible MD5 value
     */
    public static boolean looksLikeValidMd5(String possibleMd5) {
        return (possibleMd5 != null) && possibleMd5.length() == MD5_LENGTH &&
                possibleMd5.chars().allMatch(c -> isHex((char)c));
    }

    /**
     * Create and return an ID that's (virtually) guaranteed not to name a real object on a
     * GA4GH server.  It uses {@link UUID#randomUUID()} to do it.
     * This is identical to {@link #randomName()} but for the name.
     * @return an ID that's (virtually) guaranteed not to name a real object
     */
    public static String randomId() {
        return UUID.randomUUID().toString();
    }


    /**
     * Create and return a name that's (virtually) guaranteed not to name a real object on a
     * GA4GH server.  It uses {@link UUID#randomUUID()} to do it.
     * This is identical to {@link #randomId()} but for the name.
     * @return a name that's (virtually) guaranteed not to name a real object
     */
    public static String randomName() {
        return UUID.randomUUID().toString();
    }

    /**
     * Utility method to fetch the ID of a reference to which we can map the reads we're testing.
     * @param client the {@link Client} connection to the server
     * @return the ID of a reference
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getValidReferenceId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferencesRequest refsReq = SearchReferencesRequest
                .newBuilder()
                .setReferenceSetId(Utils.getReferenceSetIdByAssemblyId(client, TestData.REFERENCESET_ASSEMBLY_ID))
                .setMd5Checksum(TestData.REFERENCE_BRCA1_MD5_CHECKSUM)
                .build();
        final SearchReferencesResponse refsResp = client.references.searchReferences(refsReq);
        assertThat(refsResp).isNotNull();
        final List<Reference> references = refsResp.getReferencesList();
        assertThat(references).isNotNull().isNotEmpty();
        assertThat(references).hasSize(1);
        return references.get(0).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link ReadGroup}.
     * @param client the {@link Client} connection to the server
     * @return the ID of an arbitrary {@link ReadGroup}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getReadGroupId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest
                        .newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSetsList();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        List<ReadGroup> readGroups = readGroupSet.getReadGroupsList();
        assertThat(readGroups).isNotEmpty().isNotNull();
        final ReadGroup readGroup = readGroups.get(0);
        assertThat(readGroup).isNotNull();
        return readGroup.getId();
    }

    /**
     * Utility method to fetch the ID of a named {@link ReadGroup} in a named {@link ReadGroupSet}.
     * @param client the {@link Client} connection to the server
     * @param readGroupSetName the name of a {@link ReadGroupSet}
     * @param readGroupName the name of a {@link ReadGroup} in the given {@link ReadGroupSet}
     * @return the ID of the {@link ReadGroup}, or null if not found
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getReadGroupIdForName(Client client, String readGroupSetName, String readGroupName) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(readGroupSetName)
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSetsList();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        final List<ReadGroup> readGroups = readGroupSet.getReadGroupsList();
        final Optional<ReadGroup> result =
                readGroups.stream().filter(readGroup -> readGroupName.equals(readGroup.getName())).findFirst();
        return result.isPresent() ? result.get().getId() : null;
    }

    /**
     * Utility method to fetch all {@link ReadGroupSet}s.
     * @param client the {@link Client} connection to the server
     * @return all {@link ReadGroupSet}s
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<ReadGroupSet> getAllReadGroupSets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<ReadGroupSet> result = new LinkedList<>();

        String pageToken = "";
        do {
            final SearchReadGroupSetsRequest readGroupSetsReq =
                    SearchReadGroupSetsRequest
                            .newBuilder()
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .setDatasetId(TestData.getDatasetId())
                            .build();
            final SearchReadGroupSetsResponse readGroupSetsResp =
                    client.reads.searchReadGroupSets(readGroupSetsReq);
            pageToken = readGroupSetsResp.getNextPageToken();
            assertThat(readGroupSetsResp).isNotNull();
            final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSetsList();
            assertThat(readGroupSets).isNotEmpty().isNotNull();
            result.addAll(readGroupSets);
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Utility method to fetch all {@link ReadGroup}s given a {@link ReadGroupSet} name.
     * @param client the {@link Client} connection to the server
     * @param name the name of a {@link ReadGroupSet}
     * @return all {@link ReadGroup}s in the {@link ReadGroupSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<ReadGroup> getReadGroupsForName(Client client, String name) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<ReadGroup> result = new LinkedList<>();
        String pageToken = "";

        do {
            final SearchReadGroupSetsRequest readGroupSetsReq =
                    SearchReadGroupSetsRequest
                            .newBuilder()
                            .setName(name)
                            .setDatasetId(TestData.getDatasetId())
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchReadGroupSetsResponse readGroupSetsResp =
                    client.reads.searchReadGroupSets(readGroupSetsReq);
            pageToken = readGroupSetsResp.getNextPageToken();
            final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSetsList();
            assertThat(readGroupSets).isNotEmpty().isNotNull();
            result.addAll(readGroupSets.stream()
                    .flatMap(rgs -> rgs.getReadGroupsList().stream())
                    .collect(Collectors.toList()));
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Utility method to fetch a ReferenceSetId given a {@link ReferenceSet#getAssemblyId()}.
     * @param client the connection to the server
     * @param assemblyId the {@link ReferenceSet#getAssemblyId()} of the {@link ReferenceSet}
     * @return The ReferenceSet ID
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getReferenceSetIdByAssemblyId(Client client, String assemblyId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAssemblyId(assemblyId)
                        .build();
        final SearchReferenceSetsResponse resp =
                client.references.searchReferenceSets(req);
        final List<ReferenceSet> refSets = resp.getReferenceSetsList();
        assertThat(refSets).isNotNull();
        assertThat(refSets).hasSize(1);
        final ReferenceSet refSet = refSets.get(0);
        return refSet.getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link VariantSet}.
     * @param client the connection to the server
     * @return the ID of a {@link VariantSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getVariantSetId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSetsList();
        assertThat(variantSets).isNotEmpty();
        return variantSets.get(0).getId();
    }

    /**
     * Convenience function for getting a variant set by name. When no set is found
     * matching the name returns the first variant set found.
     * @param client
     * @param name
     * @return
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */

    public static VariantSet getVariantSetByName(Client client, String name) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<VariantSet> variantSets = Utils.getAllVariantSets(client);
        for (VariantSet v: variantSets) {
            if (v.getName() == name) {
                return v;
            }
        }
        return variantSets.get(0);
    }

    /**
     * Search for and return all {@link Variant} objects in the {@link VariantSet} with ID
     * <tt>variantSetId</tt>, from <tt>start</tt> to <tt>end</tt>.
     * @param client the connection to the server
     * @param variantSetId the ID of the {@link VariantSet}
     * @param start the start of the range to search
     * @param end the end of the range to search
     * @return the {@link List} of results
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<Variant> getAllVariantsInRange(Client client,
                                                      String variantSetId,
                                                      long start, long end) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // get all variants in the range
        final List<Variant> result = new LinkedList<>();
        String pageToken = "";

        do {
            final SearchVariantsRequest vReq =
                    SearchVariantsRequest.newBuilder()
                            .setVariantSetId(variantSetId)
                            .setReferenceName(TestData.REFERENCE_NAME)
                            .setStart(start).setEnd(end)
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
            pageToken = vResp.getNextPageToken();
            result.addAll(vResp.getVariantsList());
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Search for and return all {@link VariantSet}s.
     *
     * @param client the connection to the server
     * @return the {@link List} of results
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<VariantSet> getAllVariantSets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<VariantSet> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchVariantSetsRequest req =
                    SearchVariantSetsRequest.newBuilder()
                            .setDatasetId(TestData.getDatasetId())
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);
            pageToken = resp.getNextPageToken();
            result.addAll(resp.getVariantSetsList());
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Search for and return all {@link CallSet}s in the {@link VariantSet} named by <tt>variantSetId</tt>.
     *
     * @param client the connection to the server
     * @param variantSetId the ID of the {@link VariantSet}
     * @return the {@link List} of results
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<CallSet> getAllCallSets(Client client, String variantSetId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<CallSet> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchCallSetsRequest callSetsSearchRequest =
                    SearchCallSetsRequest.newBuilder()
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .setVariantSetId(variantSetId)
                            .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);
            pageToken = csResp.getNextPageToken();
            result.addAll(csResp.getCallSetsList());
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Retrieve all {@link ReferenceSet}s.
     * @param client the connection to the server
     * @return a {@link List} of all {@link Reference}s in the first {@link ReferenceSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<ReferenceSet> getAllReferenceSets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<ReferenceSet> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchReferenceSetsRequest refSetsReq =
                    SearchReferenceSetsRequest.newBuilder()
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchReferenceSetsResponse refSetsResp =
                    client.references.searchReferenceSets(refSetsReq);
            pageToken = refSetsResp.getNextPageToken();
            result.addAll(refSetsResp.getReferenceSetsList());
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Retrieve all references in the {@link ReferenceSet} named by the reference set ID.
     *
     * @param client   the connection to the server
     * @param refSetId the ID of the {@link ReferenceSet} we're using
     * @return a {@link List} of all {@link Reference}s in the first {@link ReferenceSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<Reference> getAllReferences(Client client,String refSetId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<Reference> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchReferencesRequest refsReq =
                    SearchReferencesRequest.newBuilder()
                            .setReferenceSetId(refSetId)
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchReferencesResponse refsResp = client.references.searchReferences(refsReq);
            pageToken = refsResp.getNextPageToken();
            result.addAll(refsResp.getReferencesList());
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Given a reference ID, return all {@link ReadAlignment}s
     * @param client the connection to the server
     * @param referenceId the ID of the {@link Reference} we're using
     * @param readGroupId the ID of the {@link ReadGroup} we're using
     * @return all the {@link ReadAlignment} objects that match
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<ReadAlignment> getAllReads(Client client, String referenceId, String readGroupId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<ReadAlignment> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchReadsRequest req = SearchReadsRequest.newBuilder()
                    .setReferenceId(referenceId)
                    .addAllReadGroupIds(aSingle(readGroupId))
                    .setPageToken(pageToken)
                    .setPageSize(100)
                    .build();
            final SearchReadsResponse resp = client.reads.searchReads(req);
            result.addAll(resp.getAlignmentsList());
            pageToken = resp.getNextPageToken();
        } while (pageToken != null && !pageToken.equals(""));

        return result;
    }

    /**
     * Retrieve all {@link Dataset}s we're allowed to access.
     * @param client the connection to the server
     * @return all the {@link Dataset}s
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<Dataset> getAllDatasets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final List<Dataset> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchDatasetsRequest req =
                    SearchDatasetsRequest.newBuilder()
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchDatasetsResponse resp = client.metadata.searchDatasets(req);
            pageToken = resp.getNextPageToken();
            result.addAll(resp.getDatasetsList());
        } while (pageToken != null && !pageToken.equals(""));
        return result;
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link RnaQuantification}.
     * @param client the connection to the server
     * @return the ID of a {@link RnaQuantification}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getRnaQuantificationId(Client client, String rnaQuantificationSetId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchRnaQuantificationsRequest req =
                SearchRnaQuantificationsRequest.newBuilder()
                        .setRnaQuantificationSetId(rnaQuantificationSetId)
                        .build();
        final SearchRnaQuantificationsResponse resp = client.rnaquantifications.searchRnaQuantification(req);

        final List<RnaQuantification> rnaQuantifications = resp.getRnaQuantificationsList();
        assertThat(rnaQuantifications).isNotEmpty();
        return rnaQuantifications.get(0).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link RnaQuantificationSet}.
     * @param client the connection to the server
     * @return the ID of a {@link RnaQuantificationSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getRnaQuantificationSetId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchRnaQuantificationSetsRequest req =
                SearchRnaQuantificationSetsRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchRnaQuantificationSetsResponse resp = client.rnaquantifications.searchRnaQuantificationSets(req);
        final List<RnaQuantificationSet> rnaQuantificationSets = resp.getRnaQuantificationSetsList();
        assertThat(rnaQuantificationSets).isNotEmpty();
        return rnaQuantificationSets.get(0).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link ExpressionLevel}.
     * @param client the connection to the server
     * @param rnaQuantificationId the id of the rna quantification
     * @return the ID of a {@link ExpressionLevel}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getExpressionLevelId(Client client, String rnaQuantificationId) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchExpressionLevelsRequest req =
                SearchExpressionLevelsRequest.newBuilder()
                        .setRnaQuantificationId(rnaQuantificationId)
                        .build();
        final SearchExpressionLevelsResponse resp = client.rnaquantifications.searchExpressionLevel(req);
        final List<ExpressionLevel> expressionLevels = resp.getExpressionLevelsList();
        assertThat(expressionLevels).isNotEmpty();
        return expressionLevels.get(0).getId();
    }

    /**
     * Convenience method to catch a {@link GAWrapperException} and cast the return value to that type.
     * <b>Only use this when you're expecting the enclosed code to throw that exception.</b>
     * If the enclosed code doesn't throw the expected {@link GAWrapperException}, this method calls
     * {@link org.assertj.core.api.StrictAssertions#fail(String)} to cause the enclosing test to fail
     * and log the stack trace.
     * @param thisShouldThrow the {@link Callable} we're calling, which should throw {@link GAWrapperException}
     * @return the {@link Throwable} thrown in the execution of the {@link Callable}
     */
    public static GAWrapperException catchGAWrapperException(ThrowableAssert.ThrowingCallable
                                                                     thisShouldThrow) {
        final GAWrapperException maybeAnException =
                (GAWrapperException)catchThrowable(thisShouldThrow);
        if (maybeAnException == null) {
            // we were expecting an exception and didn't get one.  log it as a failure.
            fail("Expected but did not receive GAWrapperException");
        }
        return maybeAnException;
    }

    /**
     * Search for and return all {@link VariantAnnotation} objects in the {@link VariantAnnotationSet} with ID <tt>variantAnnotationSetId</tt>, from
     * <tt>start</tt> to <tt>end</tt>.
     *
     * @param client                 the connection to the server
     * @param variantAnnotationSetId the ID of the {@link VariantAnnotationSet}
     * @param start                  the start of the range to search
     * @param end                    the end of the range to search
     * @return the {@link List} of results
     * @throws GAWrapperException             if the server finds the request invalid in some way
     * @throws UnirestException               if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<VariantAnnotation> getAllVariantAnnotationsInRange(Client client,
                                                                          String variantAnnotationSetId,
                                                                          long start, long end)
            throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // get all variantAnnotations in the range
        final List<VariantAnnotation> result = new LinkedList<>();
        String pageToken = "";

        do {
            final SearchVariantAnnotationsRequest vReq =
                    SearchVariantAnnotationsRequest.newBuilder()
                            .setVariantAnnotationSetId(variantAnnotationSetId)
                            .setReferenceName(TestData.VARIANT_ANNOTATION_REFERENCE_NAME)
                            .setStart(start).setEnd(end)
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchVariantAnnotationsResponse vResp = client.variantAnnotations.searchVariantAnnotations(vReq);
            pageToken = vResp.getNextPageToken();
            result.addAll(vResp.getVariantAnnotationsList());
        } while (!pageToken.equals(""));

        return result;
    }

    /**
     * Utility method to fetch alist of {@link VariantAnnotationSet} given the ID of a {@link Dataset}.
     * @param client the connection to the server
     * @return a list of {@link VariantAnnotationSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<VariantAnnotationSet> getAllVariantAnnotationSets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // Get all compliance variant sets.
        final List<VariantSet> variantSetsCompliance = getAllVariantSets(client);

        //Check some sets are available.
        assertThat(variantSetsCompliance).isNotEmpty();

        // Build a list of VariantAnnotationSets.
        final List<VariantAnnotationSet> result = new LinkedList<>();

        // there may be multiple variantSets to check
        for (final VariantSet variantSet : variantSetsCompliance) {
            final SearchVariantAnnotationSetsRequest req =
                    SearchVariantAnnotationSetsRequest.newBuilder()
                            .setVariantSetId(variantSet.getId())
                            .build();

            final SearchVariantAnnotationSetsResponse resp = client.variantAnnotations.searchVariantAnnotationSets(req);
            if (resp.getVariantAnnotationSetsList() != null) {
                result.addAll(resp.getVariantAnnotationSetsList());
            }

        }
        return result;
    }

    /**
     * Utility method to fetch the Id of a {@link VariantAnnotationSet} for the compliance dataset.
     * @param client the connection to the server
     * @return the ID of a {@link VariantAnnotationSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getVariantAnnotationSetId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // get all compliance variant annotation sets
        final List<VariantAnnotationSet> variantAnnotationSets = getAllVariantAnnotationSets(client);
        return variantAnnotationSets.get(0).getId();
    }


    /**
     * Given a name return the variant annotation set corresponding to that name. When that name
     * is not found returns the first annotation set found.
     * @param client the connection to the server
     * @param name the string name of the annotation set
     * @return a {@link VariantAnnotationSet} with the requested name
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static VariantAnnotationSet getVariantAnnotationSetByName(Client client, String name) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // get all compliance variant annotation sets
        final List<VariantAnnotationSet> variantAnnotationSets = getAllVariantAnnotationSets(client);
        for (VariantAnnotationSet vas : variantAnnotationSets) {
            if (vas.getName().equals(name)) {
                return vas;
            }
        }
        return variantAnnotationSets.get(0);
    }

    /**
     * Search for and return all {@link FeatureSet}s.
     *
     * @param client the connection to the server
     * @return the {@link List} of results
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static List<FeatureSet> getAllFeatureSets(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        final List<FeatureSet> result = new LinkedList<>();
        String pageToken = "";
        do {
            final SearchFeatureSetsRequest req =
                    SearchFeatureSetsRequest.newBuilder()
                            .setDatasetId(TestData.getDatasetId())
                            .setPageSize(100)
                            .setPageToken(pageToken)
                            .build();
            final SearchFeatureSetsResponse resp = client.sequenceAnnotations.searchFeatureSets(req);
            pageToken = resp.getNextPageToken();
            result.addAll(resp.getFeatureSetsList());
        } while (!pageToken.equals(""));

        return result;
    }

    /**
     * Utility method to fetch the Id of a {@link FeatureSet} for the compliance dataset.
     * @param client the connection to the server
     * @return the ID of a {@link FeatureSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getFeatureSetId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // get all compliance feature sets
        final List<FeatureSet> featureSets = getAllFeatureSets(client);
        return featureSets.get(0).getId();
    }

    /**
     * Given a name, return the feature set corresponding to that name. When that name
     * is not found returns the first feature set found.
     * @param client the connection to the server
     * @param name the string name of the annotation set
     * @return a {@link FeatureSet} with the requested name
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static FeatureSet getFeatureSetByName(Client client, String name) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // get all compliance feature sets
        final List<FeatureSet> featureSets = getAllFeatureSets(client);
        for (FeatureSet fs : featureSets) {
            if (fs.getName().equals(name)) {
                return fs;
            }
        }
        return featureSets.get(0);
    }
    /**
     * Sugar for getting the first Biosample result that matches the name search request.
     * @param client
     * @param name The name of the Biosample
     * @return  Biosample
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static Biosample getBiosampleByName(Client client, String name) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchBiosamplesRequest req =
                SearchBiosamplesRequest.newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .setName(name)
                        .build();

        final SearchBiosamplesResponse resp = client.bioMetadata.searchBiosamples(req);
        return (Biosample)resp.getBiosamplesList().get(0);
    }

    /**
     * Utility method to fetch the Id of a {@link FeatureSet} for the compliance dataset.
     * @param client the connection to the server
     * @return the ID of a {@link FeatureSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    public static String getFeatureG2PSetId(Client client) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {

        // get all compliance feature sets
        final List<FeatureSet> featureSets = getAllFeatureSets(client);
        return featureSets.get(1).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link PhenotypeAssociationSet}.
     * @param client the connection to the server
     * @return the ID of a {@link PhenotypeAssociationSet}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     **/
    public static String getPhenotypeAssociationSetId(Client client) throws  InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final SearchPhenotypeAssociationSetsRequest req =
                SearchPhenotypeAssociationSetsRequest.newBuilder()
                        .setDatasetId(getDatasetId(client))
                        .build();
        final SearchPhenotypeAssociationSetsResponse resp = client.genotypePhenotype.searchPhenotypeAssociationSets(req);
        final List<PhenotypeAssociationSet> phenotypeAssociationSets = resp.getPhenotypeAssociationSetsList();
        assertThat(phenotypeAssociationSets).isNotEmpty();
        return phenotypeAssociationSets.get(0).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link Dataset}.
     * @param client the connection to the server
     * @return the ID of a {@link Dataset}
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     **/
    public static String getDatasetId(Client client) throws  InvalidProtocolBufferException, UnirestException, GAWrapperException  {
        final SearchDatasetsRequest req =
                SearchDatasetsRequest.newBuilder()
                        .build();
        final SearchDatasetsResponse resp = client.metadata.searchDatasets(req);
        assertThat(resp.getDatasetsList()).isNotEmpty();
        return resp.getDatasets(0).getId() ;
    }
}
