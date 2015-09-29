package org.ga4gh.ctk;

import static org.slf4j.LoggerFactory.*;

/**
 * <p>Utility interface to bring in the loggers.</p>
 * <p>If you "implements CtkLogs" then your class gets the
 * default 'log' and 'test' log to work with, saving you
 * a bit of typing and increasing likelihood of using
 * consistent logger names. Recommended for integration
 * tests, but might be a little slow for super lightweight
 * tests in a loop (getting a stacktrace isn't quick); if
 * you want to go faster or avoid inherited-method magic,
 * just use: </p>
 * <pre>
 * {@code
 *    static org.slf4j.Logger testlog = getLogger("TESTLOG");
 *    static org.slf4j.Logger trafficlog = getLogger("TESTLOG.TRAFFIC");
 *    static org.slf4j.Logger log = getLogger(<myclass>.class);
 * }*
 * </pre>
 * <p>Created by Wayne Stidolph on 6/29/2015.</p>
 */
public interface CtkLogs {
    String SYSTEST = "TESTLOG";
    /**
     * The TESTLOG.
     */
    static org.slf4j.Logger testlog = getLogger(SYSTEST);

    static String TRAFFICLOG=SYSTEST + ".TRAFFIC";
    static org.slf4j.Logger trafficlog = getLogger(TRAFFICLOG);

    /**
     * <p>Sets the log according to the name of the class which 'implements CtkLogs'.</p>
    */
    static org.slf4j.Logger log = getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    // stackTrace()[1] would be this interface, [2] is the invocation of this log method
    // so we can use the name of that implementing-class as the logger name
    // can we do this just once somehow? can a default method check if 'log' is already init ...

    /**
     * Digs the method name out of the stacktrace (slow but occasionally useful).
     */
    String thisMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
}
