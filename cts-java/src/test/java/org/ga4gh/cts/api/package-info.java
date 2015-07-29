/**
 * <p>The actual conformance-verifying tests.
 * <p>
 * These tests are normally run by the maven 'surefire' plugin, as configured
 * in the pom.xml file. They are normal JUnit4 java tests, or Spock groovy tests.

 * <h2>Creating Tests</h2>
 * <ul>
 * <li>Name your test (following the behavior-naming model, such as 'badDatasetIdShouldGet404')</li>
 * <li>Annotate  it with '@Test'</li>
 * </ul>

 * <p>
 * To parameterize your test, so that it gets run in a data-driven style, you'll need to
 * have it in a class declared to @RunWith(JUnitParamsRunner.class), and then annotate
 * your test methods with @Parameters(...).
 * <p>There's more info on parameterized tests on the
 * <a href="https://github.com/Pragmatists/JUnitParams">JUnitParams github site</a>.
 * <h2>Running Tests</h2>
 * <p>
 * You can run tests from the command-line via the <pre>ctk</pre> shell-script, or using a Web interface.
 * More info coming....
 */
package org.ga4gh.cts.api;

