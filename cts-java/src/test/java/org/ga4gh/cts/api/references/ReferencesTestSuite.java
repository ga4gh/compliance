package org.ga4gh.cts.api.references;

import com.googlecode.junittoolbox.*;
import org.junit.runner.*;

/**
 * <p>This suite runs the "REFERENCES" category of tests.</p>
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
