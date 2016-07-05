package org.ga4gh.ctk.transport;

import java.util.Map;
import java.util.StringJoiner;

/**
 * This interface provides access to all of the URL paths we need to talk to the server.
 */
public interface URLMAPPING {

    /**
     * </p>The urlRoot is a string such as http://localhost:8000<p>
     * @return the URL to be used to reach the target server
     */
    String getUrlRoot();

    /**
     * </p>The urlRoot is a string such as http://localhost:8000<p>
     * @param urlRoot URL to be used to reach the target server
     */
    void setUrlRoot(String urlRoot);

    String getGetReadGroupSet();

    void setGetReadGroupSet(String getReadGroupSet);

    String getGetReadGroup();

    void setGetReadGroup(String getReadGroup);

    String getSearchDataSets();

    void setSearchDataSets(String searchDataSets);

    String getGetDataSet();

    void setGetDataSet(String getDataSet);

    String getSearchReads();

    void setSearchReads(String searchReads);

    String getSearchPhenotypes();

    void setSearchPhenotypes(String searchPhenotypes);

    String getSearchGenotypePhenotype();

    void setSearchGenotypePhenotype(String searchGenotypePhenotype);

    String getSearchPhenotypeAssociationSets();

    void setSearchPhenotypeAssociationSets(String searchPhenotypeAssociationSets);

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

    String getSearchReferenceSets();

    void setSearchReferenceSets(String searchReferenceSets);

    String getSearchVariantSets();

    void setSearchVariantSets(String searchVariantSets);

    String getSearchVariants();

    void setSearchVariants(String searchVariants);

    String getGetVariant();

    void setGetVariant(String getVariant);

    String getGetVariantSet();

    void setGetVariantSet(String getVariantSet);

    String getSearchCallSets();

    void setSearchCallSets(String searchCallSets);

    String getGetCallSet();

    void setGetCallSet(String getCallSet);


    String getSearchVariantAnnotationSets();

    void setSearchVariantAnnotationSets(String searchVariantAnnotationSets);

    String getGetVariantAnnotationSet();

    void setGetVariantAnnotationSet(String getVariantAnnotationSet);

    String getSearchVariantAnnotations();

    void setSearchVariantAnnotations(String searchVariantAnnotations);

    String getSearchBioSamples();

    void setSearchBioSamples(String searchBioSamples);

    String getGetBioSample();

    void setGetBioSample(String getBioSamples);

    String getSearchIndividuals();

    void setSearchIndividuals(String searchIndividuals);

    String getGetIndividual();

    void setGetIndividual(String getIndividual);


    String getSearchFeatureSets();

    void setSearchFeatureSets(String searchFeatureSets);

    String getGetFeatureSet();

    void setGetFeatureSet(String getFeatureSet);

    String getSearchFeatures();

    void setSearchFeatures(String searchFeatures);

    String getGetFeature();

    void setGetFeature(String getFeature);


    Map<String, String> getEndpoints();

    void setEndpoints(Map<String, String> endpoints);

    /**
     * load with default properties
     */
    void doInit();

    /**
     * <p>Initialize URLMAPPING.</p>
     * <p>Given a resource name, this loads (in order):
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
        return new URLMAPPINGImpl();
    }

}
