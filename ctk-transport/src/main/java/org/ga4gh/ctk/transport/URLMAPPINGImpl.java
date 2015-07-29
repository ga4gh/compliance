package org.ga4gh.ctk.transport;

import java.io.*;
import java.util.*;

import static org.slf4j.LoggerFactory.*;

/**
 * <p>Mapping support to get from an IDL endpoint to an implementation endpoint.</p>
 * <p>Probably this would be better-done using e.g., Apache Commons Configuration,
 * but that seems to get more complicated than this code needs to be so I just put
 * this together fairly quick and only anecdotally tested it - so don't have a lot
 * of faith in this and feel free to swap in a robust solution!</p>
 *
 * <p>Created by Wayne Stidolph on 5/25/2015.</p>
 */

public class URLMAPPINGImpl implements URLMAPPING {

    private static org.slf4j.Logger log = getLogger(URLMAPPINGImpl.class);
    /**
     * <p>Map IDL message name to target server endpoint.</p>
     * <p>This Map is initialized in code with defaults from the
     * IDL (hand-copied, might not be up to date). On startup of the
     * URLMAPPING class, an initializer reads in an UrlMapping.properties
     * file and merges it with this Map, which allows for extension or
     * overwrite.</p>
     * <p>The Map is public and mutable so it can be easily examined or
     * replaced in test code.</p>
     * <p>Design note - why not an Enum? For extensibility-without-compiling.
     * This lets a test writer/API developer add or change URLs without having
     * to repackage the transport module.</p>
     */
    public Map<String, String> endpoints;
    private static Map<String, String> defaultEndpoints;

    /**
     * <p>dumpToStdOut is a property set at the java System level '-Dctk.tgt.urlmapper.dump=true'</p>
     * <p>When this property is set, the URLMAPPER dumps directly to the stdout what actions it
     * takes during the doInit() method (what
     * 'ctk.tgt.*' properties it discovers and their values).
     * This static method is invoked during class initialization, and by tests which
     * want to ensure the transport environment is initialzied as they expect</p>
     */
    public static boolean dumpToStdOut = false;

    /* **** DEFAULT ENDPOINTS no matter what, we have these *** */
    static {
        log = getLogger(URLMAPPINGImpl.class);

        defaultEndpoints = new HashMap<>();
        defaultEndpoints.put("ctk.tgt.searchReads", "reads/search");
        defaultEndpoints.put("ctk.tgt.searchReadGroupSets", "readgroupsets/search");
        defaultEndpoints.put("ctk.tgt.searchReferences", "references/search");
        defaultEndpoints.put("ctk.tgt.searchReferencesets", "referencesets/search");
        defaultEndpoints.put("ctk.tgt.searchVariantSets", "variantsets/search");
        defaultEndpoints.put("ctk.tgt.searchVariants", "variants/search");
        defaultEndpoints.put("ctk.tgt.searchCallsets", "callsets/search");
        defaultEndpoints.put("ctk.tgt.getReferences", "references/{id}");
        defaultEndpoints.put("ctk.tgt.getReferencesets", "referencesets/{id}");
        defaultEndpoints.put("ctk.tgt.getReferencesBases", "references/{id}/bases");

        dumpToStdOut = Boolean.getBoolean("ctk.tgt.urlmapper.dump"); // so, -Dctk.urlmapper.dump= true

        log.info("set default URLMAPPING urlRoot to " + defaultEndpoints.get("ctk.tgt.urlRoot"));
    }

    /**
     * Load with default properties.
     */
    public URLMAPPINGImpl() {
        doInit(""); // empty string will cause defaulttransport.properties to load
    }

    /**
     * <p>Initialize URLMAPPING.</p>
     * <p>Given a resource name, this looks loads (in order):
     * <ul>
     * <li>a properties file of that name on the classpath</li>
     * <li>a properties file of that name from the file system</li>
     * <li>the operating system environment variables ("ctk.tgt.*)</li>
     * <li>the java system properties (e.g., command line -D...) of "ctk.tgt.*"</li>
     * </ul>
     * If the resName is blank then the file/resource sought is "defaulttransport.properties"
     * If the resName is given then the default properties file is not loaded at all.
     *
     * @param resName the resource name to init from (if blank, uses 'defaulttransport.properties')
     */
    public void doInit(String resName) {

        endpoints = new HashMap<>(defaultEndpoints); // start fresh using baked-in defaults

        if (resName == null || resName.isEmpty()) {
            resName = "defaulttransport.properties";
        }

        if (dumpToStdOut) {
            System.out.println("\nprocess resource/file " + resName);
        }
        Properties tempProps = loadPropsName(resName);
        if (!tempProps.isEmpty()) {
            mergePropertiesIntoMap(tempProps, endpoints);
        }
        if (dumpToStdOut) {
            System.out.println("\nprocess Env");
        }
        tempProps = loadPropsEnv("ctk.tgt.");
        if (!tempProps.isEmpty()) {
            mergePropertiesIntoMap(tempProps, endpoints);
        }
        if (dumpToStdOut) {
            System.out.println("\nprocess Sys");
        }
        tempProps = loadPropsSystem("ctk.tgt.");
        if (!tempProps.isEmpty()) {
            mergePropertiesIntoMap(tempProps, endpoints);
        }
    }

    /**
     * Do init, defaults.
     * <p>
     * Just syntactic sugar for doInit("").
     */
    public void doInit() {
        doInit("");
    }

