package org.ga4gh.cts.api.variants;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.*;
import org.ga4gh.ctk.*;
import org.ga4gh.ctk.transport.*;
import org.ga4gh.ctk.transport.protocols.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.junit.runner.*;

import java.net.HttpURLConnection;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * <p>
 *     Callsets-related integration tests.
 * </p>
 * Created by Wayne Stidolph on 6/11/2015.
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class CallsetsSearchResponseCheckIT implements CtkLogs {
    // implements CtkLogsprivate static org.slf4j.Logger log = getLogger(CallsetsSearchResponseCheckIT.class);

    static URLMAPPING urls = URLMAPPING.getInstance();
    private static final Client client = new Client(urls);

    /*
     REF MATERIAL from the  variantmethods IDL re GASearchCallSetRequest
     This request maps to the body of `POST /callsets/search` as JSON

     If specified, will restrict the query to call sets within the given variant sets.
     array<string> variantSetIds = [];

     Only return call sets for which a substring of the name matches this string.
     union { null, string } name = null;
     */



    @BeforeClass
    public static void setupTransport() throws Exception {
        //InetSocketAddress endpointAddress = new InetSocketAddress("127.0.0.1", 8000);
        // service = new SimpleOrderServiceEndpoint(endpointAddress);
        urls = URLMAPPING.getInstance(); // reload defaults
    }

    private static String makeUrl(String partialUrl) {
        return urls.getUrlRoot() + "/" + partialUrl;
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
     * Test that searches' verbs/methods work as expected.
     *
     * @param fullUrl the URL to test
     */
    private void testSearchRouting(final String fullUrl) throws UnirestException {
        // send some malformed requests and expect status == HTTP_BAD_REQUEST
        final String[] badJson = {"", "JSON", "<xml/>", "{", "}", "{\"bad:\"", "{]"};
        for (String datum : badJson) {
            assertThat(Unirest.post(fullUrl).header("Content-type", "application/json").
                    body(datum).asBinary().getStatus()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    /**
     * Test the status codes we're supposed to receive from the GET, POST, and OPTIONS methods on
     * <tt>/callsets/search</tt>.
     * <p>
     * <p>See server/tests/unit/test_views.py: testRouteCallsets</p>
     *
     * @throws Exception if there's a connection problem
     */
    @Test
    public void checkCallSetsRouting() throws Exception {
        String callsetsPartialUrl = urls.getSearchCallsets();

        testHttpMethods(makeUrl(callsetsPartialUrl));
    }

    /**
     * Test the status codes we're supposed to receive from the GET, POST, and OPTIONS methods on
     * <tt>/variants/search</tt>.
     * <p>
     * <p>See server/tests/unit/test_views.py: testRouteCallsets</p>
     *
     * @throws Exception if there's a connection problem
     */
    @Test
    public void checkVariantSearchMethods() throws Exception {
        String partialUrl = urls.getSearchVariants();

        testHttpMethods(makeUrl(partialUrl));
    }

    private String[] allSearchUrls() {
        return new String[] {
                makeUrl(urls.getSearchCallsets()),
                makeUrl(urls.getSearchReadGroupSets()),
                makeUrl(urls.getSearchReads()),
                makeUrl(urls.getSearchReferencesets()), // this fails (404 instead of 405)
                makeUrl(urls.getSearchVariants()),
                makeUrl(urls.getSearchVariantSets())
        };
    }

    /**
     * Test search routing and the handling of bad data.
     */
    @Test
    @Parameters(method = "allSearchUrls")
    public void checkSearchRouting(String fullUrl) throws UnirestException {
        testSearchRouting(fullUrl);
    }

}
