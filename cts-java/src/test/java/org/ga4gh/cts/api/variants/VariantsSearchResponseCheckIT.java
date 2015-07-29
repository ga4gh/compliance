package org.ga4gh.cts.api.variants;

import junitparams.*;
import org.ga4gh.*;
import org.ga4gh.ctk.*;
import org.ga4gh.ctk.transport.*;
import org.ga4gh.ctk.transport.protocols.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.junit.runner.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>Test the data returned in a GASearchVariantsResponse is as expected.</p>
 * <p>These are parametrized tests, so we need the JUnitParamsRunner, and (as of mid-2015)
 * this Runner isn't supporting JUnit4 Rules, so we have to handle things like
 * closing a SoftAssertion manually.</p>
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */
@RunWith(JUnitParamsRunner.class)
@Category(VariantsTests.class)
public class VariantsSearchResponseCheckIT implements CtkLogs {
    // private static org.slf4j.Logger log = getLogger(VariantsSearchResponseCheckIT.class);

    private static Client client;

    /**
     * Method: searchVariants(GASearchVariantsRequest request)
     * @param vsetIds the vset ids (mapped in by Parameters annotation)
     * @param refName the ref name (mapped in by Parameters annotation)
     * @param start the start (mapped in by Parameters annotation)
     * @param end the end (mapped in by Parameters annotation)
     * @param expLength the exp length (mapped in by Parameters annotation)
     * @throws Exception the exception
     */
    @Test
    @Parameters({
            // "In the testdataset 1kg-phase1, a query for all variants on chr22
            // between coordinates 16050408 and 16052159 should have exactly 16 results" -- Jeltje
            //
            "1kg-phase1, 22, 16050408, 16052159, 16"
    })
    public void searchVariantsRequestResultSizeAsExpected(String vsetIds, String refName, long start, long end, int expLength) throws Exception {
        GASearchVariantsRequest request = GASearchVariantsRequest.newBuilder()
                // I ‘split’ the vsetIds param, that way if we’re given
                // multiple variantsetIds, we can just join them with semicolons in the
                // parameters list before the first comma and then the split sections
                // become individual (multiple) variantSetIds entries in the array
                .setVariantSetIds(Arrays.asList(vsetIds.split(";")))
                .setReferenceName(refName)
                .setStart(start)
                .setEnd(end)
                .build();

        GASearchVariantsResponse response = client.variants.searchVariants(request);

        assertThat(response.getVariants()).hasSize(expLength);
    }


    @BeforeClass
    public static void setupTransport() throws Exception {
        //InetSocketAddress endpointAddress = new InetSocketAddress("127.0.0.1", 8000);
        // service = new SimpleOrderServiceEndpoint(endpointAddress);
        client = new Client(URLMAPPING.getInstance());
    }
}
