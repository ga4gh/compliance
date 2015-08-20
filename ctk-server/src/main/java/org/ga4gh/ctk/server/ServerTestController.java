package org.ga4gh.ctk.server;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.ResultsSupport;
import org.ga4gh.ctk.TestRunner;
import org.ga4gh.ctk.config.Props;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Test controller used when running as a server.
 *
 * Created by Wayne Stidolph on 7/15/2015.
 */
@Controller
@RequestMapping("/servertest")
public class ServerTestController implements CtkLogs {

    @Autowired
    private TestRunner testrunner;

    public void setTestrunner(TestRunner testrunner) {
        this.testrunner = testrunner;
    }

    @Autowired
    public Props props;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView runTests(@RequestParam(value = "urlRoot", required = false) String urlRoot,
                                 @RequestParam(value = "matchstr", required = false) String mstr) {
        if (urlRoot == null)
            urlRoot = URLMAPPING.getInstance().getUrlRoot();
        if (mstr == null)
            mstr = props.ctk_matchstr;
        String resultsDir = ResultsSupport.getResultsDir(urlRoot);
        if (!resultsDir.isEmpty()) {
            // we have a place to put results
            log.info("about to run tests " + urlRoot + " " + mstr + " " + props.ctk_testjar);
            Future<String> futureTestResultIndexPage =
                    testrunner.doTestRun(urlRoot, props.ctk_tgt_dataset_id,
                                         mstr, props.ctk_testjar, resultsDir);
            String testResultIndexPage = null;
            try { // wait for results
                testResultIndexPage = futureTestResultIndexPage.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            log.info("test complete, results to " + testResultIndexPage);

            // TODO check for empty testResultsIndexPage, if so return error (need error page)
            // see http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/view/RedirectView.html
            return new ModelAndView(new RedirectView(testResultIndexPage,
                    true, // redirect is context-relative
                    false)); // don't bother staying http 1.0 compatible
        }
        return new ModelAndView("Couldn't build results dir for " + urlRoot);
    }
}