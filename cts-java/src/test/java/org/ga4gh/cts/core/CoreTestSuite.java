package org.ga4gh.cts.core;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.junit.runner.RunWith;

/**
 * Created by Wayne Stidolph on 6/7/2015.
 */

@RunWith(WildcardPatternSuite.class)
@IncludeCategories(CoreTests.class)
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class CoreTestSuite {
}
