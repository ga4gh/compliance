package org.ga4gh.cts.api.sequenceAnnotations;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.ga4gh.cts.api.sequenceAnnotations.SequenceAnnotationTests;
import org.junit.runner.RunWith;

/**
 * <p>This suite runs the "sequence annotations" category of tests.</p>
 * <p>Uses {@link WildcardPatternSuite} runner, so use junittoolbox's {@code @IncludeCategories}
 * or {@code ExcludeCategories} if you need to customize.</p>
 * <p>If there are no tests categorized as {@code SequenceAnnotationTests} then you'll get
 * a {@code NoTestsRemainException}</p>
 * <p>
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories(SequenceAnnotationTests.class)
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class SequenceAnnotationsTestSuite {
}
