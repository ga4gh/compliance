package org.ga4gh.cts.api.variants;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.TransportUtils;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.SearchCallSetsRequest;
import org.ga4gh.methods.SearchCallSetsResponse;
import org.ga4gh.methods.SearchVariantSetsRequest;
import org.ga4gh.methods.SearchVariantSetsResponse;
import org.ga4gh.models.CallSet;
import org.ga4gh.models.VariantSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Callsets-related integration tests.
 * </p>
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class CallsetsSearchResponseCheckIT implements CtkLogs {

    private static final URLMAPPING urls = URLMAPPING.getInstance();

    private static Client client = new Client(urls);

    private static String makeUrl(String partialUrl) {
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

        testHttpMethods(makeUrl(callsetsPartialUrl));
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
     *
     * @param fullUrl the URL to test (supplied by {@link #allSearchUrls()}
     * @throws UnirestException if there's a communication problem
     */
    @Test
    @Parameters(method = "allSearchUrls")
    public void checkSearchRouting(String fullUrl) throws UnirestException {
        testSearchRouting(fullUrl);
    }

    /**
     * Search call sets.  Fetches call sets from the specified dataset.
     * <ul>
     * <li>Query 1: <pre>/variantsets/search datasetIds: (passed in)</pre></li>
     * <li>Test 1: assert that we received a {@link SearchVariantSetsResponse} containing an
     * array of {@link VariantSet} objects.  For each of the VariantSet objects, grab the
     * <pre>id</pre> and pass it to....</li>
     * <li>Query 2: <pre>/callsets/search variantSetIds: id</pre></li>
     * <li>Test 2: assert that the returned object is a {@link SearchCallSetsResponse}, and
     * that it contains &gt; 0 {@link CallSet} objects. We can check that the call sets have
     * distinct ID values.</li>
     * </ul>
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void searchForExpectedCallSets() throws AvroRemoteException {
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSets()).isNotEmpty();
        for (VariantSet set : vResp.getVariantSets()) {
            final String id = set.getId();

            final SearchCallSetsRequest csReq =
                    SearchCallSetsRequest.newBuilder()
                                         .setVariantSetId(id)
                                         .build();
            final SearchCallSetsResponse csResp = client.variants.searchCallSets(csReq);

            assertThat(csResp.getCallSets()).isNotEmpty();
        }
    }

    /**
     * Test getting a call set with a valid ID.
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void getCallSetWithValidIDShouldSucceed() throws AvroRemoteException {
        final SearchVariantSetsRequest vReq =
                SearchVariantSetsRequest.newBuilder()
                                        .setDatasetId(TestData.getDatasetId())
                                        .build();
        final SearchVariantSetsResponse vResp = client.variants.searchVariantSets(vReq);

        assertThat(vResp.getVariantSets()).isNotEmpty();

        // grab the first VariantSet and use it as source of ReadSets
        final VariantSet variantSet = vResp.getVariantSets().get(0);
        final String variantSetId = variantSet.getId();

        final SearchCallSetsRequest callSetsSearchRequest =
                SearchCallSetsRequest.newBuilder()
                                     .setVariantSetId(variantSetId)
                                     .build();
        final SearchCallSetsResponse csResp = client.variants.searchCallSets(callSetsSearchRequest);

        // grab one of the CallSets returned from the search
        assertThat(csResp.getCallSets()).isNotEmpty();
        final CallSet callSetFromSearch = csResp.getCallSets().get(0);
        final String callSetId = callSetFromSearch.getId();
        assertThat(callSetId).isNotNull();

        // fetch the CallSet with that ID and compare with the one from the search
        final CallSet callSetFromGet = client.variants.getCallSet(callSetId);
        assertThat(callSetFromGet).isNotNull();
        assertThat(callSetFromGet.getId()).isEqualTo(callSetId);
        assertThat(callSetFromGet).isEqualTo(callSetFromSearch);
    }

    /**
     * Test getting a call set with an invalid ID.
     * @throws AvroRemoteException if there's a communication problem
     */
    @Test
    public void getCallSetWithInvalidIDShouldFail() throws AvroRemoteException {
        final String nonexistentCallSetId = Utils.randomId();

        // fetch the CallSet with that ID
        final CallSet callSetFromGet = client.variants.getCallSet(nonexistentCallSetId);
        assertThat(callSetFromGet).isNull();
    }


}
