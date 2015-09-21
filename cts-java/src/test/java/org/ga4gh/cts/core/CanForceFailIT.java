package org.ga4gh.cts.core;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Sanity test: setting the system property "cts.demofail" will
 * force this test to fail.
 *
 * @author Herb Jellinek
 * Abstracted from class <tt>LandingPageIT</tt> by Wayne Stidolph.
 */
@Category(CoreTests.class)
public class CanForceFailIT implements CtkLogs {

    private static final String FAILURE_PROPERTY_NAME = "cts.demofail";

    /**
     * <p>Show that tests can fail.</p>
     * <p>By querying for a system property <tt>cts.demofail</tt> this
     * test shows that tests can fail. This optional failure thus demonstrates the CTK
     * capabilities of linking from the generated Surefire report directly
     * to the failing line of test code.</p>
     *
     * @throws Exception the exception
     */
    @Test
    public void propertyCanCauseTestFail() throws Exception {

        if (Boolean.getBoolean(FAILURE_PROPERTY_NAME)) {
            testlog.warn("Forcing failure because "+FAILURE_PROPERTY_NAME+" is true");
            assertThat(false).isTrue();
        } else {
            assertThat(false).isFalse();
        }
    }

}
