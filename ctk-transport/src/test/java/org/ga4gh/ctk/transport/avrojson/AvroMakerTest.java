package org.ga4gh.ctk.transport.avrojson;

import junitparams.JUnitParamsRunner;
import org.apache.avro.generic.GenericDatumWriter;
import org.ga4gh.ctk.transport.testcategories.AvroTests;
import org.ga4gh.ctk.transport.testcategories.TransportTests;
import org.ga4gh.methods.SearchReadsResponse;
import org.ga4gh.methods.SearchReadsResponseAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

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

    SearchReadsResponse gsrr;
    ByteArrayOutputStream avroJson;
    String localJson = "{ \"alignments\" : [ ],\"nextPageToken\" : \"" + TOKEN + "\"}";


    // JSON without the type->value bodies for fields
    @Before
    public void before() throws Exception {
        gsrr = SearchReadsResponse.newBuilder()
                .setNextPageToken(TOKEN)
                .build();

        avroJson = JsonMaker.avroToJsonBytes(
                new GenericDatumWriter<SearchReadsResponse>(),
                SearchReadsResponse.SCHEMA$,
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
        SearchReadsResponse exemplar = new SearchReadsResponse();

        AvroMaker<SearchReadsResponse> av = new AvroMaker<>(exemplar);

        SearchReadsResponse deserializationResult =
                av.makeAvroFromJson(localJson, "test deserializing ");
        // do field-by-field compare here
        SearchReadsResponseAssert.assertThat(deserializationResult)
                .isNotNull()
                .hasNextPageToken(TOKEN);
    }


} 
