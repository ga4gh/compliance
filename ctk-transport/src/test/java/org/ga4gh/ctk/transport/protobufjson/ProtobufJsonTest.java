package org.ga4gh.ctk.transport.protobufjson;

import com.google.protobuf.util.JsonFormat;
import ga4gh.ReadServiceOuterClass.*;
import ga4gh.VariantServiceOuterClass.*;
import org.ga4gh.ctk.transport.testcategories.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

/**
 * JsonMaker Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 3, 2015</pre>
 */
@Category(TransportTests.class)
public class ProtobufJsonTest {

    @Test
    public void ProtobufBuildsDefaultObject() throws Exception {
        SearchReadsRequest srr = SearchReadsRequest.newBuilder()
                .setStart(0L)
                .build();
        assertNotNull(srr);
    }

    /**
     * Method: avroToJson(DatumWriter dw, Schema schema, T srcBytes)
     * Note this test is specific to the Avro-using JSON serializer
     * so the "expected" JSON is in the Avro style, with JSON blocks
     * for non-null fields, like ... "start": { "long" : "0"}
     * <p>
     * This is fine for an Avro deserializer, but doesn't work with
     * default Jackson deserializing, so we'll have a distinct test
     * for Jackson.
     */
    @Test
    @Category(ProtobufJsonTests.class)
    public void ProtobufGeneratesJsonBytesForDefaultGA() throws Exception {
        SearchReadsRequest srr = SearchReadsRequest.newBuilder()
                .setStart(0L)
                .build();
        assertNotNull("test data is default GaSearchReadsRequest", srr);

        String actual = JsonFormat.printer().print(srr);

        String expected = "{\n}";
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    @Category(ProtobufJsonTests.class)
    public void ProtobufGeneratesSimpleJSONSearchVariantsRequest() throws Exception {

        SearchVariantsRequest svr = SearchVariantsRequest.newBuilder()
                .addAllCallSetIds(Arrays.asList("foo", "bar"))
                .setReferenceName("I.Am.The.Walrus")
                .setStart(500L)
                .setEnd(7654L)
                .setPageToken("snuffle.bunny")
                .setVariantSetId("great_variant_set_id")
                .build();
        String actual = JsonFormat.printer().print(svr);

        JSONAssert.assertEquals("{referenceName:I.Am.The.Walrus}", actual, false);
        JSONAssert.assertEquals("{callSetIds:[\"foo\", \"bar\"]}", actual, false);
        JSONAssert.assertEquals("{callSetIds:[\"bar\", \"foo\"]}", actual, false);
        JSONAssert.assertEquals("{variantSetId:\"great_variant_set_id\"}", actual, false);
    }

    @Test
    @Category(TransportTests.class)
    public void ProtobufGeneratesSimpleJSONSearchReadsRequest() throws Exception {
        SearchReadsRequest srr = SearchReadsRequest.newBuilder()
                .addAllReadGroupIds(Collections.<String>emptyList())
                .setStart(0L)
                .setPageSize(32)
                .setEnd(4321L)
                .build();
        String actual = JsonFormat.printer().print(srr);

        JSONAssert.assertEquals("{pageSize:32}", actual, false);
        JSONAssert.assertEquals("{end:\"4321\"}", actual, false);
    }

    /**
     * Protobuf notice mismatch obj schema.
     * <p>
     * Feed wrong builder to Protobuf, something should complain.
     * Probably different in different cases, but this shows one.
     *
     * @throws Exception the exception
     */
    @Test(expected = com.google.protobuf.InvalidProtocolBufferException.class)
    @Category(ProtobufJsonTests.class)
    public void ProtobufNoticeMismatchObjSchema() throws Exception {
        SearchReadsRequest srr = SearchReadsRequest.newBuilder()
                .setStart(1L)
                .build();
        String actual = JsonFormat.printer().print(srr);

        SearchVariantsResponse.Builder responseBuilder = SearchVariantsResponse.newBuilder();
        JsonFormat.parser().merge(actual, responseBuilder);
    }

}
