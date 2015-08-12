package org.ga4gh.cts.api;

import org.ga4gh.models.Program;
import org.ga4gh.models.ReadAlignment;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
     * This is a null suitable for use where we might want to pass a {@link Program}.
     */
    public static final Program nullProgram = null;

    /**
     * Certain AssertJ methods accept a variable number of args: <tt>assertThat(Collection).doesNotContain(...)</tt>,
     * for instance.  Sometimes we want to pass null to such a method, but the IDE complains that this is "confusing."
     * If we supply a typed value, the complaint goes away.
     * This is a null suitable for use where we might want to pass a {@link ReadAlignment}.
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
     * @return an ID that's (virtually) guaranteed not to name a real object
     */
    public static String randomId() {
        return UUID.randomUUID().toString();
    }

}
