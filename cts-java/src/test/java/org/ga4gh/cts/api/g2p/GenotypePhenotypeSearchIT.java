package org.ga4gh.cts.api.g2p;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.ExternalIdentifierQuery;
import org.ga4gh.methods.SearchGenotypePhenotypeRequest;
import org.ga4gh.methods.SearchGenotypePhenotypeResponse;
import org.ga4gh.models.ExternalIdentifier;
import org.ga4gh.models.Evidence;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
public class GenotypePhenotypeSearchIT {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Ensure an error is thrown if no parameters are supplied.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void ensureCatchNoParametersSearch() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
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
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setFeature(TestData.FEATURE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Simple search for evidence, searches by drug name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simpleEvidenceSearch() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setEvidence(TestData.EVIDENCE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Checks that evidence level is present, searches by drug name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void evidenceLevelCheck() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setFeature(TestData.FEATURE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull();
        assertThat(response.getAssociations()).isNotEmpty();
        Evidence evidence = response.getAssociations().get(0).getEvidence().get(0);
        assertThat(evidence.getEvidenceType().getTerm()).isEqualTo(TestData.EVIDENCE_LEVEL);
    }

    /**
     * Simple search for Phenotype, searches by disease name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simplePhenotypeSearch() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setPhenotype(TestData.PHENOTYPE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Simple search for Phenotype, using a non-existent, random disease name.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simplePhenotypeSearchNoFind() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setPhenotype(Utils.randomName());
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
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        ExternalIdentifier id = new ExternalIdentifier() ;
        id.setDatabase(TestData.FEATURE_DB);
        id.setIdentifier(TestData.FEATURE_DB_ID);
        id.setVersion(TestData.FEATURE_DB_VERSION);
        List<ExternalIdentifier> ids = new ArrayList<>() ;
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
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        ExternalIdentifier id = new ExternalIdentifier() ;
        id.setDatabase(TestData.PHENOTYPE_DB);
        id.setIdentifier(TestData.PHENOTYPE_DB_ID);
        id.setVersion(TestData.PHENOTYPE_DB_VERSION);
        List<ExternalIdentifier> ids = new ArrayList<>() ;
        ids.add(id) ;
        ExternalIdentifierQuery phenotype = new ExternalIdentifierQuery();
        phenotype.setIds(ids);
        request.setPhenotype(phenotype);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations()).isNotEmpty() ;
    }

    /**
     * Search for Evidence, using specific external identifier.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void simpleEvidenceSearchExternalIdentifier() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
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
        assertThat(evidenceObject.getEvidenceType().getTerm()).isEqualTo(TestData.EVIDENCE_LEVEL);
    }

    /**
     * Simple search for Feature, test page size = 1, expects a single result.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingOne() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setFeature(TestData.FEATURE_NAME);
        request.setPageSize(1);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response).isNotNull();
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations().size()).isEqualTo(1);
    }

    /**
     * Simple search for Feature, test page size = null, expects more than one result in the page.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingMore() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setFeature(TestData.FEATURE_NAME);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations().size()).isGreaterThan(1);
    }

    /**
     * Simple search for Feature, test page size = 1, find all, expects a single result per page.
     * @throws AvroRemoteException if there's an unanticipated error
     */
    @Test
    public void testGenotypePhenotypeSearchFeaturePagingAll() throws AvroRemoteException {
        SearchGenotypePhenotypeRequest request = SearchGenotypePhenotypeRequest.newBuilder().build();
        request.setFeature(TestData.FEATURE_NAME);
        request.setPageSize(1);
        SearchGenotypePhenotypeResponse response = client.genotypePhenotype.searchGenotypePhenotype(request);
        assertThat(response.getAssociations()).isNotNull() ;
        assertThat(response.getAssociations().size()).isEqualTo(1);
        while(response.getNextPageToken() != null) {
            System.err.println(response.getNextPageToken()) ;
            String previous_id = response.getAssociations().get(0).getId() ;
            request = SearchGenotypePhenotypeRequest.newBuilder().build();
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
