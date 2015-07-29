package org.ga4gh.ctk.transport.avrojson;

import junitparams.*;
import org.apache.avro.generic.*;
import org.ga4gh.*;
import org.ga4gh.ctk.transport.testcategories.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.junit.runner.*;

import java.io.*;

/**
 * AvroMaker Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 3, 2015</pre>
 */
@RunWith(JUnitParamsRunner.class)
@Category({TransportTests.class, AvroTests.class})
public class AvroMakerTest {

    long startVal = 4321L;
    int pageSizeVal = 33;
    String TOKEN = "I_am_a_test";

    GASearchReadsResponse gsrr;
    ByteArrayOutputStream avroJson;
    String localJson = "{ \"alignments\" : [ ],\"nextPageToken\" : \"" + TOKEN + "\"}";


    // JSON without the type->value bodies for fields
    @Before
    public void before() throws Exception {
        gsrr = GASearchReadsResponse.newBuilder()
                .setNextPageToken(TOKEN)
                .build();

        avroJson = JsonMaker.avroToJsonBytes(
                new GenericDatumWriter<GASearchReadsResponse>(),
                GASearchReadsResponse.SCHEMA$,
                gsrr);
    }

    @After
    public void after() throws Exception { }

    /**
     * Demonstrate deserialize JSON built the "GA4GH way" - no type info,
     * undefined ordering of fields in each JSON block
     * @throws Exception
     */
    @Test
    public void testMakeAvroFromJson() throws Exception {
        GASearchReadsResponse examplar = new GASearchReadsResponse();

        AvroMaker<GASearchReadsResponse> av = new AvroMaker<>(examplar);

        GASearchReadsResponse deserializationResult =
                av.makeAvroFromJson(localJson.toString(),
                        "test deserializing ");
        // do field-by-field compare here
        GASearchReadsResponseAssert.assertThat(deserializationResult)
                .isNotNull()
                .hasNextPageToken(TOKEN);
    }


} 
