package org.ga4gh.ctk.transport.avrojson;

import org.apache.avro.*;
import org.apache.avro.generic.*;
import org.apache.avro.io.*;
import org.ga4gh.*;
import org.ga4gh.ctk.transport.testcategories.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.skyscreamer.jsonassert.*;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * JsonMaker Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 3, 2015</pre>
 */
@Category(TransportTests.class)
public class JsonMakerTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: avroToJson(DatumWriter dw, Schema schema, T srcBytes)
     */
    //@Test
    // not really an Avro-specific test so no Category here
    public void NullAvroMeansEmptyJsonBytes() throws Exception {
        //JsonMaker av = new JsonMaker();
        Schema sampleSchema = GAReadGroup.SCHEMA$;
        DatumWriter dw = new GenericDatumWriter<>(sampleSchema);

        // this generates a log warning in JsonMaker, but not
        // an exception
        ByteArrayOutputStream rtn =
                JsonMaker.avroToJsonBytes(dw, sampleSchema, null);

        byte[] bytes = rtn.toByteArray();
        assertEquals(0, bytes.length);
    }


    //@Test
    public void AvroBuildsDefaultObject() throws Exception {
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setStart(0L)
                .build();
        assertNotNull(gsrr);
    }

    /**
     * Method: avroToJson(DatumWriter dw, Schema schema, T srcBytes)
     * Note this test is specific to the Avro-using JSON serializer
     * so the "expected" JSON is in the Avro style, with JSON blocks
     * for non-null fields, like ... "start": { "long" : "0"}
     *
     * This is fine for an Avro deserializer, but doesn't work with
     * default Jackson deserializing, so we'll have a distinct test
     * for Jackson.
     */
    //@Test
    @Category(AvroTests.class)
    public void AvroGeneratesJsonBytesForDefaultGA() throws Exception {
        //JsonMaker av = new JsonMaker();
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setStart(0L)
                .build();
        assertNotNull("test data is default GaSearchReadsRequest", gsrr);
        Schema sampleSchema = GASearchReadsRequest.SCHEMA$;
        DatumWriter dw = new GenericDatumWriter<>(sampleSchema);

        String actual =
                JsonMaker.avroToJsonBytes(dw, sampleSchema, gsrr).toString();

        String expected = "{\n" +
                "  \"readGroupIds\" : [ ],\n" +
                "  \"referenceName\" : null,\n" +
                "  \"referenceId\" : null,\n" +
                "  \"start\" : {\n" +
                "    \"long\" : 0\n" +
                "  },\n" +
                "  \"end\" : null,\n" +
                "  \"pageSize\" : null,\n" +
                "  \"pageToken\" : null\n" +
                "}";
        boolean strictCompare = true;
        JSONAssert.assertEquals(expected, actual, strictCompare);
    }

    @Category(AvroTests.class)
    public void JacksonGeneratesJsonBytesForDefaultGA() throws Exception {
        //JsonMaker av = new JsonMaker();
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setStart(0L)
                .build();
        assertNotNull("test data is default GaSearchReadsRequest", gsrr);
        Schema sampleSchema = GASearchReadsRequest.SCHEMA$;
        DatumWriter dw = new GenericDatumWriter<>(sampleSchema);

        String actual =
                JsonMaker.JacksonToJsonBytes(gsrr).toString();

        String expected = "{\n" +
                "  \"readGroupIds\" : [ ],\n" +
                "  \"referenceName\" : null,\n" +
                "  \"referenceId\" : null,\n" +
                "  \"start\" : 0,\n" +
                "  \"end\" : null,\n" +
                "  \"pageSize\" : null,\n" +
                "  \"pageToken\" : null\n" +
                "}";
        boolean strictCompare = true;
        JSONAssert.assertEquals(expected, actual, strictCompare);
    }

    @Test
    public void GsonGenerateSimpleJSONSearchVariantsRequest() throws Exception {

        GASearchVariantsRequest gsvr = GASearchVariantsRequest.newBuilder()
                .setCallSetIds(Arrays.asList("foo", "bar"))
                .setReferenceName("I.Am.The.Walrus")
                .setStart(500L)
                .setEnd(7654L)
                .setPageToken("snuffle.bunny")
                .setVariantName("garble")
                .build();
        String actual = JsonMaker.GsonToJsonBytes(gsvr);

        JSONAssert.assertEquals("{variantName:garble}", actual, false);
        JSONAssert.assertEquals("{referenceName:I.Am.The.Walrus}", actual, false);
        JSONAssert.assertEquals("{callSetIds:[\"foo\", \"bar\"]}", actual, false);
        JSONAssert.assertEquals("{callSetIds:[\"bar\", \"foo\"]}", actual, false);
    }

    @Test
    @Category(TransportTests.class)
    public void GsonGeneratesSimpleJSONSearchReadsRequest() throws Exception {
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setStart(0L)
                .setPageSize(32)
                .setEnd(4321L)
                .build();
        String actual = JsonMaker.GsonToJsonBytes(gsrr);

        JSONAssert.assertEquals("{start:0}", actual, false);
        JSONAssert.assertEquals("{pageSize:32}", actual, false);
        JSONAssert.assertEquals("{end:4321}", actual, false);
    }
    /**
     * Avro notice mismatch obj schema.
     *
     * Feed mismatched Schema to avro, something should complain!
     * Probably different in different cases, but this shows one.
     *
     * @throws Exception the exception
     */
    //@Test(expected = org.apache.avro.UnresolvedUnionException.class)
    @Category(AvroTests.class)
    public void AvroNoticeMismatchObjSchema() throws Exception {
        //JsonMaker av = new JsonMaker();
        GASearchReadsRequest gsrr = GASearchReadsRequest.newBuilder()
                .setStart(0L) // must set, even to default
                .build();
        Schema wrongSchema = GAExperiment.SCHEMA$;
        DatumWriter dw = new GenericDatumWriter<>(wrongSchema);

        // using the DatumWriter should trigger an exception
        String actual = JsonMaker.avroToJsonBytes(dw, wrongSchema, gsrr).toString();
    }

}
