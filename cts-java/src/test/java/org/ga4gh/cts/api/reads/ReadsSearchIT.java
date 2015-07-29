package org.ga4gh.cts.api.reads;

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
 * <p>Verify data returned from reads/search queries
 * meet expectations.</p>
 *
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */
@Category(ReadsTests.class)
@RunWith(JUnitParamsRunner.class)
public class ReadsSearchIT implements CtkLogs {
    // private static org.slf4j.Logger log = getLogger(ReadsSearchIT.class);

    private static Client client;


    @BeforeClass
    public static void setupTransport() throws Exception {
        //InetSocketAddress endpointAddress = new InetSocketAddress("127.0.0.1", 8000);
        // service = new SimpleOrderServiceEndpoint(endpointAddress);
        client = new Client(URLMAPPING.getInstance());

        //client.start(); start binary transceiver to Server Under Test
    }

    @AfterClass
    public static void shutdownTransport() throws Exception {

    }

    /**
     * <p>Verify alignedSequences match pattern.</p>
     * <p>In any ReadsTests response, the alignedSequence field can only contain [ACTGN]+.
     * No spaces, no other letters, no lowercase, no null. This is dataset specific
     * at this point, but we might be able to extend it to all datasets later - Jeltje email</p>
     * @param rgid the readgroup ID
    */
    @Test
    @Parameters({
            "VALID_READGROUPID"
    })
    // We pass in a key to look up the readgroupId, rather than the readgroupId itself,
    // so the TAP framework can make a valid filename out of the parameter string
    public void readsResponseMatchesACTGNPattern(String rgid) throws Exception {
        String replacedRgid = rgidMap.get(rgid);
        // do a readsearch
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setReadGroupIds(Collections.singletonList(replacedRgid))
                .build();
        GASearchReadsResponse grtn = client.reads.searchReads(gsrr);

        // the readmethods idl says:
        // record GASearchReadsResponse { array<GAReadAlignment> alignments = []; ...
        // so at the least we should get back an empty array

        assertThat(grtn.getAlignments()).isNotNull();

            // GASearchReadsResponse
        //    array<GAReadAlignment> alignments = [];
        //       GAReadAlignment field alignedSequence is null || string

        for (GAReadAlignment gar : grtn.getAlignments()) {
            assertThat(gar.getAlignedSequence()).isNotNull()
                    .matches("[ACTGN]+");
        }
    }

    private static Map<String, String> rgidMap;
    static {
        rgidMap = new HashMap<>();
        rgidMap.put("VALID_READGROUPID","low-coverage:HG00533.mapped.ILLUMINA.bwa.CHS.low_coverage.20120522");
    }

}