    /**
     * Load properties by resource or file name.
     * <p>Looks on classpath for resource with provided name;
     * if found, loads the included properties matching prefix "ctk.tgt."</p>
     * <p>Looks on file system for resource matching name; loads those props
     * (which allows for file system to override classpath).</p>
     *
     * @param resName the res name
     * @return the properties
     */
    public static Properties loadPropsName(String resName) {

        Properties tempProps = new Properties();

        // first we load anything on the classpath
        try (InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName)) {

            tempProps.load(inStream);

            if (dumpToStdOut) {
                System.out.println("loaded props from classpath " + resName);
            }
        } catch (Exception ioe) { // just ignore not-found
            if (dumpToStdOut) {
                System.out.println("Did not find resource " + resName);
            }
        }

        // then we try the same name on the file system
        try (InputStream inStream = new FileInputStream(resName)) {

            tempProps.load(inStream);

            if (dumpToStdOut) {
                System.out.println("loaded props from file " + resName);
            }
        } catch (Exception ioe) {
            if (dumpToStdOut) {
                System.out.println("Did not find property file " + resName);
            }
        }

        return tempProps;
    }

    public static Properties loadPropsSystem(String prefix) {
        Properties props = new Properties();
        for (Object key : System.getProperties().keySet()) {
            String ks = key.toString();

            if (ks.startsWith(prefix)) {
                if (dumpToStdOut) {
                    System.out.println("Sys prop has " + ks + " => " + System.getProperty(ks));
                }
                props.put(ks, System.getProperty(ks));
            }
        }
        return props;
    }

    public static Properties loadPropsEnv(String prefix) {
        Map<String, String> env = System.getenv();
        // copy over the env vals with right name
        Properties props = new Properties();
        for (String key : env.keySet()) {

            if (key.startsWith(prefix)) {
                if (dumpToStdOut) {
                    System.out.println("env has " + key + " => " + env.get(key));
                }
                props.put(key, env.get(key));
            }
        }
        return props;
    }

    public static void mergePropertiesIntoMap(Properties props, Map map) {
        if (map != null && props != null) {
            for (Enumeration en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String)en.nextElement();
                //noinspection unchecked
                map.put(key, props.getProperty(key));
                if (dumpToStdOut) {
                    System.out.println("merging " + key + " => " + props.getProperty(key));
                }
            }
        }
    }

    @Override
    public String getUrlRoot() {
        return endpoints.get("ctk.tgt.urlRoot");
    }

    public void setUrlRoot(String urlRoot) {
        log.debug("setUrlRoot param is " + urlRoot);
        if (urlRoot != null && !urlRoot.isEmpty()) {
            if (!urlRoot.endsWith("/")) {
                urlRoot = urlRoot + "/";
            }
            endpoints.put("ctk.tgt.urlRoot", urlRoot);
            log.debug("setUrlRoot sets ctk.tgt.urlRoot to " + urlRoot);
        } else {
            log.debug("setUrlRoot got null/empty argument, not making change");
        }
    }

    // Syntactic sugar, mostly to help IDEs autocomplete
    // These methods are an incomplete list, I just added them when
    // I needed them. You can always just do
    // URLMAPPING.endpoints.get("mykey") etc.

    @Override
    public String getSearchReads() {
        return endpoints.get("ctk.tgt.searchReads");
    }

    @Override
    public void setSearchReads(String searchReads) {
        endpoints.put("ctk.tgt.searchReads", searchReads);
    }

    @Override
    public String getSearchReadGroupSets() {
        return endpoints.get("ctk.tgt.searchReadGroupSets");
    }

    @Override
    public void setSearchReadGroupSets(String searchReadGroupSets) {
        endpoints.put("ctk.tgt.searchReadGroupSets", searchReadGroupSets);
    }

    @Override
    public String getReference() {
        return endpoints.get("ctk.tgt.getReferences");
    }

    @Override
    public void setReference(String reference) {
        endpoints.put("ctk.tgt.getReferences", reference);
    }

    @Override
    public String getReferenceSets() {
        return endpoints.get("ctk.tgt.getReferencesets");
    }

    @Override
    public void setReferenceSets(String referenceSets) {
        endpoints.put("ctk.tgt.getReferencesets", referenceSets);
    }

    @Override
    public String getSearchReferences() {
        return endpoints.get("ctk.tgt.searchReferences");
    }

    @Override
    public void setSearchReferences(String searchReferences) {
        endpoints.put("ctk.tgt.searchReferences", searchReferences);
    }

    @Override
    public String getSearchReferenceBases() {
        return endpoints.get("ctk.tgt.getReferencesBases");
    }

    @Override
    public void setSearchReferenceBases(String searchReferenceBases) {
        endpoints.put("ctk.tgt.getReferencesBases", searchReferenceBases);
    }

    @Override
    public String getSearchReferencesets() {
        return endpoints.get("ctk.tgt.searchReferencesets");
    }

    @Override
    public void setSearchReferencesets(String searchReferencesets) {
        endpoints.put("ctk.tgt.searchReferencesets", searchReferencesets);
    }

    @Override
    public String getSearchVariantSets() {
        return endpoints.get("ctk.tgt.searchVariantSets");
    }

    @Override
    public void setSearchVariantSets(String searchVariantSets) {
        endpoints.put("ctk.tgt.searchVariantSets", searchVariantSets);
    }

    @Override
    public String getSearchVariants() {
        return endpoints.get("ctk.tgt.searchVariants");
    }

    @Override
    public void setSearchVariants(String searchVariants) {
        endpoints.put("ctk.tgt.searchVariants", searchVariants);
    }

    @Override
    public String getSearchCallsets() {
        return endpoints.get("ctk.tgt.searchCallsets");
    }

    @Override
    public void setSearchCallsets(String searchCallsets) {
        endpoints.put("ctk.tgt.searchCallsets", searchCallsets);
    }

    @Override
    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    @Override
    public void setEndpoints(Map<String, String> newEndpoints) {
        endpoints = newEndpoints;
    }
}
