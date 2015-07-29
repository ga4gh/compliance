package org.ga4gh.cts.api;

import org.ga4gh.ctk.*;
import org.ga4gh.ctk.transport.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * <p>Stupidly named 'zz...' so it runs last, this test class will evaluate
 * integration test completeness (are all messages, data type,and endpoints exercised)</p>
 * <p>Created by Wayne Stidolph on 5/30/2015.</p>
 */
public class zzCheckCoverageIT implements CtkLogs {

    static URLMAPPING urls;

    @Ignore("Unimplemented")
    @Test
    public void allIdlMessagesShouldBeUsed() throws Exception {
        // plan is to attach the Tables of what-was-used to the
        // TestContext, then here to refer to that and make assertions
        //
        // So the setup phase of this test is to collect all the messages,
        // defined in the IDL

        assertEquals(1, 1);
    }

    @Ignore("Unimplemented")
    @Test
    public void allIdlDatatypesShouldBeUsed() throws Exception {
        // plan is to attach the Tables of what-was-used to the
        // TestContext, then here to refer to that and make assertions
    }

    @Ignore("Unimplemented")
    @Test
    public void allEndpointsShouldBeUsed() throws Exception {
        // plan is to attach the Tables of what-was-used to the
        // TestContext, then here to refer to that and make assertions

    }

    @BeforeClass
    public static void setupTransport() throws Exception {
        //InetSocketAddress endpointAddress = new InetSocketAddress("127.0.0.1", 8000);
        // service = new SimpleOrderServiceEndpoint(endpointAddress);
        urls = URLMAPPING.getInstance(); // reload defaults
        //client = new VariantsProtocolClient();
    }
}
