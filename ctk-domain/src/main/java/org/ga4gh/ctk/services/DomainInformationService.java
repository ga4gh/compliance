package org.ga4gh.ctk.services;

import com.google.common.base.*;
import com.google.gson.*;
import org.ga4gh.ctk.utility.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * <p>Provide information about the domain of interactions with the GA4GH Server (e.g.,
 * data types, transport endpoints, etc).</p>
 *
 * <p>The DomainInformationService (DIS) is initialized with a list of the generated data
 * elements coming from Schema compilation during the CTK build process. The DIS sorts this
 * data into request types, reponse types, methods, data object types and "other" (e.g.,
 * the GAExeption). This data is then available to tests to use for comparing against actual
 * object sent/recieved.</p>
 *
 * <p>The DIS initializes by reading the file identified by the property "ctk.domaintypesfile"
 * which is expected to be a JSON array of strings, one data type for each string. The DIS assumes
 * that data items:</P
 * <ul>
 *     <li>packaged in the org.ga4.gh.models package are data objects</li>
 *     <li>whose java class name ends in "Request" are Request objects</li>
 *     <li>whose java class name ends in "Response" are Response objects</li>
 *     <li>whose java class name ends in "Methods" are Method interface (currently ignored)</li>
 *     <li>not fitting above categories are "other"</li>
 * </ul>

 *
 * Created by Wayne Stidolph on 8/2/2015.
 */

public class DomainInformationService {

    static Logger log = LoggerFactory.getLogger(DomainInformationService.class);
    private static DomainInformationService me = new DomainInformationService();

    private static boolean initialized = false;
    private static List<String> requestTypes = new LinkedList<>();
    private static List<String> responseTypes = new LinkedList<>();
    private static List<String> methodTypes = new LinkedList<>();
    private static List<String> methodnames = new LinkedList<>();
    private static List<String> methodRtn = new ArrayList<>();
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
            initialized = me.populateDomainTypes(fileContents);
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
            initialized = me.populateDomainTypes(fileContents);
        }
        return me;
    }

    /**
     * Clear the types lists.
     *
     * Test-support function for testing this service; reset the lists of
     *  domain information in prep for re-reading different domain info files.
     */
    public void clearTypes() {
        requestTypes = new LinkedList<>();
        responseTypes = new LinkedList<>();
        methodTypes = new LinkedList<>();
        methodnames = new LinkedList<>();
        methodRtn = new LinkedList<>();
        dataObjTypes = new LinkedList<>();
        otherTypes = new LinkedList<>();
    }

    /**
     * Gets "Request" types.
     *
     * @return the Request-named IDL types
     */
    public List<String> getRequestTypes() {
        return new LinkedList<String>(requestTypes);
    }

    /**
     * Gets list of "Response" types.
     *
     * @return the Response-named IDL types
     */
    public List<String> getResponseTypes() {
        return new LinkedList<String>(responseTypes);
    }

    /**
     * Gets types defined in org.ga4gh.models package.
     *
     * @return the data obj type
     */
    public List<String> getDataObjType() {
        return new LinkedList<String>(dataObjTypes);
    }

    /**
     * Gets "other" types (not Request, Response,method, nor model).
     *
     * @return the "other" types
     */
    public List<String> getOtherTypes() {
        return new LinkedList<String>(otherTypes);
    }

    /**
     * <p>Return list of names of Method interfaces.</>
     *
     * @return the methods
     */
    public List<String> getMethodTypes() {
        return new LinkedList<String>(methodTypes);
    }

    private static void addToMethodRtnsList(String methodsClass) {
        // dig out the method return types

        try {
            Class clazz = Class.forName(methodsClass);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods){
                methodnames.add(method.getName());
                methodRtn.add(method.getReturnType().getName());
            }
        } catch (ClassNotFoundException e) {
           log.warn("addToMethodRtnsList could not find class {}", methodsClass);
        }
    }

    void addToMethodTypes(String methodClassName){
        methodTypes.add(methodClassName);
        addToMethodRtnsList(methodClassName);
    }

    /**
     * <p>Fetch the contents of the file identified by 'ctk.domaintypesfile'.</p>
     * <p>Looks on the 'Props' object for teh property but that object might not be
     * present(if not running under Spring) so this method has a fallback to look for the
     * property value directly on the java System Properties and the enclosing Environment.</p>
     * <p>The property usually identifies the domain types file which was auto-generated
     * during build (by the file-list maven plugin) but the file can be hand-edited or a different
     * file identified if you want to omit or add coverage tracking for some types.</p>
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
     * <p>Populate domain types information from a string. String is often the contents
     * of the file identified by the "ctk.domaintypesfile" property. String is a JSON
     * array of strings, each string identifying a GA4GH IDL domain data type
     * (a request, response, data types, a methods interface or "other").</p>
     * <p>The strings are each a fully qualified java class path, with the directory
     * separator escaped:</p>
     * <pre>
     *     <code>
[
     "org\\ga4gh\\methods\\GAException.java",
     "org\\ga4gh\\methods\\ListReferenceBasesRequest.java",
     "org\\ga4gh\\methods\\ListReferenceBasesResponse.java",
     ...
     *     </code>
     * </pre>
     * <p>Use log level 'debug' to get summary parsing information; use 'trace'
     * to get line-by-line information.</p>
     *
     * @param jsonArrOfStrClassnames the json array of strings of class path-names
     * @return true IFF 'gson' extracted a non-null array to process from the param
     *
     */
    public boolean populateDomainTypes(String jsonArrOfStrClassnames) {

        Gson gson = new Gson();
        String[] domaintypeStrings = gson.fromJson(jsonArrOfStrClassnames, String[].class);

        if (domaintypeStrings == null) {
            log.warn("populateDomainTypes contents parsed to null, contents are: {}", jsonArrOfStrClassnames);
            return false;
        } else {
            log.debug("populateDomainTypes gets {} entries from contents: {}", domaintypeStrings.length, jsonArrOfStrClassnames);
        }

        for (String line : domaintypeStrings) {
            log.trace("populateDomainTypes processing " + line);

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
                addToMethodTypes(typeStr);
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
     * the base path of the target server.</p>
     * <p>You might not want an endpoint to be considered for coverage testing, even if
     * it is defined in the URLMAPPINGImpl default set of URLs (perhaps it is not yet implemented
     * in the schema you are testing). Therefore, values in the default URLMAPPINGImpl can
     * be overriden using properties (e.g., in defaulttransport.properties or on command line)
     * to be blank ("ctk.tgt.getVariant=") and then the endpoint is "inactive"
     * and will not be included in the response from getActiveEndpoints.</p>
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

    /**
     * Gets expected returns - all "Response" items, and all return types defined
     * from IDL-defined methods.
     *
     * @return the expected returns
     */
    public List<String> getExpectedReturns() {
        Set<String> exRtns = new HashSet<>(getResponseTypes());
        exRtns.addAll(methodRtn); // TODO - strip out 'void'?
        return new ArrayList<>(exRtns);
    }

    /**
     * Gets list of names of known Methods (no matter which *Methods interface they're on).
     *
     * @return the methodnames
     */
    public List<String> getMethodnames() {
        return new LinkedList<String>(methodnames);
    }
}
