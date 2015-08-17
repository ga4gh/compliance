package org.ga4gh.ctk.services;

import org.apache.commons.collections.map.*;
import org.junit.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Wayne Stidolph on 8/16/2015.
 */
public class DomainInformationServiceTest {

    @BeforeClass
    public static void setUpType() throws Exception {
    }

    static DomainInformationService svc;
    @Before
    public void setUp() throws Exception {
       svc  = DomainInformationService.getService(defaultTypes);
    }

    @Test
    public void testGetRequestTypes() throws Exception {
        assertThat(svc.getRequestTypes()).containsExactly(
                "org.ga4gh.methods.ListReferenceBasesRequest",
                "org.ga4gh.methods.SearchCallSetsRequest",
                "org.ga4gh.methods.SearchDatasetsRequest",
                "org.ga4gh.methods.SearchReadGroupSetsRequest",
                "org.ga4gh.methods.SearchReadsRequest",
                "org.ga4gh.methods.SearchReferenceSetsRequest",
                "org.ga4gh.methods.SearchReferencesRequest",
                "org.ga4gh.methods.SearchVariantSetsRequest",
                "org.ga4gh.methods.SearchVariantsRequest"
        );
    }

    @Test
    public void testGetResponseTypes() throws Exception {
        assertThat(svc.getResponseTypes()).containsExactly(
                "org.ga4gh.methods.ListReferenceBasesResponse",
                "org.ga4gh.methods.SearchCallSetsResponse",
                "org.ga4gh.methods.SearchDatasetsResponse",
                "org.ga4gh.methods.SearchReadGroupSetsResponse",
                "org.ga4gh.methods.SearchReadsResponse",
                "org.ga4gh.methods.SearchReferenceSetsResponse",
                "org.ga4gh.methods.SearchReferencesResponse",
                "org.ga4gh.methods.SearchVariantSetsResponse",
                "org.ga4gh.methods.SearchVariantsResponse"
        );
    }

    @Test
    public void testGetDataObjType() throws Exception {
        assertThat(svc.getDataObjType()).containsExactly(
                "org.ga4gh.models.Call",
                "org.ga4gh.models.CallSet",
                "org.ga4gh.models.CigarOperation",
                "org.ga4gh.models.CigarUnit",
                "org.ga4gh.models.Common",
                "org.ga4gh.models.Dataset",
                "org.ga4gh.models.Experiment",
                "org.ga4gh.models.ExternalIdentifier",
                "org.ga4gh.models.Fragment",
                "org.ga4gh.models.LinearAlignment",
                "org.ga4gh.models.Metadata",
                "org.ga4gh.models.Position",
                "org.ga4gh.models.Program",
                "org.ga4gh.models.ReadAlignment",
                "org.ga4gh.models.ReadGroup",
                "org.ga4gh.models.ReadGroupSet",
                "org.ga4gh.models.Reads",
                "org.ga4gh.models.ReadStats",
                "org.ga4gh.models.Reference",
                "org.ga4gh.models.References",
                "org.ga4gh.models.ReferenceSet",
                "org.ga4gh.models.Strand",
                "org.ga4gh.models.Variant",
                "org.ga4gh.models.Variants",
                "org.ga4gh.models.VariantSet",
                "org.ga4gh.models.VariantSetMetadata"
        );
    }

    @Test
    public void testGetOtherTypes() throws Exception {
        assertThat(svc.getOtherTypes()).containsExactly(
                "org.ga4gh.methods.GAException", "org.ga4gh.methods.RPC"
        );
    }

    @Test
    public void testGetMethods() throws Exception {
        // method isn't implemented so it returns empty string
        assertThat(svc.getMethods()).containsExactly();
    }

    @Test
    public void testExtractDomainTypes() throws Exception {
        svc.clearTypes();
        assertThat(svc.extractDomainTypes(defaultTypes)).isTrue();
    }

    @Test
    public void testGetActiveEndpoints() throws Exception {
        Map<String, String> dummyEndpoints = new HashedMap();
        dummyEndpoints.put("A","Aused");
        dummyEndpoints.put("B",""); // empty string means "inactive"
        dummyEndpoints.put("E","Eused");
        dummyEndpoints.put("F","Dused");
        dummyEndpoints.put("D","Dused"); // both F & D map to same endpoint
        List<String> ue = svc.getActiveEndpoints(dummyEndpoints);

        // this assertion verifies they're unique and ordered and non-active stripped
        assertThat(ue).containsExactly("Aused","Dused","Eused");

    }

    final static String defaultTypes = "["
           + "\"org\\\\ga4gh\\\\methods\\\\GAException.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\ListReferenceBasesRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\ListReferenceBasesResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\ReadMethods.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\ReferenceMethods.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\RPC.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchCallSetsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchCallSetsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchDatasetsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchDatasetsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReadGroupSetsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReadGroupSetsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReadsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReadsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReferenceSetsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReferenceSetsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReferencesRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchReferencesResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchVariantSetsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchVariantSetsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchVariantsRequest.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\SearchVariantsResponse.java\","
           + "\"org\\\\ga4gh\\\\methods\\\\VariantMethods.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Call.java\","
           + "\"org\\\\ga4gh\\\\models\\\\CallSet.java\","
           + "\"org\\\\ga4gh\\\\models\\\\CigarOperation.java\","
           + "\"org\\\\ga4gh\\\\models\\\\CigarUnit.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Common.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Dataset.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Experiment.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ExternalIdentifier.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Fragment.java\","
           + "\"org\\\\ga4gh\\\\models\\\\LinearAlignment.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Metadata.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Position.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Program.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ReadAlignment.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ReadGroup.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ReadGroupSet.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Reads.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ReadStats.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Reference.java\","
           + "\"org\\\\ga4gh\\\\models\\\\References.java\","
           + "\"org\\\\ga4gh\\\\models\\\\ReferenceSet.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Strand.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Variant.java\","
           + "\"org\\\\ga4gh\\\\models\\\\Variants.java\","
           + "\"org\\\\ga4gh\\\\models\\\\VariantSet.java\","
           + "\"org\\\\ga4gh\\\\models\\\\VariantSetMetadata.java\""
           + "]";

}