package org.ga4gh.cts.core;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.junit.runner.RunWith;

/**
 * This suite runs tests of the core API.
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories(CoreTests.class)
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class CoreTestSuite {
}
