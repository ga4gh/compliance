/**
 * <p>This package contains the actual compliance-verifying tests.
 * <p>
 * These tests are normally run by the maven '<tt>surefire</tt>' plugin, as configured
 * in the <tt>pom.xml</tt>. They are normal JUnit4 tests written in Java.
 *
 * <h2>Creating Tests</h2>
 * <ul>
 * <li>Name your test (following the behavior-naming model, such as
 * '<tt>badDatasetIdShouldGet404</tt>')</li>
 * <li>Annotate it with '<tt>@Test</tt>'</li>
 * </ul>
 *
 * <p>
 * To parameterize your test so that it gets run in a data-driven style, you'll need to
 * have it in a class declared to <tt>@RunWith(JUnitParamsRunner.class)</tt>, and then annotate
 * your test methods with <tt>@Parameters(...)</tt>.
 * <p>There's more info on parameterized tests on the
 * <a href="https://github.com/Pragmatists/JUnitParams">JUnitParams GitHub site</a>.
 * <h2>Running Tests</h2>
 * <p>
 * You can run tests from the command-line via the <tt>ctk</tt> shell-script, or using a Web
 * interface.
 */
package org.ga4gh.cts.api;

