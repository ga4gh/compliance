package org.ga4gh.cts.core;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import junitparams.JUnitParamsRunner;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.testcategories.CoreTests;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test fetches the target server landing page as evidence of connectivity.
 * It also scrapes the supplied HTML as fodder for the eventual report.
 *
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */
@Category(CoreTests.class)
@RunWith(JUnitParamsRunner.class)
public class LandingPageIT implements CtkLogs {

    /**
     * <p>The landing page should exist.</p>
     * <p>This test verifies the environmental "urlRoot" is a valid URL
     * and then tries to fetch the page at that root. A response of '200' and
     * a non-empty body are required to pass the test.</p>
     * <p>See server/tests/unit/test_views.py: testRouteIndex</p>
     * <p>See server/tests/unit/test_views.py: testRouteIndexRedirect</p>
     *
     * @throws Exception the exception
     */
    @Test
    public void landingPageIsProper() throws Exception {
        final String urlStrWithVersion = URLMAPPING.getInstance().getUrlRoot();

        URL urlWithVersion = new URL(urlStrWithVersion);
        URL urlWithoutVersion =
                new URL(urlWithVersion.getProtocol(), urlWithVersion.getHost(), urlWithVersion.getPort(), "");

        String unversionedUrl = urlWithVersion.getProtocol() + "://" + urlWithVersion.getAuthority();

        checkLandingPage(urlStrWithVersion);
        checkLandingPage(unversionedUrl);
    }

    private void checkLandingPage(String url) throws UnirestException {
        log.debug("Fetching " + url);
        GetRequest request = Unirest.get(url);
        HttpResponse<String> response = request.asString();

        // it must be text/html
        final Headers headers = response.getHeaders();
        assertThat(headers).containsKey("content-type");
        List<String> values = headers.get("content-type");
        assertThat(values).hasSize(1);
        assertThat(values.get(0)).startsWith("text/html");

        // it must have a non-empty body
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().length()).isGreaterThan(0);
        assertThat(response.getStatus()).isEqualTo(200);
    }



    /**
     * <p>Show that tests can fail.</p>
     * <p>By querying for a system property "cts.demofail" this
     * test shows that tests can fail. This optional failure thus demonstrates the CTK
     * capabilities of linking from the generated Surefire report directly
     * to the failing line of test code, and to the test-specific javadoc.</p>
     *
     * @throws Exception the exception
     */
    @Test
    public void propertyCanCauseTestFail() throws Exception {

        if(Boolean.getBoolean("cts.demofail")) {
            testlog.warn("Dummying failure because cts.demofail is true");
            assertThat(false).isTrue();
        }
        else
            assertThat(false).isFalse();
    }
}
