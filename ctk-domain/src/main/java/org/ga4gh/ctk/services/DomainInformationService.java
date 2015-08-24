package org.ga4gh.ctk.services;

import com.google.common.base.*;
import com.google.gson.*;
import org.ga4gh.ctk.utility.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

/**
 * Created by Wayne Stidolph on 8/2/2015.
 */

public class DomainInformationService {

    static Logger log = LoggerFactory.getLogger(DomainInformationService.class);
    private static DomainInformationService me = new DomainInformationService();

    private static boolean initialized = false;
    private static List<String> requestTypes = new LinkedList<>();
    private static List<String> responseTypes = new LinkedList<>();
    private static List<String> methods = new LinkedList<>();
    private static List<String> dataObjTypes = new LinkedList<>();
    private static List<String> otherTypes = new LinkedList<>();
    @Autowired
    Props props;
    public DomainInformationService() {
    }

    /**
     * Gets service with information extracted from file identified by
     * 'ctk.domaintypesfile' property.
     *
     * @return the service
     */
    public static DomainInformationService getService() {
        if (!initialized) {
            log.debug("getService finds service un-initialized");
            String fileContents = me.getContentsFromProperty();
            initialized = me.extractDomainTypes(fileContents);
        }
        return me;
    }

    /**
     * Gets service with information extracted from the JSON string
     * passed as argument. String is JSON array of strings, each string
     * is a domain class type:
     * <pre>
     *     [
     * "org\\ga4gh\\methods\\GAException.java",
     * "org\\ga4gh\\methods\\ListReferenceBasesRequest.java",
     * "org\\ga4gh\\methods\\ListReferenceBasesResponse.java",
     * ...
     * ]
     * </pre>
     *
     * @param fileContents the file contents (as would be in the domain types file)
     * @return the service
     */
    public static DomainInformationService getService(String fileContents) {
        if (!initialized) {
            log.debug("getService finds service un-initialized");
            initialized = me.extractDomainTypes(fileContents);
        }
        return me;
    }

    private static void addToMethodsList(String methodsClass) {
        // dig out the actual methods on the interface, add to 'methods'
        // until we do that, don't want to just stuff the methods interface
        // into this list - want it to alarmingly empty :)
        log.trace("addToMethodsList is unimplemented, does nothing with {}", methodsClass);
    }

    public void clearTypes() {
        requestTypes = new LinkedList<>();
        responseTypes = new LinkedList<>();
        methods = new LinkedList<>();
        dataObjTypes = new LinkedList<>();
        otherTypes = new LinkedList<>();
    }

    public List<String> getRequestTypes() {
        return requestTypes;
    }

    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public List<String> getDataObjType() {
        return dataObjTypes;
    }

    public List<String> getOtherTypes() {
        return otherTypes;
    }

    public List<String> getMethods() {
        return methods;
    }

    /**
     * Fetch the contents of the file identified by 'ctk.domaintypesfile'.
     *
     * @return the string
     */
    String getContentsFromProperty() {

        if (props != null) {
            log.debug("getContentsFromProperty sees props.ctk_domaintypesfile as {}", props.ctk_domaintypesfile);
            return Utils.readFile(props.ctk_domaintypesfile);
        } else {
            log.info("getContentsFromProperty has null 'props' so will look directly in Environment");
        }
        // in case we run without Spring ijection somehow, get directly from env
        String filename = Utils.getPropEnvValue("ctk.domaintypesfile");

        if (filename.isEmpty()) {
            log.warn("Never found good value for ctk.domaintypesfile");
            return "";
        }

        String fileContents = Utils.readFile(filename);
        return fileContents;
    }

    /**
     * Read in domain types file and parse to extract generated data
     * type (messages, types, methods). Types not fitting the convention
     * are considered "data object" types.
     * <p>
     * The domain types file content is auto-generated during build by the file-list
     * maven plugin , but the file can be hand-edited if you want to omit
     * or add coverage tracking for some types.
     */
    public boolean extractDomainTypes(String fileContents) {

        Gson gson = new Gson();
        String[] domaintypeStrings = gson.fromJson(fileContents, String[].class);

        if (domaintypeStrings == null) {
            log.warn("extractDomainTypes contents parsed to null, contents are: {}", fileContents);
            return false;
        } else {
            log.debug("extractDomainTypes gets {} entries from contents: {}", domaintypeStrings.length, fileContents);
        }

        for (String line : domaintypeStrings) {
            log.trace("extractDomainTypes processing " + line);

            // convert directory to dot, drop repeats, and drop .java suffix
            CharSequence dotted = CharMatcher.anyOf("/\\").replaceFrom(line, ".");
            String singledot = CharMatcher.is('.').collapseFrom(dotted, '.');
            int dotJavaIdx = singledot.lastIndexOf(".java");
            String typeStr = singledot.substring(0, dotJavaIdx);
            // so a typeStr looks like org.ga4gh.methods.SearchDatasetsResponse.java

            if (typeStr.startsWith("org.ga4gh.models.")) {
                dataObjTypes.add(typeStr);
                log.trace(typeStr + " add to dataObjTypes");
            } else if (typeStr.endsWith("Request")) {
                requestTypes.add(typeStr);
                log.trace(typeStr + " add to requestTypes");
            } else if (typeStr.endsWith("Response")) {
                responseTypes.add(typeStr);
                log.trace(typeStr + " add to responseTypes");
            } else if (typeStr.endsWith("Methods")) {
                addToMethodsList(typeStr);
                log.trace(typeStr + " add to methodTypes");
            } else {
                otherTypes.add(typeStr);
                log.trace(typeStr + " add to otherTypes");
            }
        }

        return true;
    }

    /**
     * <p>Extracts the "active" endpoints from a Map of string->endpoint.</p>
     * <p>"Active" is endpoint keys which have a non-null/empty value, and aren't
     * the base path of the target server. The CTK expects an "active" endpoint
     * to be used in some test.</p>
     * <p>The default enpoints in the URLMAPPING might have a value "reserved" but
     * if this is overriden by properties (e.g., in defaulttransport.properties
     * or on command line) to be blank, then the endpoint is "inactive"
     * meaning the CTK does not expect it to be used.</p>
     *
     * @param endpoints the endpoints to filter
     * @return the list of "active" enpoints, alpha-sorted
     */
    public List<String> getActiveEndpoints(Map<String, String> endpoints) {
        // Un-implemented endpoints can have a property, but
        // the actual endpoint *value* is set blank/null in the properties
        // which override the defaults
        endpoints.remove("ctk.tgt.urlRoot"); // maybe this shouldn't be stored in URMAPPING endpoints?

        Set<String> activeEndpoints = new HashSet<>();
        activeEndpoints.addAll(endpoints.values());
        activeEndpoints.remove(""); // "" value signals reserved-but-not-used
        return Utils.asSortedList(activeEndpoints);
    }

}
