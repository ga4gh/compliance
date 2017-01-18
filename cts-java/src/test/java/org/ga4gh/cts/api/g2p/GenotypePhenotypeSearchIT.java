package org.ga4gh.cts.api.g2p;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.HttpURLConnection;

import ga4gh.GenotypePhenotype;
import ga4gh.GenotypePhenotypeServiceOuterClass;
import ga4gh.GenotypePhenotypeServiceOuterClass.EvidenceQuery;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeResponse;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesResponse;
import ga4gh.Metadata.OntologyTerm;
import ga4gh.SequenceAnnotationServiceOuterClass;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ga4gh.cts.api.Utils.catchGAWrapperException;

/**
 * Tests dealing with searching for GenotypePhenotype associations.
 *
 * @author Brian Walsh
 */
@Category(GenotypePhenotypeTests.class)
public class GenotypePhenotypeSearchIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Ensure an error is thrown if no parameters are supplied.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void ensureCatchNoParametersSearch() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .build();
        final GAWrapperException didThrow =
                catchGAWrapperException(() -> {
                    client.genotypePhenotype.searchGenotypePhenotypes(request);
                });
        assertThat(didThrow.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
    }

     /**
      * Simple search for feature, searches by Genomic feature name.
      * @throws GAWrapperException if the server finds the request invalid in some way
      * @throws UnirestException if there's a problem speaking HTTP to the server
      * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
      */
     @Test
     public void testGenotypesSearchByNameKIT() throws   InvalidProtocolBufferException, UnirestException, GAWrapperException  {

         SearchFeaturesRequest request = SearchFeaturesRequest
                 .newBuilder()
                 .setFeatureSetId(Utils.getFeatureG2PSetId(client))
                 .setName(TestData.FEATURE_NAME)
                 .build();

         SequenceAnnotationServiceOuterClass.SearchFeaturesResponse response = client.sequenceAnnotations.searchFeatures(request);
         assertThat(response.getFeaturesList()).isNotNull() ;
         assertThat(response.getFeaturesList()).isNotEmpty() ;
         assertThat(response.getFeaturesList().size()).isEqualTo(3) ;
     }

    /**
     * Simple search for evidence, searches by drug name.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testGenotypePhenotypeSearchEvidence() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        EvidenceQuery evidenceQuery = EvidenceQuery
                .newBuilder()
                .setDescription(TestData.EVIDENCE_NAME)
                .build();
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .addEvidence(evidenceQuery)
                .build();
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotypes(request);
        assertThat(response.getAssociationsList()).isNotNull() ;
        assertThat(response.getAssociationsList()).isNotEmpty() ;
    }

    /**
     * Checks that evidence level is present, searches by drug name.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testGenotypePhenotypeSearchEnsureEvidence() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // simulate user interacting with sequenceAnnotations
        SearchFeaturesRequest featuresRequest = SearchFeaturesRequest
                .newBuilder()
                .setFeatureSetId(Utils.getFeatureG2PSetId(client))
                .setName(TestData.FEATURE_NAME)
                .build();

        SequenceAnnotationServiceOuterClass.SearchFeaturesResponse featuresResponse = client.sequenceAnnotations.searchFeatures(featuresRequest);
        assertThat(featuresResponse.getFeaturesList()).isNotNull() ;
        assertThat(featuresResponse.getFeaturesList()).isNotEmpty() ;
        assertThat(featuresResponse.getFeaturesList().size()).isEqualTo(3) ;

        final String obfuscated = featuresResponse.getFeaturesList().get(2).getId(); //"WyJkYXRhc2V0MSIsImNnZCIsImh0dHA6Ly9vaHN1LmVkdS9jZ2QvMjdkMjE2OWMiXQ";

        // use obfuscated featureId in G2P request
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest request = GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .addFeatureIds(obfuscated)
                .build();
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotypes(request);
        assertThat(response.getAssociationsList()).isNotNull();
        assertThat(response.getAssociationsList()).isNotEmpty();
        GenotypePhenotype.Evidence evidence = response.getAssociations(0).getEvidence(0);
        assertThat(evidence.getDescription()).isEqualTo(TestData.EVIDENCE_LEVEL);
    }


    @Test
    public void testGenotypePhenotypeSearchFeature() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        // simulate user interacting with sequenceAnnotations
        final String obfuscated = "WyJkYXRhc2V0MSIsImNnZCIsImh0dHA6Ly9vaHN1LmVkdS9jZ2QvMjdkMjE2OWMiXQ";
        // use obfuscated featureId in G2P request
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest request = GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .addFeatureIds(obfuscated)
                .build();
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotypes(request);
        assertThat(response.getAssociationsList()).isNotNull();
        assertThat(response.getAssociationsList()).isNotEmpty();
    }

    /**
     * Simple search for Phenotype, searches by disease name.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypesSearchById() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        GenotypePhenotypeServiceOuterClass.SearchPhenotypesRequest request = GenotypePhenotypeServiceOuterClass.SearchPhenotypesRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .setId(TestData.PHENOTYPE_ID)
                .build();

        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Search for Phenotype by OntologyTerm
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypesSearchOntologyTerm() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        OntologyTerm type = OntologyTerm
                .newBuilder()
                .setTerm(TestData.PHENOTYPE_ONTOLOGYTERM_ID)
                .build();
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .setType(type)
            .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypeSearchQualifiersSensitivityPATO_0000396() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        OntologyTerm ontologyterm = OntologyTerm
                .newBuilder()
                .setTermId(TestData.PHENOTYPE_SENSITIVITY_ID)
                .build();
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .addQualifiers(ontologyterm)
            .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Multiple Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypeSearchMultipleQualifiers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);

        OntologyTerm ontologyterm1 = OntologyTerm
                .newBuilder()
                .setTermId(TestData.PHENOTYPE_SENSITIVITY_ID)
                .build();

        OntologyTerm ontologyterm2 = OntologyTerm
                .newBuilder()
                .setTermId(TestData.PHENOTYPE_ABNORMAL_ID)
                .build();

        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .addQualifiers(0, ontologyterm1)
            .addQualifiers(1, ontologyterm2)
            .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypesSearchDescription() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .setDescription(TestData.PHENOTYPE_DESCRIPTION)
            .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypesSearchDescriptionWildcard() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .setDescription(TestData.PHENOTYPE_DESCRIPTION_WILDCARD)
                .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList().size()).isEqualTo(7);
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void testPhenotypesSearchMultipleTerms() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        OntologyTerm ontologyterm1 = OntologyTerm
                .newBuilder()
                .setTermId(TestData.PHENOTYPE_AGE_OF_ON_SET)
                .build();
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .setDescription(TestData.PHENOTYPE_DESCRIPTION_MELANOMA)
            .setAgeOfOnset(ontologyterm1)
            .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull();
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    /**
     * Simple search for Phenotype, using a non-existent, random disease name.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void simplePhenotypeSearchNoFind() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .setId(Utils.randomName())
                .build();
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypesList()).isNotNull() ;
        assertThat(response.getPhenotypesList()).isEmpty();
    }

}
