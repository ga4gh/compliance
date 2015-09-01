package org.ga4gh.cts.api;

import org.apache.avro.AvroRemoteException;
import org.assertj.core.api.ThrowableAssert;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.methods.SearchReadGroupSetsRequest;
import org.ga4gh.methods.SearchReadGroupSetsResponse;
import org.ga4gh.methods.SearchReferenceSetsRequest;
import org.ga4gh.methods.SearchReferenceSetsResponse;
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
        final SearchReferenceSetsRequest refSetReq = SearchReferenceSetsRequest.newBuilder().build();
        final SearchReferenceSetsResponse refSetResp = client.references.searchReferenceSets(refSetReq);
        assertThat(refSetResp).isNotNull();
        assertThat(refSetResp.getReferenceSets()).isNotNull().isNotEmpty();
        final ReferenceSet refSet = refSetResp.getReferenceSets().get(0);

        final List<String> refIds = refSet.getReferenceIds();
        assertThat(refIds).isNotNull().isNotEmpty();
        return refIds.get(0);
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
     * Convenience method to catch a {@link GAWrapperException} and cast the return value to that type.
     * @param shouldRaiseThrowable the {@link Callable} we're calling
     * @return the {@link Throwable} thrown in the execution of the {@link Callable}
     */
    public static GAWrapperException catchGAWrapperException(ThrowableAssert.ThrowingCallable
                                                                      shouldRaiseThrowable) {
        return (GAWrapperException)catchThrowable(shouldRaiseThrowable);
    }
}
