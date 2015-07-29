package org.ga4gh.cts.api.variants;

import org.ga4gh.ctk.*;
import org.ga4gh.ctk.transport.*;
import org.ga4gh.ctk.transport.protocols.*;
import org.junit.*;
import org.junit.experimental.categories.*;

/**
 * <p>Verify the paging behavior of the /variants endpoint</p>
 * <p>Created by Wayne Stidolph on 6/11/2015.</p>
 */
@Category(VariantsTests.class)
public class VariantsMethodsPagingChecksIT implements CtkLogs {
    // private static org.slf4j.Logger log = getLogger(VariantsMethodsPagingChecksIT.class);

    private static Client client;

    /**
     * <p>Page through big variants response.</p>
     *
     * <p>  The continuation token, which is used to page through large result sets.
     To get the next page of results, set this parameter to the value of
     `nextPageToken` from the previous response.</p>
     *
     * @throws Exception the exception
     */
    @Ignore
    @Test
    public void pageThroughBigVariantsResponse() throws Exception {
        //TODO write the test :)
    }

    @BeforeClass
    public static void setupTransport() throws Exception {
        client = new Client(URLMAPPING.getInstance());

    }
}
