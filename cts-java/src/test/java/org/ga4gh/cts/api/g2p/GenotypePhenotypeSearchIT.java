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

import ga4gh.GenotypePhenotype.Evidence;
import ga4gh.GenotypePhenotypeServiceOuterClass;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeResponse;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesResponse;
import ga4gh.Metadata.OntologyTerm;

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
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .build();
        final GAWrapperException didThrow =
                catchGAWrapperException(() -> {
                    client.genotypePhenotype.searchGenotypePhenotypes(request);
                });
        assertThat(didThrow.getHttpStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    // /**
    //  * Simple search for feature, searches by Genomic feature name.
    //  * @throws AvroRemoteException if there's an unanticipated error
    //  */
    // @Test
    // public void simpleFeatureSearch() throws AvroRemoteException {
    //     final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
    //     SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
    //             .newBuilder()
    //             .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
    //             .build();
    //     request.setFeature(TestData.FEATURE_NAME);
    //     SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
    //     assertThat(response.getAssociations()).isNotNull() ;
    //     assertThat(response.getAssociations()).isNotEmpty() ;
    // }

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
        //TODO -- request.setEvidence(TestData.EVIDENCE_NAME);
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
    public void evidenceLevelCheck() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);

        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
                .newBuilder()
                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
                .setGenotypeIds(0,TestData.GENOTYPE_ID)
                .build();
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotypes(request);
        assertThat(response.getAssociations(0).getEvidenceList()).isNotNull();
        assertThat(response.getAssociations(0).getEvidenceList()).isNotEmpty();
        Evidence evidence = response.getAssociations(0).getEvidence(0);
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
    public void phenotypeSearchByOntologyTerm() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
    public void phenotypeSearchQualifers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        OntologyTerm ontologyterm = OntologyTerm
                .newBuilder()
                .setTerm(TestData.PHENOTYPE_SENSITIVITY_ID)
                .build();
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .addQualifiers(0,ontologyterm)
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
    public void phenotypeSearchMultipleQualifers() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);

        OntologyTerm ontologyterm1 = OntologyTerm
                .newBuilder()
                .setTerm(TestData.PHENOTYPE_SENSITIVITY_ID)
                .build();

        OntologyTerm ontologyterm2 = OntologyTerm
                .newBuilder()
                .setTerm(TestData.PHENOTYPE_SENSITIVITY_ID)
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
    public void phenotypeSearchDescription() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
    public void phenotypeSearchDescriptionWildcard() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
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
    public void phenotypeSearchMultipleTerms() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
        OntologyTerm ontologyterm1 = OntologyTerm
                .newBuilder()
                .setTerm(TestData.PHENOTYPE_AGE_OF_ON_SET)
                .build();
        SearchPhenotypesRequest request = SearchPhenotypesRequest
            .newBuilder()
            .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
            .setDescription(TestData.PHENOTYPE_DESCRIPTION_MELANOMA)
            .setAgeOfOnSet(ontologyterm1)
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
        assertThat(response.getPhenotypesList()).isNotEmpty();
    }

    // /**
    //  * Search for Feature , using for specific external identifier.
    //  * @throws AvroRemoteException if there's an unanticipated error
    //  */
    // @Test
    // public void simpleFeatureSearchExternalIdentifier() throws AvroRemoteException {
    //     final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
    //     SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
    //             .newBuilder()
    //             .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
    //             .build();
    //     ExternalIdentifier id = new ExternalIdentifier();
    //     id.setDatabase(TestData.FEATURE_DB);
    //     id.setIdentifier(TestData.FEATURE_DB_ID);
    //     id.setVersion(TestData.FEATURE_DB_VERSION);
    //     List<ExternalIdentifier> ids = new ArrayList<>();
    //     ids.add(id) ;
    //     ExternalIdentifierQuery feature = new ExternalIdentifierQuery();
    //     feature.setIds(ids);
    //     request.setFeature(feature);
    //     SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
    //     assertThat(response.getAssociations()).isNotNull() ;
    //     assertThat(response.getAssociations().size()).isEqualTo(1);
    // }

//    /**
//     * Simple search for Feature, test page size = 1, expects a single result.
//     *
//     * @throws GAWrapperException if the server finds the request invalid in some way
//     * @throws UnirestException if there's a problem speaking HTTP to the server
//     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
//     */
//    @Test
//    public void testGenotypePhenotypeSearchFeaturePagingOne() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
//        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
//        SearchGenotypesRequest request = SearchGenotypesRequest
//                .newBuilder()
//                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
//                .build();
//        request.setReferenceName(TestData.FEATURE_NAME);
//        request.setPageSize(1);
//        SearchGenotypesResponse response = client.genotypePhenotype.searchGenotypes(request);
//        assertThat(response).isNotNull();
//        assertThat(response.getGenotypes()).isNotNull() ;
//        assertThat(response.getGenotypes().size()).isEqualTo(1);
//        assertThat(response.getNextPageToken().isNotNull());
//    }

//    /**
//     * Simple search for Feature, test page size = null, expects more than one result in the page.
//     *
//     * @throws GAWrapperException if the server finds the request invalid in some way
//     * @throws UnirestException if there's a problem speaking HTTP to the server
//     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
//     */
//    @Test
//    public void testGenotypePhenotypeSearchFeaturePagingMore() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
//        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
//        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
//                .newBuilder()
//                .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
//                .build();
//        request.setReferenceName(TestData.FEATURE_NAME);
//        SearchGenotypesResponse response = client.genotypePhenotype.searchGenotypes(request);
//        assertThat(response.getGenotypes()).isNotNull();
//        assertThat(response.getGenotypes().size()).isGreaterThan(1);
//        assertThat(response.getNextPageToken().isEqualTo(''));
//    }
//
//    /**
//     * Simple search for Feature, test page size = 1, find all, expects a single result per page.
//     *
//     * @throws GAWrapperException if the server finds the request invalid in some way
//     * @throws UnirestException if there's a problem speaking HTTP to the server
//     * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
//     */
//    @Test
//    public void testGenotypePhenotypeSearchFeaturePagingAll() throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
//        final String phenotypeAssociationSetId = Utils.getPhenotypeAssociationSetId(client);
//        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest
//                                                    .newBuilder()
//                                                    .setPhenotypeAssociationSetId(phenotypeAssociationSetId)
//                                                    .build();
//        request.setReferenceName(TestData.FEATURE_NAME);
//        request.setPageSize(1);
//        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
//        assertThat(response.getAssociations()).isNotNull() ;
//        assertThat(response.getAssociations().size()).isEqualTo(1);
//        while(response.getNextPageToken() != null) {
//            System.err.println(response.getNextPageToken());
//            String previous_id = response.getAssociations().get(0).getId();
//            request = SearchGenotypePhenotypeRequest.newBuilder().setPhenotypeAssociationSetId(phenotypeAssociationSetId).build();
//            request.setFeature(TestData.FEATURE_NAME);
//            request.setPageSize(1);
//            request.setPageToken(response.getNextPageToken());
//            response = client.genotypePhenotype.searchGenotypePhenotype(request);
//            if (response.getNextPageToken() != null ) {
//                assertThat(response.getAssociations()).isNotEmpty();
//                assertThat(previous_id).isNotEqualTo(response.getAssociations().get(0).getId());
//            }
//        }
//    }
}
