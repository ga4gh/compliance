package org.ga4gh.ctk;

import junit.framework.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.optional.junit.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;
import org.slf4j.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.*;

import java.io.*;

/**
 * <p>Route JUnit test events into the TESTLOG</p>
 * <p>Normal use is to be attached to a JunitCore as a listener, or to
 * be instantiated as a junit task "formatter" inside an ant buildfile.</p>
 * <p>Created by Wayne Stidolph on 6/23/2015.</p>
 */
@Component
@Scope("prototype")
public class TestExecListener extends RunListener implements JUnitResultFormatter  {

    static long runCount = 0L; // these are the overall run totals
    static long failureCount = 0L;
    static long errorCount = 0L;
    static long skipCount  = 0L;
    static float ms = 0.0f;

    private static org.slf4j.Logger testlog = LoggerFactory.getLogger("TESTLOG");

    /**
     * <p>Gets test report summary string.</p>
     *
     * <p>Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.3f sec</p>
     *
     * @return the test report
     */
    public static String getTestReport() {
        return String.format("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.3f sec",
                runCount, failureCount, errorCount, skipCount, ms);
    }

    /**
     * <p>Reset stats</p>
     */
    public static void resetStats() {
        runCount = 0L; // these are the overall run totals
        failureCount = 0L;
        errorCount = 0L;
        skipCount  = 0L;
        ms = 0.0f;
    }

    /******** first methods are for when directly attached to the JUnitCore ***/
    /**
     * Called before any tests have been run.
     */
    public void testRunStarted(Description description) throws java.lang.Exception {
        testlog.info("Number of testcases to execute : " + description.testCount());
    }

    /**
     * Called when all tests have finished
     */
    public void testRunFinished(Result result) throws java.lang.Exception {
        testlog.info("Number of testcases executed : " + result.getRunCount());
    }

    /**
     * Called when an atomic test is about to be started.
     */
    public void testStarted(Description description) throws java.lang.Exception {
        testlog.info("Starting test case : " + description.getMethodName());
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     */
    public void testFinished(Description description) throws java.lang.Exception {
        testlog.debug("Finished test case : " + description.getMethodName());
    }

    /**
     * Called when an atomic test fails.
     */
    public void testFailure(Failure failure) throws java.lang.Exception {
        testlog.warn("FAILED test case : " + failure.getMessage());
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated with Ignore.
     */
    public void testIgnored(Description description) throws java.lang.Exception {
        testlog.info("Ignoring test case : " + description.getMethodName());
    }

    /****** JUnitResultFormatter methods, for listening to ant <junit> run ***/
    /**
     * The whole testsuite started in ant.
     *
     * @param suite the suite.
     * @throws BuildException on error.
     */
    @Override
    public void startTestSuite(JUnitTest suite) throws BuildException {
        testlog.info("Suite start " + suite.getName());
    }

    /**
     * The whole testsuite ended.
     *
     * @param suite the suite.
     * @throws BuildException on error.
     */
    @Override
    public void endTestSuite(JUnitTest suite) throws BuildException {

        long suiteRunCount = suite.runCount();
        long suiteFailureCount = suite.failureCount();
        long suiteErrorCount = suite.errorCount();
        long suiteSkipCount  = suite.skipCount();
        float suiteMs = suite.getRunTime()/1000.0f;

        // track overall
        runCount     += suiteRunCount;
        failureCount += suiteFailureCount;
        errorCount   += suiteErrorCount;
        skipCount    += suiteFailureCount;
        ms           += suiteMs;

        String suiteSummary= String.format("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.3f sec",
                suiteRunCount, suiteFailureCount,suiteErrorCount,suiteSkipCount, suiteMs);
        testlog.info(suiteSummary);
    }



    /**
     * Sets the stream the formatter is supposed to write its results to.
     *
     * @param out the output stream to use.
     */
    @Override
    public void setOutput(OutputStream out) {

    }

    /**
     * This is what the test has written to System.out
     *
     * @param out the string to write.
     */
    @Override
    public void setSystemOutput(String out) {

    }

    /**
     * This is what the test has written to System.err
     *
     * @param err the string to write.
     */
    @Override
    public void setSystemError(String err) {

    }

    /**
     * An error occurred.
     *
     * @param test
     * @param e
     */
    @Override
    public void addError(Test test, Throwable e) {
        testlog.error("ERROR: " + test.toString() + " due to " + e.getMessage());
    }

    /**
     * A failure occurred.
     *
     * @param test
     * @param e
     */
    @Override
    public void addFailure(Test test, AssertionFailedError e) {
        testlog.warn("FAILED " + test.toString() + " due to " + e.getMessage());
    }

    /**
     * A test ended.
     *
     * @param test
     */
    @Override
    public void endTest(Test test) {
        testlog.debug("test: " + test.toString());
    }

    /**
     * A test started.
     *
     * @param test
     */
    @Override
    public void startTest(Test test) {
        testlog.trace("start test: " + test.toString());
    }
}
