package org.ga4gh.cts.api.references;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;

/**
 * <p>This suite runs the "references" category of tests.</p>
 * <p>Uses {@link WildcardPatternSuite} runner, so use junittoolbox's {@code @IncludeCategories}
 * or {@code ExcludeCategories} if you need to customize.</p>
 * <p>If there are no tests categorized as {@code ReferencesTests} then you'll get
 * a {@code NoTestsRemainException}</p>
 * Created by Wayne Stidolph on 6/7/2015.
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories(ReferencesTests.class)
@SuiteClasses({"**/*IT.class","**/*Test.class"})
public class ReferencesTestSuite {
}
