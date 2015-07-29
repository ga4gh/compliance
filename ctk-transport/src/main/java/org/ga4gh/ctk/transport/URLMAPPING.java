package org.ga4gh.ctk.transport;

import java.util.*;

/**
 * Created by Wayne Stidolph on 7/16/2015.
 */
public interface URLMAPPING {

    /**
     * </p>The urlRoot is a string such as http://localhost:8000/v0.5.1<p>
     * @return the URL to be used to reach the target server
     */
    String getUrlRoot();

    /**
     * </p>The urlRoot is a string such as http://localhost:8000/v0.5.1<p>
     * @param urlRoot URL to be used to reach the target server
     */
    void setUrlRoot(String urlRoot);

    String getSearchReads();

    void setSearchReads(String searchReads);

    String getSearchReadGroupSets();

    void setSearchReadGroupSets(String searchReadGroupSets);

    String getReference();

    void setReference(String reference);

    String getSearchReferences();

    void setSearchReferences(String searchReferences);

    String getSearchReferenceBases();

    void setSearchReferenceBases(String searchReferenceBases);

    String getReferenceSets();

    void setReferenceSets(String referenceSets);

    String getSearchReferencesets();

    void setSearchReferencesets(String searchReferencesets);

    String getSearchVariantSets();

    void setSearchVariantSets(String searchVariantSets);

    String getSearchVariants();

    void setSearchVariants(String searchVariants);

    String getSearchCallsets();

    void setSearchCallsets(String searchCallsets);

    Map<String, String> getEndpoints();

    void setEndpoints(Map<String, String> endpoints);

    /**
     * load with default properties
     */
    void doInit();

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
     * If the resName is given then the defaultproperties file is not loaded at all.
     *
     * @param resourceName the resource name to init from (if blank, uses 'defaulttransport.properties')
     */
    void doInit(String resourceName);

    /**
     * Gets a instance of URLMAPPING filled in with the defaults
     * and with property overrides.
     * @return an URLMAPPINGImpl
     */
    static URLMAPPING getInstance(){
        URLMAPPING u = new URLMAPPINGImpl();
        //u.doInit(); the URLMAPPINGImpl constructor does this
        return u;}

}
