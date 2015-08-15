package org.ga4gh.ctk;

import com.google.common.collect.*;
import org.apache.tools.ant.*;
import org.ga4gh.ctk.config.*;
import org.ga4gh.ctk.transport.*;
import org.ga4gh.ctk.transport.avrojson.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.*;

import java.util.concurrent.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>This class runs the tests; it can be invoked from the command line runner,
 * or from a server-runner.</p>
 * Created by Wayne Stidolph on 7/11/2015.
 */
@Component
@Scope("prototype")
public class TestRunner implements BuildListener {
    static String SYSTEST = "TESTLOG";
    static String TRAFFICLOG=SYSTEST + ".TRAFFIC";
    private static org.slf4j.Logger trafficlog = getLogger(TRAFFICLOG);
    private static Logger log = getLogger(TestRunner.class);

    @Autowired
    private Props props;
    public void setProps(Props props){
        this.props = props;
    }

    @Autowired
    private TestExecListener testExecListener;
    public void setTestExecListener(TestExecListener testExecListener) {
        this.testExecListener = testExecListener;
    }

    @Autowired
    private AntExecutor antExecutor;

    @Value("${ctk.tgt.urlRoot}")
    String urlroot;

    // this is the object we use to pass final result status back
    CompletableFuture<String> result;

    /**
     * String name of the directory under which to put the test results
     */
    private String acceptedTargetDir="";

    /**
     * Default invocation, does test run using properties
     * ctk.tgt.urlRoot, ctk_matchstr, ctk_testjar;
     *
     * @return a Future with the string to use as a testrun identifier.
     */
    public CompletableFuture<String> doTestRun() {

        String matchStr = props.ctk_matchstr;
        CtkLogs.log.debug("matchStr: " + matchStr);
        String resultDir = ResultsSupport.getResultsDir(urlroot);

        return doTestRun(urlroot,matchStr,props.ctk_testjar,resultDir);

    }

    /**
     * Do test run using specific parameters.
     *
     * @param urlRoot the url root
     * @param matchStr the match str
     * @param testJar the test jar
     * @param toDir the directory to put result into (default =".")
     * @return a Future with the string to use as a testrun identifier.
     */
    public CompletableFuture<String> doTestRun(String urlRoot,
                                               String matchStr,
                                               String testJar,
                                               String toDir){
        URLMAPPING urls = URLMAPPING.getInstance();
        urls.setUrlRoot(urlRoot);

        AvroJson.shouldDoComms = true; // always start from assumption of goodness!

        // is there a pattern we should enforce?
        acceptedTargetDir =
                ((null == toDir || toDir.isEmpty()) ? "target/" : toDir);
        // TODO ensure the toDir exists, create here

                    /* ****** MAIN RUN-THE-TESTS *********** */

        result = new CompletableFuture<String>();
        boolean goodLaunch = antExecutor.executeAntTask(testJar,
                matchStr,
                urls,
                acceptedTargetDir,
                this); // "this" registers this for the BuildListener callbacks
        if(!goodLaunch){
            log.warn("bad test run for " + acceptedTargetDir + " " + testJar + " " + matchStr + " urls: " + urls);
            result.complete("");
        }

        // see finish processing n buildFinished()
        return result;
    }

    /**** CATCH BUILD EVENTS FOR THE BUILD WE LAUNCHED ****/

    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     * <p>
     * <p>This event is fired before the project instance is fully
     * configured.  In particular no properties have been set and the
     * project may not know its name or default target, yet.</p>
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    @Override
    public void buildStarted(BuildEvent event) {

    }

    /**
     * Signals that the last target has finished. This event
     * will still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    @Override
    public void buildFinished(BuildEvent event) {
       /* ******* post-Test reporting ********* */
        // ant file runs junitreporter, so those reports are done
        // just log the traffic, until we write the coverage-tests
        for (Table.Cell<String, String, Integer> cell : AvroJson.getMessages().cellSet()) {
            trafficlog.info(cell.getRowKey() + " " + cell.getColumnKey() + " " + cell.getValue());
        }
        String todir = event.getProject().getUserProperty("ctk.todir");
        log.debug("buildFinished for " + todir);
        // signal the listener to proceed
        result.complete(todir +"report/html/index.html");
    }

    /**
     * Signals that a target is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getTarget()
     */
    @Override
    public void targetStarted(BuildEvent event) {

    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    @Override
    public void targetFinished(BuildEvent event) {

    }

    /**
     * Signals that a task is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getTask()
     */
    @Override
    public void taskStarted(BuildEvent event) {

    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    @Override
    public void taskFinished(BuildEvent event) {
    }

    /**
     * Signals a message logging event.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getException()
     * @see BuildEvent#getPriority()
     */
    @Override
    public void messageLogged(BuildEvent event) {

    }
}
