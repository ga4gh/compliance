package org.ga4gh.cts.api.references;

/**
 * <p>This test fetches the target server landing page as evidence of connectivity;
 * the test also scrapes the supplied HTML as fodder for the eventual report.</p>
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */

import org.ga4gh.ctk.*;
import org.junit.*;
import org.junit.experimental.categories.*;

import static org.junit.Assert.*;

@Category(ReferencesTests.class)
public class DummyTestIT implements CtkLogs {
    /**
     * Dummy so ReferencesTestSuite can initialize.
     *
     * <p>Delete when there's real tests, this just lets us put the
     * TestSuite in place as a visual reminder of the need to write
     * tests.</p>
     *
     * @throws Exception the exception
     */
    @Test
    @Ignore("Placeholder, not yet written")
    public void dummy() throws Exception {
        fail("We should not run a Dummy!");
    }
}
