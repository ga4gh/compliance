package org.ga4gh.cts.api;

import org.apache.avro.AvroRemoteException;
import org.assertj.core.api.ThrowableAssert;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.methods.*;
import org.ga4gh.models.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.catchThrowable;

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
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    public static String getValidReferenceId(Client client) throws AvroRemoteException {
        final SearchReferenceSetsRequest refSetsReq = SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse refSetsResp = client.references.searchReferenceSets(refSetsReq);

        final List<ReferenceSet> refSets = refSetsResp.getReferenceSets();

        final SearchReferencesRequest refsReq = SearchReferencesRequest
                .newBuilder()
                .setReferenceSetId(refSets.get(0).getId())
                .build();
        final SearchReferencesResponse refsResp = client.references.searchReferences(refsReq);
        assertThat(refsResp).isNotNull();
        final List<Reference> references = refsResp.getReferences();
        assertThat(references).isNotNull().isNotEmpty();

        return references.get(0).getId();
    }

    /**
     * Utility method to fetch the ID of an arbitrary {@link ReadGroup}.
     * @param client the {@link Client} connection to the server
     * @return the ID of an arbitrary {@link ReadGroup}
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    public static String getReadGroupId(Client client) throws AvroRemoteException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest
                        .newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSets();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        List<ReadGroup> readGroups = readGroupSet.getReadGroups();
        assertThat(readGroups).isNotEmpty().isNotNull();
        final ReadGroup readGroup = readGroups.get(0);
        assertThat(readGroup).isNotNull();
        return readGroup.getId();
    }

    /**
     * Utility method to fetch the ID of a {@link ReadGroup} in a named {@link ReadGroupSet}.
     * @param client the {@link Client} connection to the server
     * @param name the name of a ReadGroup
     * @return the ID of an arbitrary {@link ReadGroup}
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    public static String getReadGroupIdForName(Client client, String name) throws AvroRemoteException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest.newBuilder()
                                          .setDatasetId(TestData.getDatasetId())
                                          .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSets();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        List<ReadGroup> readGroups = readGroupSet.getReadGroups();
        Optional<ReadGroup> result =
                readGroups.stream().filter(readGroup -> name.equals(readGroup.getName())).findFirst();
        return result.isPresent() ? result.get().getId() : null;
    }

    /**
     * Utility method to fetch all {@link ReadGroupSet}s.
     * @param client the {@link Client} connection to the server
     * @return all {@link ReadGroupSet}s
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    public static List<ReadGroupSet> getAllReadGroupSets(Client client)
            throws AvroRemoteException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest
                        .newBuilder()
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSets();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        return readGroupSets;
    }


    /**
     * Utility method to fetch all {@link ReadGroup}s given a {@link ReadGroupSet} name.
     * @param client the {@link Client} connection to the server
     * @param name the name of a {@link ReadGroupSet}
     * @return all {@link ReadGroup}s in the {@link ReadGroupSet}
     * @throws AvroRemoteException is the server throws an exception or there's an I/O error
     */
    public static List<ReadGroup> getReadGroupsForName(Client client, String name)
            throws AvroRemoteException {
        final SearchReadGroupSetsRequest readGroupSetsReq =
                SearchReadGroupSetsRequest
                        .newBuilder()
                        .setName(name)
                        .setDatasetId(TestData.getDatasetId())
                        .build();
        final SearchReadGroupSetsResponse readGroupSetsResp =
                client.reads.searchReadGroupSets(readGroupSetsReq);
        assertThat(readGroupSetsResp).isNotNull();
        final List<ReadGroupSet> readGroupSets = readGroupSetsResp.getReadGroupSets();
        assertThat(readGroupSets).isNotEmpty().isNotNull();
        final ReadGroupSet readGroupSet = readGroupSets.get(0);
        List<ReadGroup> readGroups = readGroupSet.getReadGroups();
        assertThat(readGroups).isNotNull();
        return readGroups;
    }

    /**
     * Utility method to fetch a ReferenceSetId given a {@link ReferenceSet#assemblyId}.
     * @param client the connection to the server
     * @param assemblyId the {@link ReferenceSet#assemblyId} of the {@link ReferenceSet}
     * @return The ReferenceSet ID
     *
     * @throws AvroRemoteException if the server throws an exception or there's an I/O error
     */
    public static String getReferenceSetIdByAssemblyId(Client client, String assemblyId) throws AvroRemoteException {
        final SearchReferenceSetsRequest req =
                SearchReferenceSetsRequest.newBuilder()
                        .setAssemblyId(assemblyId)
                        .build();
        final SearchReferenceSetsResponse resp =
                client.references.searchReferenceSets(req);
        final List<ReferenceSet> refSets = resp.getReferenceSets();
        assertThat(refSets).isNotNull();
        final ReferenceSet refSet = refSets.get(0);
        return refSet.getId();
    }

    /**
     * Utility method to fetch the ID off an arbitrary {@link VariantSet}.
     * @param client the connection to the server
     * @return the ID of a {@link VariantSet}
     * @throws AvroRemoteException if the server throws an exception or there's an I/O error
     */
    public static String getVariantSetId(Client client) throws AvroRemoteException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);

        final List<VariantSet> variantSets = resp.getVariantSets();
        assertThat(variantSets).isNotEmpty();
        return variantSets.get(0).getId();
    }

    /**
     * Search for and return all {@link Variant} objects in the {@link VariantSet} with ID
     * <tt>variantSetId</tt>, from <tt>start</tt> to <tt>end</tt>.
     * @param client the connection to the server
     * @param variantSetId the ID of the {@link VariantSet}
     * @param start the start of the range to search
     * @param end the end of the range to search
     * @return the {@link List} of results
     * @throws AvroRemoteException if the server throws an exception or there's an I/O error
     */
    public static List<Variant> getAllVariantsInRange(Client client,
                                                      String variantSetId,
                                                      long start, long end) throws AvroRemoteException {
        // get all variants in the range
        final SearchVariantsRequest vReq =
                SearchVariantsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .build();
        final SearchVariantsResponse vResp = client.variants.searchVariants(vReq);
        return vResp.getVariants();
    }

    /**
     * Search for and return all {@link VariantSet}s.
     *
     * @param client the connection to the server
     * @return the {@link List} of results
     * @throws AvroRemoteException if the server throws an exception or there's an I/O error
     */
    public static List<VariantSet> getAllVariantSets(Client client) throws AvroRemoteException {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse resp = client.variants.searchVariantSets(req);
        return resp.getVariantSets();
    }

    /**
     * Search for and return all {@link CallSet}s in the {@link VariantSet} named by <tt>variantSetId</tt>.
     *
     * @param client the connection to the server
     * @param variantSetId the ID of the {@link VariantSet}
     * @return the {@link List} of results
     * @throws AvroRemoteException if the server throws an exception or there's an I/O error
     */
    public static List<CallSet> getAllCallSets(Client client,
                                               String variantSetId) throws AvroRemoteException {
        final SearchCallSetsRequest callSetsSearchRequest =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .build();
        final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);
        return csResp.getCallSets();
    }

    /**
     * Convenience method to catch a {@link GAWrapperException} and cast the return value to that type.
     * @param shouldRaiseThrowable the {@link Callable} we're calling
     * @return the {@link Throwable} thrown in the execution of the {@link Callable}
     */
    public static GAWrapperException catchGAWrapperException(ThrowableAssert.ThrowingCallable
                                                                      shouldRaiseThrowable) {
        return (GAWrapperException)catchThrowable(shouldRaiseThrowable);
    }

    /**
     * Retrieve all {@link ReferenceSet}s.
     * @param client the connection to the server
     * @return a {@link List} of all {@link Reference}s in the first {@link ReferenceSet}
     */
    public static List<ReferenceSet> getAllReferenceSets(Client client) throws AvroRemoteException {
        final SearchReferenceSetsRequest refSetsReq = SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse refSetsResp = client.references.searchReferenceSets(refSetsReq);

        return refSetsResp.getReferenceSets();
    }

    /**
     * Retrieve all references in the {@link ReferenceSet} named by the reference set ID.
     *
     * @param client   the connection to the server
     * @param refSetId the ID of the {@link ReferenceSet} we're using
     * @return a {@link List} of all {@link Reference}s in the first {@link ReferenceSet}
     */
    public static List<Reference> getAllReferences(Client client,
                                                   String refSetId) throws AvroRemoteException {
        final SearchReferencesRequest refsReq =
                SearchReferencesRequest.newBuilder()
                                       .setReferenceSetId(refSetId)
                                       .build();
        final SearchReferencesResponse refsResp = client.references.searchReferences(refsReq);
        return refsResp.getReferences();
    }

    /**
     * Given a reference ID, return all {@link ReadAlignment}s
     * @param client the connection to the server
     * @param referenceId the ID of the {@link Reference} we're using
     * @param readGroupId the ID of the {@link ReadGroup} we're using
     * @return all the {@link ReadAlignment} objects that match
     */
    public static List<ReadAlignment> getAllReads(Client client, String referenceId,
                                                  String readGroupId) throws AvroRemoteException {
        final SearchReadsRequest req = SearchReadsRequest.newBuilder()
                .setReferenceId(referenceId)
                .setReadGroupIds(aSingle(readGroupId))
                .build();
        final SearchReadsResponse resp = client.reads.searchReads(req);
        return resp.getAlignments();
    }

    /**
     * Retrieve all {@link Dataset}s we're allowed to access.
     * @param client the connection to the server
     * @return all the {@link Dataset}s
     */
    public static List<Dataset> getAllDatasets(Client client) throws AvroRemoteException {
        final SearchDatasetsRequest req = SearchDatasetsRequest.newBuilder().build();
        return client.reads.searchDatasets(req).getDatasets();

    }
}
