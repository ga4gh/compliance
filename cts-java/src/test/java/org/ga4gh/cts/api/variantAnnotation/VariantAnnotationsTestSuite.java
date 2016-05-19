package org.ga4gh.cts.api.variantAnnotation;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;

/**
 * <p>This suite runs the "variant annotations" category of tests.</p>
 * <p>Uses {@link WildcardPatternSuite} runner, so use junittoolbox's {@code @IncludeCategories}
 * or {@code ExcludeCategories} if you need to customize.</p>
 * <p>If there are no tests categorized as {@code VariantAnnotationTests} then you'll get
 * a {@code NoTestsRemainException}</p>
 * <p>
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories(VariantAnnotationTests.class)
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class VariantAnnotationsTestSuite {
}
