package org.ga4gh.ctk.transport.protocols;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;

import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationSetsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationSetsResponse;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsRequest;
import ga4gh.AlleleAnnotationServiceOuterClass.SearchVariantAnnotationsResponse;
import ga4gh.AlleleAnnotations.VariantAnnotation;
import ga4gh.AlleleAnnotations.VariantAnnotationSet;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypeAssociationSetsRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypeAssociationSetsResponse;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchPhenotypesResponse;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeRequest;
import ga4gh.GenotypePhenotypeServiceOuterClass.SearchGenotypePhenotypeResponse;
import ga4gh.Metadata.Dataset;
import ga4gh.MetadataServiceOuterClass.SearchDatasetsRequest;
import ga4gh.MetadataServiceOuterClass.SearchDatasetsResponse;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadGroupSetsResponse;
import ga4gh.ReadServiceOuterClass.SearchReadsRequest;
import ga4gh.ReadServiceOuterClass.SearchReadsResponse;
import ga4gh.Reads.ReadAlignment;
import ga4gh.Reads.ReadGroup;
import ga4gh.Reads.ReadGroupSet;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.References.Reference;
import ga4gh.References.ReferenceSet;
import ga4gh.SequenceAnnotationServiceOuterClass;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeatureSetsRequest;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeatureSetsResponse;
import ga4gh.SequenceAnnotationServiceOuterClass.SearchFeaturesResponse;
import ga4gh.SequenceAnnotations.Feature;
import ga4gh.SequenceAnnotations.FeatureSet;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Variants.CallSet;
import ga4gh.Variants.Variant;
import ga4gh.Variants.VariantSet;
import ga4gh.BioMetadata.*;
import ga4gh.BioMetadataServiceOuterClass.SearchBioSamplesRequest;
import ga4gh.BioMetadataServiceOuterClass.SearchBioSamplesResponse;
import ga4gh.BioMetadataServiceOuterClass.SearchIndividualsRequest;
import ga4gh.BioMetadataServiceOuterClass.SearchIndividualsResponse;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.ctk.transport.protobuf.Get;
import org.ga4gh.ctk.transport.protobuf.Post;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides an interface to/facade for the GA4GH server we're testing.
 * <p>
 * Methods are categorized by functional area:
 * <ul>
 * <li>{@link #reads reads}</li>
 * <li>{@link #variants variants}</li>
 * <li>{@link #references references}</li>
 * <li>{@link #variantAnnotations variantAnnotations}</li>
 * <li>{@link #sequenceAnnotations sequenceAnnotations}</li>
* </ul>
 *
 * @author Herb Jellinek
 */
public class Client {

    private final URLMAPPING urls;

    public final WireTracker wireTracker;

    /**
     * Provides access to variants-related methods.  For example,
     * <pre>
     *     myClient.variants.searchVariantSets(...);
     * </pre>
     */
    public final Variants variants = new Variants();

    /**
     * Provides access to reads-related methods.  For example,
     * <pre>
     *     myClient.reads.searchReads(...);
     * </pre>
     */
    public final Reads reads = new Reads();

    /**
     * Provides access to references-related methods.  For example,
     * <pre>
     *     myClient.references.searchReferenceSets(...);
     * </pre>
     */
    public final References references = new References();


    /**
     * Provides access to GenotypePhenotype-related methods.  For example,
     * <pre>
     *     myClient.genotypePhenotype.searchReferenceSets(...);
     * </pre>
     */
    public final GenotypePhenotype genotypePhenotype = new GenotypePhenotype();


    /**
     * Provides access to variantannotations-related methods.  For example,
     * <pre>
     *     myClient.variantAnnotations.searchVariantAnnotations(...);
     * </pre>
     */
    public final VariantAnnotations variantAnnotations = new VariantAnnotations();

    /**
     * Provides access to sequenceannotations-related methods.  For example,
     * <pre>
     *     myClient.sequenceAnnotations.searchFeatures(...);
     * </pre>
     */
    public final SequenceAnnotations sequenceAnnotations = new SequenceAnnotations();

    /**
     * Provides access to metadata-related methods.  For example,
     * <pre>
     *     myClient.metadata.searchDatasets(...);
     * </pre>
     */

    public final Metadata metadata = new Metadata();


    public final BioMetadata bioMetadata = new BioMetadata();

    /**
     * Create a new client that can make requests on a GA4GH server.
     *
     * @param urls an URLMAPPING object that gives us the paths to use
     */
    public Client(URLMAPPING urls) {
        this(urls, null);
    }

    /**
     * Create a new client that can make requests on a GA4GH server.
     *
     * @param urls an URLMAPPING object that gives us the paths to use
     * @param wt   If not null, capture the data going across the wire
     */
    public Client(URLMAPPING urls, WireTracker wt) {
        this.urls = urls;
        wireTracker = wt;
    }


    /**
     * Inner class holding all metadata-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class Metadata {
        /**
         * Gets a list of datasets accessible through the API.
         * <tt>POST /datasets/search</tt> accepts a {@link SearchDatasetsRequest}
         * and returns a {@link SearchDatasetsResponse}.
         *
         * @param request the {@link SearchDatasetsRequest} request
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchDatasetsResponse searchDatasets(SearchDatasetsRequest request) throws UnirestException, InvalidProtocolBufferException, GAWrapperException {
            SearchDatasetsResponse.Builder responseBuilder = SearchDatasetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchDataSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Dataset} by ID.
         * <tt>GET /datasets/{id}</tt> returns a {@link Dataset}.
         *
         * @param id the ID of the dataset
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public Dataset getDataset(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Dataset.Builder responseBuilder = Dataset.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetDataSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }
    }

    /**
     * Inner class holding all variants-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class Variants {

        /**
         * Gets a list of {@link VariantSet} matching the search criteria via
         * <tt>POST /variantsets/search</tt>.
         *
         * @param request the {@link SearchVariantSetsRequest} we'll issue
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchVariantSetsResponse searchVariantSets(SearchVariantSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchVariantSetsResponse.Builder responseBuilder = SearchVariantSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchVariantSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link VariantSet} by ID.
         * <tt>GET /variantsets/{id}</tt> will return a JSON version of {@link VariantSet}.
         *
         * @param id the ID of the variant set
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public VariantSet getVariantSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            VariantSet.Builder responseBuilder = VariantSet.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetVariantSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link Variant} matching the search criteria.
         * <p>
         * <tt>POST /variants/search</tt> accepts a {@link SearchVariantsRequest}
         * and returns a {@link SearchVariantsResponse}.
         *
         * @param request the {@link SearchVariantsRequest} we'll issue
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchVariantsResponse searchVariants(SearchVariantsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchVariantsResponse.Builder responseBuilder = SearchVariantsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchVariants(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Variant} by ID.
         * <tt>GET /variants/{id}</tt> will return a {@link Variant}.
         *
         * @param id the ID of the variant
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public Variant getVariant(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Variant.Builder responseBuilder = Variant.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetVariant(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link CallSet}s matching the search criteria.
         * <p>
         * <tt>POST /callsets/search</tt> accepts a {@link SearchCallSetsRequest}
         * and returns a {@link SearchCallSetsResponse}.
         *
         * @param request the SearchCallSetsRequest we'll issue
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchCallSetsResponse searchCallSets(SearchCallSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchCallSetsResponse.Builder responseBuilder = SearchCallSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchCallSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link CallSet} by ID.
         * <tt>GET /callsets/{id}</tt> will return a {@link CallSet}.
         *
         * @param id the ID of the call set
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public CallSet getCallSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            CallSet.Builder responseBuilder = CallSet.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetCallSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }
    }

    /**
     * Inner class holding all reads-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class Reads {

        /**
         * Gets a list of {@link ReadAlignment} matching the search criteria.
         * <p>
         * <tt>POST /reads/search</tt> accepts a {@link SearchReadsRequest} and returns
         * a {@link SearchReadsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchReadsResponse searchReads(SearchReadsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReadsResponse.Builder responseBuilder = SearchReadsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchReads(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link ReadGroupSet} matching the search criteria.
         * <p>
         * <tt>POST /readgroupsets/search</tt> accepts a {@link SearchReadGroupSetsRequest}
         * and returns a {@link SearchReadGroupSetsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchReadGroupSetsResponse searchReadGroupSets(SearchReadGroupSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReadGroupSetsResponse.Builder responseBuilder = SearchReadGroupSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchReadGroupSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReadGroupSet} by ID.
         * <tt>GET /readgroupsets/{id}</tt> will return a JSON version of {@link ReadGroupSet}.
         *
         * @param id the ID of the read group set
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public ReadGroupSet getReadGroupSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReadGroupSet.Builder responseBuilder = ReadGroupSet.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetReadGroupSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReadGroup} by ID.
         * <tt>GET /readgroups/{id}</tt> will return a JSON version of {@link ReadGroup}.
         *
         * @param id the ID of the read group
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public ReadGroup getReadGroup(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReadGroup.Builder responseBuilder = ReadGroup.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getGetReadGroup(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }
    }

    /**
     * Inner class holding all references-related methods.  Gathering them in an inner class like
     * this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class References {

        /**
         * Gets a list of {@link ReferenceSet} matching the search criteria.
         * <p>
         * <tt>POST /referencesets/search</tt> accepts a {@link SearchReferenceSetsRequest}
         * and returns a {@link SearchReferenceSetsResponse}.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchReferenceSetsResponse searchReferenceSets(SearchReferenceSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReferenceSetsResponse.Builder responseBuilder = SearchReferenceSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchReferenceSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReferenceSet} by ID.
         * <tt>GET /referencesets/{id}</tt> returns a {@link ReferenceSet}.
         *
         * @param id the reference set ID
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public ReferenceSet getReferenceSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReferenceSet.Builder responseBuilder = ReferenceSet.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getReferenceSets(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link Reference} matching the search criteria.
         * <p>
         * <tt>POST /references/search</tt> accepts a {@link SearchReferencesRequest}
         * and returns a {@link SearchReferencesResponse}.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchReferencesResponse searchReferences(SearchReferencesRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReferencesResponse.Builder responseBuilder = SearchReferencesResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), urls.getSearchReferences(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Reference} by ID.
         * <tt>GET /references/{id}</tt> returns a {@link Reference}.
         *
         * @param id the reference set ID
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public Reference getReference(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Reference.Builder responseBuilder = Reference.newBuilder();
            new Get<>(urls.getUrlRoot(), urls.getReference(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Add <tt>key</tt> = <tt>value</tt> to the {@link Map} if <tt>value</tt> is not <tt>null</tt>.
         *
         * @param map   the Map into which we might insert
         * @param key   the key
         * @param value the value
         */
        private void putInMapIfValueNotNull(Map<String, Object> map,
                                            String key,
                                            Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        /**
         * Lists {@link Reference} bases by ID and optional range.
         * <tt>GET /references/{id}/bases</tt> returns a {@link ListReferenceBasesResponse}.
         *
         * @param id      the reference set ID
         * @param request Avro object to be serialized as JSON to the server
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public ListReferenceBasesResponse getReferenceBases(String id, ListReferenceBasesRequest request) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ListReferenceBasesResponse.Builder responseBuilder = ListReferenceBasesResponse.newBuilder();
            final Map<String, Object> params = new HashMap<>();
            putInMapIfValueNotNull(params, "start", request.getStart());
            putInMapIfValueNotNull(params, "end", request.getEnd());
            putInMapIfValueNotNull(params, "pageToken", request.getPageToken());
            new Get<>(urls.getUrlRoot(), urls.getSearchReferenceBases(), id, params, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }
    }

    /**
     * Inner class holding all sequence annotation-related methods.
     */
    public class SequenceAnnotations {
        public SearchFeatureSetsResponse searchFeatureSets(SearchFeatureSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchFeatureSets();
            SearchFeatureSetsResponse.Builder responseBuilder = SearchFeatureSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }


        public FeatureSet getFeatureSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            String path = urls.getGetFeatureSet();
            FeatureSet.Builder builder = FeatureSet.newBuilder();
            new Get<>(urls.getUrlRoot(), path, id, null, builder, wireTracker).performQuery();
            return builder.build();
        }

        public SearchFeaturesResponse searchFeatures(SequenceAnnotationServiceOuterClass.SearchFeaturesRequest request)
                throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchFeatures();
            SearchFeaturesResponse.Builder responseBuilder = SearchFeaturesResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        public Feature getFeature(String id) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getGetFeature();
            Feature.Builder builder = Feature.newBuilder();
            new Get<>(urls.getUrlRoot(), path, id, null, builder, wireTracker).performQuery();
            return builder.build();
        }

    }
    /**
     * Inner class holding all biodata-related methods.  Gathering them in an inner class like
     * this makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class BioMetadata {
        /**
         * Searches biosamples at the /biosamples/search endpoint using the given request.
         * @param request   A SearchBioSamples request
         * @return SearchBioSamplesResponse
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchBioSamplesResponse searchBiosamples(SearchBioSamplesRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchBioSamples();
            SearchBioSamplesResponse.Builder responseBuilder = SearchBioSamplesResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Get a biosample by ID by getting the /biosamples/id endpoint
         * @param id
         * @return BioSample
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public BioSample getBioSample(String id) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getGetBioSample();
            BioSample.Builder builder = BioSample.newBuilder();
            new Get<>(urls.getUrlRoot(), path, id, null, builder, wireTracker).performQuery();
            return builder.build();
        }

        /**
         * Searches individuals at the /individuals/search endpoint using the given request.
         * @param request   A SearchIndividuals request
         * @return SearchIndividualsResponse
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public SearchIndividualsResponse searchIndividuals(SearchIndividualsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchIndividuals();
            SearchIndividualsResponse.Builder responseBuilder = SearchIndividualsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Get an individual by ID by getting the /biosamples/id endpoint
         * @param id
         * @return Individual
         * @throws GAWrapperException if the server finds the request invalid in some way
         * @throws UnirestException if there's a problem speaking HTTP to the server
         * @throws InvalidProtocolBufferException if there's a problem processing the JSON response from the server
         */
        public Individual getIndividual(String id) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getGetIndividual();
            Individual.Builder builder = Individual.newBuilder();
            new Get<>(urls.getUrlRoot(), path, id, null, builder, wireTracker).performQuery();
            return builder.build();
        }
    }

    /**
     * Inner class holding all variant annotation-related methods.  Gathering them in an inner class like this makes it a little easier for someone writing
     * tests to use their IDE's auto-complete to type method names.
     */
    public class VariantAnnotations {

        /**
         * Gets a list of {@link VariantAnnotationSet} matching the search criteria via <tt>POST /variantannotationsets/search</tt>.
         *
         * @param request the {@link SearchVariantAnnotationSetsRequest} we'll issue
         */
        public SearchVariantAnnotationSetsResponse searchVariantAnnotationSets(SearchVariantAnnotationSetsRequest request)
                throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchVariantAnnotationSets();
            SearchVariantAnnotationSetsResponse.Builder responseBuilder = SearchVariantAnnotationSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link VariantAnnotationSet} by ID. <tt>GET /variantannotationsets/{id}</tt> will return a JSON version of {@link VariantAnnotationSet}.
         *
         * @param id the ID of the variant annotation set
         */
        public VariantAnnotationSet getVariantAnnotationSet(String id) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getGetVariantAnnotationSet();

            VariantAnnotationSet.Builder builder = VariantAnnotationSet.newBuilder();
            new Get<>(urls.getUrlRoot(), path, id, null, builder, wireTracker).performQuery();
            return builder.build();
        }

        /**
         * Gets a list of {@link VariantAnnotation} matching the search criteria. <p> <tt>POST /variantannotations/search</tt> accepts a {@link
         * SearchVariantAnnotationsRequest} and returns a {@link SearchVariantAnnotationsResponse}.
         *
         * @param request the {@link SearchVariantAnnotationsRequest} we'll issue
         */
        public SearchVariantAnnotationsResponse searchVariantAnnotations(SearchVariantAnnotationsRequest request)
                throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getSearchVariantAnnotations();
            SearchVariantAnnotationsResponse.Builder builder = SearchVariantAnnotationsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, builder, wireTracker).performQuery();
            return builder.build();
        }

    }

    /**
     * Inner class holding all variant genotype-phenotype methods.
     * Gathering them in an inner class like this makes it a little
     * easier for someone writing tests to use their IDE's
     * auto-complete to type method names.
     */
    public class GenotypePhenotype {
        /**
         * Gets a list of {@link Phenotype} matching the search criteria. <p> <tt>POST /phenotypes/search</tt> accepts a {@link
         * SearchPhenotypesRequest} and returns a {@link SearchPhenotypesResponse}.
         *
         * @param request the {@link SearchPhenotypesRequest} we'll issue
         */
        public SearchPhenotypesResponse searchPhenotypes(SearchPhenotypesRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getPhenotypes();
            SearchPhenotypesResponse.Builder builder = SearchPhenotypesResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, builder, wireTracker).performQuery();
            return builder.build();
        }

        public SearchGenotypePhenotypeResponse searchGenotypePhenotypes(SearchGenotypePhenotypeRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getPhenotypes();
            SearchGenotypePhenotypeResponse.Builder builder = SearchGenotypePhenotypeResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, builder, wireTracker).performQuery();
            return builder.build();
        }

        public SearchPhenotypeAssociationSetsResponse searchPhenotypeAssociationSets(SearchPhenotypeAssociationSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            String path = urls.getPhenotypes();
            SearchPhenotypeAssociationSetsResponse.Builder builder = SearchPhenotypeAssociationSetsResponse.newBuilder();
            new Post<>(urls.getUrlRoot(), path, request, builder, wireTracker).performQuery();
            return builder.build();
        }
    }
}
