package org.ga4gh.cts.api.variants;

import org.assertj.core.api.JUnitSoftAssertions;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.RespCode;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.methods.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>Verifies basic reachability of the Server's endpoints for variants, variantsets, and
 * callsets.</p>
 * <p>Tries default or basic search/get as appropriate and ensure the response is parseable
 * of the type expected according to the IDL.</p>
 * <p>The {@code VARIANTS} API methods (as defined in {@code variantmethods.avdl}) are:</p>
 * <ul>
 * <li>POST /variants/search SearchVariantsRequest yields SearchVariantsResponse [{@code
 * searchVariants()}]</li>
 * <li>POST /variantsets/search SearchVariantSetsRequest yields SearchVariantSetsResponse
 * [{@code searchVariantSets()}]</li>
 * <li>POST /callsets/search SearchCallSetsRequest yields SearchCallSetsResponse [{@code
 * searchCallSets()}]</li>
 * </ul>
 *
 * <p>The test invokes a search request with null, default, and error parameters
 * on the endpoint and verifies the response. For tests with more insight into
 * the data returned (complex queries, etc) refer to the VariantsSearchingIT tests.</p>
 *
 * <p>Note this is run with the default JUnit runner, which means we can (and do) use
 * a JUnit4 @Rule to set up a JUnitSoftAssertions, which lets us make multiple
 * asserts and have them all automatically checked and group-reported at the end of each test.</p>
 */
@Category(VariantsTests.class)
public class VariantsMethodsEndpointAliveIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    /**
     * Method: searchVariantSets(SearchVariantSetsRequest request)
     *
     * @throws Exception if something goes wrong
     */
    //@Ignore
    @Test
    public void testSearchVariantSetsRequestEndpointAlive() throws Exception {
        SearchVariantSetsRequest svsr =
                SearchVariantSetsRequest.newBuilder().build();

        SearchVariantSetsResponse response = client.variants.searchVariantSets(svsr);
        assertThat(response).isNotNull();
    }

    /**
     * Method: searchVariantSets(SearchVariantSetsRequest request, WireTracker wd)
     *
     * @throws Exception if something goes wrong
     */
    //@Ignore
    @Test
    public void testSearchVariantSetsForRequestWdEndpointAlive() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: searchVariants(SearchVariantsRequest request)
     *
     * @throws Exception if something goes wrong
     */
    @Test
    public void testSearchVariantsRequestEndpointAlive() throws Exception {
        SearchVariantsRequest request =
                SearchVariantsRequest.newBuilder()
                                     .setReferenceName("foo")
                                     .setStart(0L)
                                     .setEnd(1L)
                                     .build();

        SearchVariantsResponse response = client.variants.searchVariants(request);

        assertThat(response).isNotNull();
    }

    /**
     * Method: searchVariants(SearchVariantsRequest request, WireTracker wd)
     *
     * @throws Exception if something goes wrong
     */
    //@Ignore
    @Test
    public void testSearchVariantsForRequestWdEndpointAlive() throws Exception {
        SearchVariantsRequest request =
                SearchVariantsRequest.newBuilder()
                                     .setReferenceName("foo")
                                     .setStart(0L)
                                     .setEnd(1L)
                                     .build();

        WireTracker mywt = new WireTracker();
        SearchVariantsResponse response = client.variants.searchVariants(request, mywt);

        // the JUnit4 Rule creates a SoftAssertion and we can do multiple asserts cleanly!
        assertThat(mywt.getResponseStatus()).isEqualTo(RespCode.NOT_IMPLEMENTED);
        assertThat(response).isNotNull();
    }

    /**
     * Method: searchCallSets(SearchCallSetsRequest request)
     *
     * @throws Exception if something goes wrong
     */
    @Test
    public void testSearchCallSetsRequestEndpointAlive() throws Exception {
        SearchCallSetsRequest scsr = SearchCallSetsRequest.newBuilder()
                                                          .build();

        SearchCallSetsResponse response = client.variants.searchCallSets(scsr);

        assertThat(response).isNotNull();
    }

    /**
     * Method: searchCallSets(SearchCallSetsRequest request)
     *
     * @throws Exception if something goes wrong
     */
    @Test
    public void testSearchCallSetsRequestWDEndpointAlive() throws Exception {
        SearchCallSetsRequest scsr = SearchCallSetsRequest.newBuilder()
                                                          .build();

        WireTracker mywt = new WireTracker();
        SearchCallSetsResponse response = client.variants.searchCallSets(scsr, mywt);

        assertThat(response).isNotNull();
        assertThat(mywt.getResponseStatus()).isEqualTo(RespCode.NOT_IMPLEMENTED);
    }

}
