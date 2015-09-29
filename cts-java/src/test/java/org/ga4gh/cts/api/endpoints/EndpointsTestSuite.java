package org.ga4gh.cts.api.endpoints;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.junit.runner.RunWith;

/**
 * <p>This suite runs the "endpoints" category of tests.</p>
 * <p>Uses {@link WildcardPatternSuite} runner, so use junittoolbox's {@code @IncludeCategories}
 * or {@code ExcludeCategories} if you need to customize.</p>
 * <p>If there are no tests categorized as {@code EndpointsTests} then you'll get
 * a {@code NoTestsRemainException}</p>
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories({CoreTests.class, EndpointsTests.class})
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class EndpointsTestSuite {
}
