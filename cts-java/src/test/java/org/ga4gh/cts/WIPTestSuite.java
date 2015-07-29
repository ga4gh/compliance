package org.ga4gh.cts;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.ga4gh.ctk.testcategories.WIP;
import org.junit.runner.RunWith;

/**
 * <p>This test suite collects all the unit or integration tests which are
 * tagged with {@code @Category(WIP.class)} and follow the naming
 * convention of ending in 'IT' (the integration tests) or
 * ending in 'Test' (the unit-level tests)</p>
 *
 * Created by Wayne Stidolph on 6/7/2015.
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories(WIP.class)
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class WIPTestSuite {
}
