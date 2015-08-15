package org.ga4gh.ctk.transport;

import org.junit.*;

import static org.assertj.core.api.Assertions.*;

/**
 * URLMAPPING Tester.
 * Problem - this test will fail if any of the URLMAPPING property file
 * or system or environment variable endpoint-setting mechanisms are in
 * use. Also, since it's loading up a <b>static</b> class, it might be that
 * these simple tests could affect later uses of URLMAPPING in the same JVM.
 *
 * So, it's not really useful as a test right now, it's just for
 * exploratory use during development, and I comment out the @Tests
 * and @Deprecate the test class
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 19, 2015</pre>
 */
@Deprecated
public class URLMAPPINGTest {

    URLMAPPINGImpl urlmapping;

    @Before
    public void before() throws Exception {
        urlmapping=new URLMAPPINGImpl();
        urlmapping.doInit();
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: loadProps(String resName)
     */
    //@Test
    public void testLoadProps() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: mergePropertiesIntoMap(Properties props, Map map)
     */
    //@Test
    public void testMergePropertiesIntoMap() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getUrlRoot()
     */
    //@Test
    public void testGetUrlRoot() throws Exception {
        String url = urlmapping.getUrlRoot();
        assertThat(url).isEqualTo("http://localhost:8000/v0.5.1/");
    }

    /**
     * Method: setUrlRoot(String urlRoot)
     */
   // @Test
    public void testSetUrlRoot() throws Exception {
        urlmapping.setUrlRoot("phoney");
        assertThat(urlmapping.getUrlRoot()).isEqualTo("phoney");
    }

    /**
     * Method: getSearchReads()
     */
    @Test
    public void testGetSearchReads() throws Exception {
        assertThat(urlmapping.getSearchReads()).isEqualTo("reads/search");
    }


    /**
     * Method: getSearchReadGroupSets()
     */
    @Test
    public void testGetSearchReadGroupSets() throws Exception {
        assertThat(urlmapping.getSearchReadGroupSets()).isEqualTo("readgroupsets/search");
    }

    /**
     * Method: getSearchReferencesets()
     */
    @Test
    public void testGetSearchReferencesets() throws Exception {
        assertThat(urlmapping.getSearchReferencesets()).isEqualTo("referencesets/search");
    }

    /**
     * Method: getSearchVariantSets()
     */
    @Test
    public void testGetSearchVariantSets() throws Exception {
        assertThat(urlmapping.getSearchVariantSets()).isEqualTo("variantsets/search");
    }

    /**
     * Method: getSearchVariants()
     */
    @Test
    public void testGetSearchVariants() throws Exception {
        assertThat(urlmapping.getSearchVariants()).isEqualTo("variants/search");
    }

    /**
     * Method: getSearchCallsets()
     */
    @Test
    public void testGetSearchCallsets() throws Exception {
        assertThat(urlmapping.getSearchCallsets()).isEqualTo("callsets/search");
    }

} 
