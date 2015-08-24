package org.ga4gh.ctk;

import org.apache.tools.ant.*;
import org.ga4gh.ctk.domain.*;
import org.ga4gh.ctk.services.*;
import org.ga4gh.ctk.transport.*;
import org.ga4gh.ctk.transport.avrojson.*;
import org.ga4gh.ctk.utility.*;
import org.ga4gh.ctk.utility.ResultsSupport;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

import static org.slf4j.LoggerFactory.*;

/**
 * <p>This class runs the tests; it can be invoked from the command line runner,
 * or from a server-runner.</p>
 * Created by Wayne Stidolph on 7/11/2015.
 */
@Component
public class TestRunner implements BuildListener {
    private static Logger log = getLogger(TestRunner.class);
    private static Logger trafficlog = getLogger(CtkLogs.TRAFFICLOG);

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

    TrafficLogService trafficLogService;
    long runkey; // = trafficLogService.createTestRunKey();

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
        log.debug("matchStr: " + matchStr);
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
        trafficLogService = TrafficLogService.getService();
        urls.setUrlRoot(urlRoot);

        AvroJson.shouldDoComms = true; // always start from assumption of goodness!

        // is there a pattern we should enforce?
        acceptedTargetDir =
                ((null == toDir || toDir.isEmpty()) ? "target/" : toDir);
        // TODO ensure the toDir exists, create here

        runkey = trafficLogService.createTestRunKey();
        int DEFAULT_TESTMETHODKEY = -1;

                    /* ****** MAIN RUN-THE-TESTS *********** */

        result = new CompletableFuture<String>();
        boolean goodLaunch = antExecutor.executeAntTask(testJar,
                matchStr,
                urls,
                acceptedTargetDir,
                runkey,
                DEFAULT_TESTMETHODKEY, // TODO placeholder we get mechanism to create per-test method keys
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
        log.trace("{} got build event {}",this, event.toString());
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
        long reportedRunKey = Long.parseLong(event.getProject().getUserProperty("ctk.runkey"));
        if(reportedRunKey != runkey)
            log.warn("mismatched runKey detected; ant say " + reportedRunKey + " but TestRunner has " + runkey);
        List<TrafficLog> msgs = trafficLogService.getTrafficLogs(reportedRunKey);
        msgs.forEach((tlm) -> trafficlog.info(tlm.toString()));
        String todir = event.getProject().getUserProperty("ctk.todir");
        log.debug("buildFinished for " + todir);
        // signal the listener to proceed
        String results;
        if(todir.endsWith("/")){
            results = todir +"report/html/index.html";}
         else {
            results = todir +"/report/html/index.html";}
        result.complete(results);
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
        log.trace("{} got build event {}",this, event.toString());
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
        log.trace("{} got build event {}",this, event.toString());
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
        log.trace("{} got build event {}",this, event.toString());
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
        log.trace("{} got build event {}",this, event.toString());
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
        log.trace("{} got build event {}",this, event.toString());
    }
}
