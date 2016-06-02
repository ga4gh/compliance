package org.ga4gh.cts.api.endpoints;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.ga4gh.ctk.transport.TransportUtils;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
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
     * Input data for parameterized tests that require a search URL as argument.
     * <p>
     * Note that this list doesn't include {@link URLMAPPING#getSearchReferenceBases()},
     * which is a <tt>GET</tt>, not <tt>POST</tt>, method.
     * </p>
     * @return an array of all search URLs that accept POST
     */
    private String[] allSearchUrlsThatAcceptPost() {
        return new String[] {
                makeFullUrl(urls.getSearchCallSets()),
                makeFullUrl(urls.getSearchDataSets()),
                makeFullUrl(urls.getSearchReadGroupSets()),
                makeFullUrl(urls.getSearchReads()),
                makeFullUrl(urls.getSearchReferences()),
                /*
                makeFullUrl(urls.getSearchReferenceBases()), // uses GET, not POST!
                */
                makeFullUrl(urls.getSearchReferenceSets()),
                makeFullUrl(urls.getSearchVariants()),
                makeFullUrl(urls.getSearchVariantSets())
        };
    }

    /**
     * Test that searches' verbs/methods work as expected.  Because our usual {@link Client} object
     * only creates well-formed requests, we use Unirest, a simple REST client API,
     * to connect to the server.
     *
     * @param fullUrl the URL to test
     */
    private void tryMalformedSearchPayloads(final String fullUrl) throws UnirestException {
        // send some malformed requests and expect status == HTTP_BAD_REQUEST
        final String[] requestBodies = {"", "JSON", "<xml/>", "{", "}", "{\"bad:\"", "{]"};
        for (String datum : requestBodies) {
            assertThat(Unirest.post(fullUrl)
                              .header("Content-type", "application/json")
                              .body(datum)
                              .asBinary() // make the request; we don't really care about the format
                              .getStatus()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    /**
     * Test search routing and the handling of bad data.
     *
     * @param fullUrl the URL to test (supplied by {@link #allSearchUrlsThatAcceptPost()})
     * @throws UnirestException if there's a communication problem
     */
    @Test
    @Parameters(method = "allSearchUrlsThatAcceptPost")
    public void testMalformedSearchPayloads(final String fullUrl) throws UnirestException {
        tryMalformedSearchPayloads(fullUrl);
    }

}
