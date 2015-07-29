package org.ga4gh.ctk;

import org.ga4gh.ctk.config.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;

import java.net.*;
import java.util.*;

/**
 * <p>The Application - this is the main entry point for the CTK for running the entire CTK
 * from a command line or an IDE, and is the CTK entry point if running from the executable jar
 * packaging.</p>
 * <p>(You can also run the CTK using {@code mvn spring-boot:run}, or you can run tests/suites one at a time
 * in your IDE.)</p>
 * <p>This entry is a Spring Boot app, so you can refer to their documentation for alternative run-methods.
 *
 * <p>Test tracking to TESTLOG is:
 * <ul>
 *     <li>warn: test failure reports</li>
 *     <li>info: summary line (failed, passed, skipped, time)</li>
 *     <li>debug: test name-matching info, count of test classes, count of test cases to run, test cases as they complete</li>
 *     <li>trace: show test case start as well as complete (helpful if hang)</li>
 * </ul>
 * <p>Test Anything Protocol (TAP) files are output to target/ dir.</p>
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html">
 * SpringBoot: Running Your Application</a>
 * @see <a href="https://testanything.org">Test Anything Protocol</a>
 *
 * <p>Created by Wayne Stidolph</p>
 */
@SpringBootApplication
public class Application implements CtkLogs,CommandLineRunner {

   // private static org.slf4j.Logger log = getLogger(Application.class);

    //private static org.slf4j.Logger testlog = getLogger(SYSTEST);


    @Autowired
    private Props props;
    public void setProps(Props props){
        this.props = props;
    }

    @Autowired
    private TestRunner testrunner;
    public void setTestrunner(TestRunner testrunner){this.testrunner = testrunner;}

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }

    /**
     * Callback used to execute the actual app (run the CTK tests).
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        // log some startup path-debug info for "why aren't my tests seen?"
        URL location = Application.class.getProtectionDomain().getCodeSource().getLocation();
        log.debug("Application launched from " + location.getFile());
        log.debug("command line args: " + Arrays.toString(args));

        testrunner.doTestRun(); // does a single run
    }
}