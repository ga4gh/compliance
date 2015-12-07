package org.ga4gh.cts.api.g2p;

import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;

import org.ga4gh.ctk.testcategories.CoreTests;
import org.ga4gh.cts.api.datasets.DatasetsTests;
import org.junit.runner.RunWith;

/**
 * <p>This suite runs the tests in the "GenotypePhenotype" category.</p>
 * <p>It uses the {@link WildcardPatternSuite} runner, so use <tt>com.googlecode.junittoolbox</tt>'s
 * {@code @IncludeCategories} or {@code ExcludeCategories} to customize.</p>
 * <p>If there are no tests categorized as {@code DatasetsTests} then you'll get
 * a {@code NoTestsRemainException}</p>
 *
 * @author Brian Walsh
 */
@RunWith(WildcardPatternSuite.class)
@IncludeCategories({CoreTests.class, GenotypePhenotypeTests.class})
@SuiteClasses({"**/*IT.class", "**/*Test.class"})
public class GenotypePhenotypeTestSuite {
}
