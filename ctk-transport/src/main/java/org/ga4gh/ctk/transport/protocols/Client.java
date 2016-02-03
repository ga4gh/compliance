package org.ga4gh.ctk.transport.protocols;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.ga4gh.ctk.transport.GAWrapperException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import ga4gh.MetadataServiceOuterClass.*;
import ga4gh.Metadata.*;
import ga4gh.VariantServiceOuterClass.*;
import ga4gh.Variants.*;
import ga4gh.ReadServiceOuterClass.*;
import ga4gh.Reads.*;
import ga4gh.ReferenceServiceOuterClass.*;
import ga4gh.References.*;
import org.ga4gh.ctk.transport.protobuf.Get;
import org.ga4gh.ctk.transport.protobuf.Post;

import java.io.IOException;
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
     * Provides access to variants-related methods.  For example,
     * <pre>
     *     myClient.references.searchReferenceSets(...);
     * </pre>
     */
    public final References references = new References();

    /**
     * Provides access to metadata-related methods.  For example,
     * <pre>
     *     myClient.metadata.searchDatasets(...);
     * </pre>
     */
    public final Metadata metadata = new Metadata();

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
         * @throws IOException if there's a communication problem
         */
        public SearchDatasetsResponse searchDatasets(SearchDatasetsRequest request) throws UnirestException, InvalidProtocolBufferException, GAWrapperException {
            SearchDatasetsResponse.Builder responseBuilder = SearchDatasetsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchDataSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Dataset} by ID.
         * <tt>GET /datasets/{id}</tt> returns a {@link Dataset}.
         *
         * @param id the ID of the dataset
         * @throws AvroRemoteException if there's a communication problem
         */
        public Dataset getDataset(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Dataset.Builder responseBuilder = Dataset.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetDataSet(), id, null, responseBuilder, wireTracker).performQuery();
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
         */
        public SearchVariantSetsResponse searchVariantSets(SearchVariantSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchVariantSetsResponse.Builder responseBuilder = SearchVariantSetsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchVariantSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link VariantSet} by ID.
         * <tt>GET /variantsets/{id}</tt> will return a JSON version of {@link VariantSet}.
         *
         * @param id the ID of the variant set
         */
        public VariantSet getVariantSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            VariantSet.Builder responseBuilder = VariantSet.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetVariantSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link Variant} matching the search criteria.
         * <p>
         * <tt>POST /variants/search</tt> accepts a {@link SearchVariantsRequest}
         * and returns a {@link SearchVariantsResponse}.
         *
         * @param request the {@link SearchVariantsRequest} we'll issue
         */
        public SearchVariantsResponse searchVariants(SearchVariantsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchVariantsResponse.Builder responseBuilder = SearchVariantsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchVariants(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Variant} by ID.
         * <tt>GET /variants/{id}</tt> will return a {@link Variant}.
         *
         * @param id the ID of the variant
         */
        public Variant getVariant(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Variant.Builder responseBuilder = Variant.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetVariant(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link CallSet}s matching the search criteria.
         * <p>
         * <tt>POST /callsets/search</tt> accepts a {@link SearchCallSetsRequest}
         * and returns a {@link SearchCallSetsResponse}.
         *
         * @param request the SearchCallSetsRequest we'll issue
         */
        public SearchCallSetsResponse searchCallSets(SearchCallSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchCallSetsResponse.Builder responseBuilder = SearchCallSetsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchCallSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link CallSet} by ID.
         * <tt>GET /callsets/{id}</tt> will return a {@link CallSet}.
         *
         * @param id the ID of the call set
         */
        public CallSet getCallSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            CallSet.Builder responseBuilder = CallSet.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetCallSet(), id, null, responseBuilder, wireTracker).performQuery();
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
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReadsResponse searchReads(SearchReadsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReadsResponse.Builder responseBuilder = SearchReadsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchReads(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link ReadGroupSet} matching the search criteria.
         * <p>
         * <tt>POST /readgroupsets/search</tt> accepts a {@link SearchReadGroupSetsRequest}
         * and returns a {@link SearchReadGroupSetsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReadGroupSetsResponse searchReadGroupSets(SearchReadGroupSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReadGroupSetsResponse.Builder responseBuilder = SearchReadGroupSetsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchReadGroupSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReadGroupSet} by ID.
         * <tt>GET /readgroupsets/{id}</tt> will return a JSON version of {@link ReadGroupSet}.
         *
         * @param id the ID of the read group set
         * @throws AvroRemoteException if there's a communication problem
         */
        public ReadGroupSet getReadGroupSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReadGroupSet.Builder responseBuilder = ReadGroupSet.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetReadGroupSet(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReadGroup} by ID.
         * <tt>GET /readgroups/{id}</tt> will return a JSON version of {@link ReadGroup}.
         *
         * @param id the ID of the read group
         * @throws AvroRemoteException if there's a communication problem
         */
        public ReadGroup getReadGroup(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReadGroup.Builder responseBuilder = ReadGroup.newBuilder();
            new Get(urls.getUrlRoot(), urls.getGetReadGroup(), id, null, responseBuilder, wireTracker).performQuery();
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
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReferenceSetsResponse searchReferenceSets(SearchReferenceSetsRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReferenceSetsResponse.Builder responseBuilder = SearchReferenceSetsResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchReferenceSets(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link ReferenceSet} by ID.
         * <tt>GET /referencesets/{id}</tt> returns a {@link ReferenceSet}.
         *
         * @param id the reference set ID
         * @throws AvroRemoteException if there's a communication problem
         */
        public ReferenceSet getReferenceSet(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ReferenceSet.Builder responseBuilder = ReferenceSet.newBuilder();
            new Get(urls.getUrlRoot(), urls.getReferenceSets(), id, null, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a list of {@link Reference} matching the search criteria.
         * <p>
         * <tt>POST /references/search</tt> accepts a {@link SearchReferencesRequest}
         * and returns a {@link SearchReferencesResponse}.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReferencesResponse searchReferences(SearchReferencesRequest request) throws InvalidProtocolBufferException, GAWrapperException, UnirestException {
            SearchReferencesResponse.Builder responseBuilder = SearchReferencesResponse.newBuilder();
            new Post(urls.getUrlRoot(), urls.getSearchReferences(), request, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }

        /**
         * Gets a {@link Reference} by ID.
         * <tt>GET /references/{id}</tt> returns a {@link Reference}.
         *
         * @param id the reference set ID
         * @throws AvroRemoteException if there's a communication problem
         */
        public Reference getReference(String id) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            Reference.Builder responseBuilder = Reference.newBuilder();
            new Get(urls.getUrlRoot(), urls.getReference(), id, null, responseBuilder, wireTracker).performQuery();
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
         * @throws AvroRemoteException if there's a communication problem
         */
        public ListReferenceBasesResponse getReferenceBases(String id, ListReferenceBasesRequest request) throws InvalidProtocolBufferException, UnirestException, GAWrapperException {
            ListReferenceBasesResponse.Builder responseBuilder = ListReferenceBasesResponse.newBuilder();
            final Map<String, Object> params = new HashMap<>();
            putInMapIfValueNotNull(params, "start", request.getStart());
            putInMapIfValueNotNull(params, "end", request.getEnd());
            putInMapIfValueNotNull(params, "pageToken", request.getPageToken());
            new Get(urls.getUrlRoot(), urls.getSearchReferenceBases(), id, params, responseBuilder, wireTracker).performQuery();
            return responseBuilder.build();
        }
    }

}
