package org.ga4gh.ctk.server;

import org.ga4gh.ctk.*;
import org.ga4gh.ctk.config.*;
import org.ga4gh.ctk.transport.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;

import java.util.concurrent.*;

/**
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
                    testrunner.doTestRun(urlRoot, mstr, props.ctk_testjar, resultsDir);
            String testResultIndexPage = null;
            try { // wait for results
                testResultIndexPage = futureTestResultIndexPage.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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