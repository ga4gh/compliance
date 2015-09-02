package org.ga4gh.cts.api.endpoints;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.ga4gh.ctk.transport.TransportUtils;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Tests for the properties of the raw server endpoints.
 *
 * @author Herb Jellinek
 */
@RunWith(JUnitParamsRunner.class)
@Category(EndpointsTests.class)
public class RawEndpointsIT {

    private static final URLMAPPING urls = URLMAPPING.getInstance();

    /**
     * Given a partial URL, use {@link TransportUtils#makeUrl(String, String)} to combine it with
     * the root URL and return the result.
     * @param partialUrl a partial URL
     * @return a full URL including the root URL
     */
    private static String makeFullUrl(String partialUrl) {
        return TransportUtils.makeUrl(urls.getUrlRoot(), partialUrl);
    }

    /**
     * Test that the basic server verbs/methods work as expected.
     *
     * @param fullUrl the URL to test
     */
    private void testHttpMethods(String fullUrl) throws UnirestException {
        assertThat(Unirest.get(fullUrl).asBinary().getStatus()).isEqualTo(HttpURLConnection.HTTP_BAD_METHOD);
        assertThat(Unirest.options(fullUrl).asBinary().getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(Unirest.post(fullUrl).asBinary().getStatus()).isEqualTo(HttpURLConnection.HTTP_UNSUPPORTED_TYPE);
    }

    /**
     * Test the status codes we're supposed to receive from the GET, POST, and OPTIONS methods on
     * <tt>/callsets/search</tt>.
     *
     * <p>See server/tests/unit/test_views.py: testRouteCallsets</p>
     *
     * @throws Exception if there's a connection problem
     */
    @Test
    public void checkCallSetsRouting() throws Exception {
        String callsetsPartialUrl = urls.getSearchCallsets();

        testHttpMethods(makeFullUrl(callsetsPartialUrl));
    }

    /**
     * Test the status codes we're supposed to receive from the GET, POST, and OPTIONS methods on
     * <tt>/variants/search</tt>.
     *
     * <p>See server/tests/unit/test_views.py: testRouteCallsets</p>
     *
     * @throws Exception if there's a connection problem
     */
    @Test
    public void checkVariantSearchMethods() throws Exception {
        String partialUrl = urls.getSearchVariants();

        testHttpMethods(makeFullUrl(partialUrl));
    }

    /**
     * Test that searches' verbs/methods work as expected.
     *
     * @param fullUrl the URL to test
     */
    private void testSearchRouting(final String fullUrl) throws UnirestException {
        // send some malformed requests and expect status == HTTP_BAD_REQUEST
        final String[] badJson = {"", "JSON", "<xml/>", "{", "}", "{\"bad:\"", "{]"};
        for (String datum : badJson) {
            assertThat(Unirest.post(fullUrl)
                              .header("Content-type", "application/json")
                              .body(datum)
                              .asBinary()
                              .getStatus()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    /**
     * Input data for {@link #checkSearchRouting(String)}.
     * @return  an array of URLs
     */
    private String[] allSearchUrls() {
        return new String[] {
                makeFullUrl(urls.getSearchCallsets()),
                makeFullUrl(urls.getSearchReadGroupSets()),
                makeFullUrl(urls.getSearchReads()),
                makeFullUrl(urls.getSearchReferencesets()), // this fails (404 instead of 405)
                makeFullUrl(urls.getSearchVariants()),
                makeFullUrl(urls.getSearchVariantSets())
        };
    }

    /**
     * Test search routing and the handling of bad data.
     *
     * @param fullUrl the URL to test (supplied by {@link #allSearchUrls()}
     * @throws UnirestException if there's a communication problem
     */
    @Test
    @Parameters(method = "allSearchUrls")
    public void checkSearchRouting(String fullUrl) throws UnirestException {
        testSearchRouting(fullUrl);
    }

}
