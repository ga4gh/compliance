package org.ga4gh.ctk.transport.avrojson;

import com.google.gson.*;
import org.apache.avro.*;
import org.apache.avro.specific.*;

import static org.slf4j.LoggerFactory.*;

/**
 * Deserialize GA4GH JSON to JVM object defined by Avro IDL.
 * Created by Wayne Stidolph on 6/3/2015.
 */
public class AvroMaker<T extends SpecificRecordBase> {

    private static org.slf4j.Logger log = getLogger(AvroMaker.class);

    private Class avroClass; // class for the avroObj examplar
    private T avroObj; // dummy object, so reflection can get properties

    public AvroMaker(T examplar) {
        avroObj = examplar;
        avroClass = avroObj.getClass();
    }

    /**
     * Make avro from json.
     *
     * @param json         the json
     * @param sourceForLog the source of the json, for log message
     * @return the generic container
     */
    public T makeAvroFromJson(String json, String sourceForLog) {

        T response = null;
        response = gsonToObjectRelaxed(json);

        if (response == null) {
            log.info("makeAvroFromResponse returns null instead of requested " + avroClass.getName()
                            + " from " + sourceForLog + " for json < " + json + " >"
            );
        }
        return response;
    }


    private T gsonToObjectRelaxed(String theJson){
        Gson gson = new Gson();
        T tgt = (T) gson.fromJson(theJson, avroClass);
        log.debug("generating a "+ avroClass.getName() + " from <" + theJson + "> yields " + tgt.toString());
        return tgt;
    }

    private Schema getSchema() {
        return avroObj.getSchema();
    }

}
