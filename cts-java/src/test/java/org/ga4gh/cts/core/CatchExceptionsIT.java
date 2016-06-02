package org.ga4gh.cts.core;

import org.assertj.core.api.ThrowableAssert;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.cts.api.Utils;
import ga4gh.Common.GAException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test handling and logging exceptions.
 *
 * @author Herb Jellinek
 */
@Category(CoreTests.class)
public class CatchExceptionsIT {

    /**
     * Wrap a piece of code that does not throw an exception in {@link Utils#catchGAWrapperException(ThrowableAssert.ThrowingCallable)}
     * and make sure it doesn't assume it's an exception.
     * <p>
     * We expect this test always to fail under all circumstances, simply
     * as a condition of how it's written.  We'll mark it with @Ignore so it doesn't look like a test that
     * was expected to succeed failed.
     * </p>
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Ignore("Exercises some logging and written always to fail, so no need to run it.")
    @Test
    public void testHandlingNonException() {
        // this should log an error also
        GAWrapperException e = Utils.catchGAWrapperException(this::doNotThrowGAWrapperException);
        assertThat(e).isNull();
    }

    /**
     * Wrap a piece of code that throws an exception in {@link Utils#catchGAWrapperException(ThrowableAssert.ThrowingCallable)}
     * and make sure it logs it.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testHandlingException() {
        GAWrapperException e = Utils.catchGAWrapperException(this::throwGAWrapperException);
        assertThat(e).isNotNull();
        assertThat(e.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Like it says on the tin: do not throw an exception.  In fact, do nothing.
     */
    private void doNotThrowGAWrapperException() {
        // do nothing
    }

    /**
     * Throw a {@link GAWrapperException}.
     */
    private void throwGAWrapperException() throws GAWrapperException {
        throw new GAWrapperException(GAException.newBuilder().setMessage("synthetic").build(),
                HttpURLConnection.HTTP_NOT_FOUND);
    }

}
