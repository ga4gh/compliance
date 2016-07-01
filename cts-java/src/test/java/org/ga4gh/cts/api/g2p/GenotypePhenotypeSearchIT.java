package org.ga4gh.cts.api.g2p;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.Utils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.ga4gh.cts.api.TestData;

import ga4gh.GenotypePhenotypeServiceOuterClass.ExternalIdentifierQuery;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeResponse;
import ga4gh.GenotypePhenotype.ExternalIdentifier;
import ga4gh.GenotypePhenotype.Evidence;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void ensureCatchNoParametersSearch() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        final GAWrapperException didThrow =
                catchGAWrapperException(() -> client.genotypePhenotype.searchGenotypePhenotype(request) );
        assertThat(didThrow.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    /**
     * Simple search for feature, searches by Genomic feature name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simpleFeatureSearch() throws AvroRemoteException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setFeature(TestData.FEATURE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Simple search for evidence, searches by drug name.
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void simpleEvidenceSearch() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setEvidence(TestData.EVIDENCE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Checks that evidence level is present, searches by drug name.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void evidenceLevelCheck() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        List<String> ids = new ArrayList<>();
        ids.add(TestData.GENOTYPE_ID);
        request.setGenotypeIds(ids);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations().get(0).getEvidence()).isNotNull();
        assertThat(response.getAssociations().get(0).getEvidence()).isNotEmpty();
        Evidence evidence = response.getAssociations()..get(0).getEvidence().get(0);
        assertThat(evidence.getDescription()).isEqualTo(TestData.EVIDENCE_LEVEL);
    }

    /**
     * Simple search for Phenotype, searches by disease name.
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchById() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setId(TestData.PHENOTYPE_ID);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Search for Phenotype by OntologyTerm
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchByOntologyTerm() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        request.type.setId(TestData.PHENOTYPE_ONTOLOGYTERM_ID);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchQualifers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        OntologyTerm ontologyterm = new OntologyTerm();
        ontologyterm.setId(TestData.PHENOTYPE_SENSITIVITY_ID);
        List<OntologyTerm> qualifiers = new ArrayList<>();
        qualifiers.add(ontologyterm)
        request.setQualifiers(qualifiers);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Multiple Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchMultipleQualifers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        List<OntologyTerm> qualifiers = new ArrayList<>();
        OntologyTerm ontologyterm1 = new OntologyTerm();
        ontologyterm1.setId(TestData.PHENOTYPE_SENSITIVITY_ID);
        qualifers.add(ontologyterm1);
        OntologyTerm ontologyterm2 = new OntologyTerm();
        ontologyterm2.setId(TestData.PHENOTYPE_SENSITIVITY_ID);
        qualifers.add(ontologyterm2);
        request.setQualifiers(qualifiers);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchDescription() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        request.setDescription(TestData.PHENOTYPE_DESCRIPTION);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchDescriptionWildcard() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        request.setDescription(TestData.PHENOTYPE_DESCRIPTION_WILDCARD);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes().size()).isEqualTo(7);
    }

    /**
     * Search for Phenotype by Qualifiers
     *
     * @throws GAWrapperException if the server finds the request invalid in some way
     * @throws UnirestException if there's a problem speaking HTTP to the server
     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
     */
    @Test
    public void phenotypeSearchMultipleTerms() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .build();
        request.setDescription(TestData.PHENOTYPE_DESCRIPTION_MELANOMA);
        request.setAgeOfOnSet(TestData.PHENOTYPE_AGE_OF_ON_SET);
        SearchPhenotypesResponse response = client.genotypePhenotype.searchPhenotypes(request);
        assertThat(response.getPhenotypes()).isNotNull();
        assertThat(response.getPhenotypes()).isNotEmpty();
    }

    /**
     * Simple search for Phenotype, using a non-existent, random disease name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simplePhenotypeSearchNoFind() throws AvroRemoteException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchPhenotypeRequest request = SearchPhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setId(Utils.randomName());
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isEmpty();
    }

    /**
     * Search for Feature , using for specific external identifier.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simpleFeatureSearchExternalIdentifier() throws AvroRemoteException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        ExternalIdentifier id = new ExternalIdentifier();
        id.setDatabase(TestData.FEATURE_DB);
        id.setIdentifier(TestData.FEATURE_DB_ID);
        id.setVersion(TestData.FEATURE_DB_VERSION);
        List<ExternalIdentifier> ids = new ArrayList<>();
        ids.add(id) ;
        ExternalIdentifierQuery feature = new ExternalIdentifierQuery();
        feature.setIds(ids);
        request.setFeature(feature);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations().size()).isEqualTo(1);
    }

    /**
     * Search for Phenotype , using specific external identifier.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simplePhenotypeSearchExternalIdentifier() throws AvroRemoteException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        ExternalIdentifier id = new ExternalIdentifier() ;
        id.setDatabase(TestData.PHENOTYPE_DB);
        id.setIdentifier(TestData.PHENOTYPE_DB_ID);
        id.setVersion(TestData.PHENOTYPE_DB_VERSION);
        List<ExternalIdentifier> ids = new ArrayList<>();
        ids.add(id) ;
        ExternalIdentifierQuery phenotype = new ExternalIdentifierQuery();
        phenotype.setIds(ids);
        request.setPhenotype(phenotype);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull();
        assertThat(response.getAssociations()).isNotEmpty();
    }

    /**
     * Search for Evidence, using specific external identifier.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simpleEvidenceSearchExternalIdentifier() throws AvroRemoteException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        ExternalIdentifier id = new ExternalIdentifier();
        id.setDatabase(TestData.EVIDENCE_DB);
        id.setIdentifier(TestData.EVIDENCE_DB_ID);
        id.setVersion(TestData.EVIDENCE_DB_VERSION);
        List<ExternalIdentifier> ids = new ArrayList<>();
        ids.add(id) ;
        ExternalIdentifierQuery evidence = new ExternalIdentifierQuery();
        evidence.setIds(ids);
        request.setEvidence(evidence);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull();
        assertThat(response.getAssociations()).isNotEmpty();
        Evidence evidenceObject = response.getAssociations().get(0).getEvidence().get(0);
        assertThat(evidenceObject.getDescription()).isEqualTo(TestData.EVIDENCE_LEVEL);
    }

    /**
     * Simple search for Feature, test page size = 1, expects a single result.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingOne() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypesRequest request = SearchGenotypesRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setReferenceName(TestData.FEATURE_NAME);
        request.setPageSize(1);
        SearchGenotypesResponse response = client.genotypePhenotype.searchGenotypes(request);
        assertThat(response).isNotNull();
        assertThat(response.getGenotypes()).isNotNull() ;
        assertThat(response.getGenotypes().size()).isEqualTo(1);
        assertThat(response.getNextPageToken().isNotNull());
    }

    /**
     * Simple search for Feature, test page size = null, expects more than one result in the page.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingMore() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        request.setReferenceName(TestData.FEATURE_NAME);
        SearchGenotypesResponse response = client.genotypePhenotype.searchGenotypes(request);
        assertThat(response.getGenotypes()).isNotNull();
        assertThat(response.getGenotypes().size()).isGreaterThan(1);
        assertThat(response.getNextPageToken().isEqualTo(''));
    }

    /**
     * Simple search for Feature, test page size = 1, find all, expects a single result per page.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingAll() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                                                    .newBuilder()
                                                    .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                                                    .build();
        request.setReferenceName(TestData.FEATURE_NAME);
        request.setPageSize(1);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations().size()).isEqualTo(1);
        while(response.getNextPageToken() != null) {
            System.err.println(response.getNextPageToken());
            String previous_id = response.getAssociations().get(0).getId();
            request = SearchGenotypePhenotypeRequest.newBuilder().setPhenotypeAssociationSetId(phenotypeAssociationSetId).build();
            request.setFeature(TestData.FEATURE_NAME);
            request.setPageSize(1);
            request.setPageToken(response.getNextPageToken());
            response = client.genotypePhenotype.searchGenotypePhenotype(request);
            if (response.getNextPageToken() != null ) {
                assertThat(response.getAssociations()).isNotEmpty();
                assertThat(previous_id).isNotEqualTo(response.getAssociations().get(0).getId());
            }
        }
    }

}
