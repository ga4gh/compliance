package org.ga4gh.ctk.transport.protocols;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.ctk.transport.avrojson.AvroJson;
import org.ga4gh.methods.*;
import org.ga4gh.models.*;

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
* </ul>
 *
 * @author Herb Jellinek
 */
public class Client {

    private final URLMAPPING urls;

    public WireTracker wireTracker = null;

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
     * Provides access to variantannotations-related methods.  For example,
     * <pre>
     *     myClient.variantAnnotations.searchVariantAnnotations(...);
     * </pre>
     */
    public final VariantAnnotations variantAnnotations = new VariantAnnotations();


    /**
     * Create a new client that can make requests on a GA4GH server.
     *
     * @param urls an URLMAPPING object that gives us the paths to use
     */
    public Client(URLMAPPING urls) {
        this.urls = urls;
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
     * Inner class holding all variants-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class Variants implements VariantMethods {

        /**
         * Gets a list of {@link VariantSet} matching the search criteria via
         * <tt>POST /variantsets/search</tt>.
         *
         * @param request the {@link SearchVariantSetsRequest} we'll issue
         */
        @Override
        public SearchVariantSetsResponse searchVariantSets(SearchVariantSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariantSets();
            SearchVariantSetsResponse response = new SearchVariantSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchVariantSetsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a {@link VariantSet} by ID.
         * <tt>GET /variantsets/{id}</tt> will return a JSON version of {@link VariantSet}.
         *
         * @param id the ID of the variant set
         */
        @Override
        public VariantSet getVariantSet(String id) throws AvroRemoteException {
            String path = urls.getGetVariantSet();
            VariantSet response = new VariantSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (VariantSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link VariantSet} matching the search criteria.
         * <p>
         * <tt>POST /variantsets/search</tt> accepts a {@link SearchVariantSetsRequest}
         * as the post body and returns a {@link SearchVariantSetsResponse}.
         *
         * @param request the SearchVariantSetsRequest we'll issue
         * @param wt      If supplied, captures the data going across the wire
         */
        public SearchVariantSetsResponse searchVariantSets(SearchVariantSetsRequest request,
                                                           WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariantSets(request);
        }

        /**
         * Gets a list of {@link Variant} matching the search criteria.
         * <p>
         * <tt>POST /variants/search</tt> accepts a {@link SearchVariantsRequest}
         * and returns a {@link SearchVariantsResponse}.
         *
         * @param request the {@link SearchVariantsRequest} we'll issue
         */
        @Override
        public SearchVariantsResponse searchVariants(SearchVariantsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariants();
            SearchVariantsResponse response = new SearchVariantsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchVariantsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a {@link Variant} by ID.
         * <tt>GET /variants/{id}</tt> will return a {@link Variant}.
         *
         * @param id the ID of the variant
         */
        @Override
        public Variant getVariant(String id) throws AvroRemoteException {
            String path = urls.getGetVariant();
            Variant response = new Variant();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (Variant)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link Variant} matching the search criteria.
         * <p>
         * <tt>POST /variants/search</tt> accepts a {@link SearchVariantsRequest}
         * and returns a {@link SearchVariantsResponse}.
         *
         * @param request the SearchVariantsRequest we'll issue
         * @param wt      If supplied, captures the data going across the wire
         */

        public SearchVariantsResponse searchVariants(SearchVariantsRequest request,
                                                     WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariants(request);
        }

        /**
         * Gets a list of {@link CallSet}s matching the search criteria.
         * <p>
         * <tt>POST /callsets/search</tt> accepts a {@link SearchCallSetsRequest}
         * and returns a {@link SearchCallSetsResponse}.
         *
         * @param request the SearchCallSetsRequest we'll issue
         */
        @Override
        public SearchCallSetsResponse searchCallSets(SearchCallSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchCallsets();
            SearchCallSetsResponse response = new SearchCallSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchCallSetsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a {@link CallSet} by ID.
         * <tt>GET /callsets/{id}</tt> will return a {@link CallSet}.
         *
         * @param id the ID of the call set
         */
        @Override
        public CallSet getCallSet(String id) throws AvroRemoteException {
            String path = urls.getGetCallset();
            CallSet response = new CallSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (CallSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link CallSet} objects matching the search criteria.
         * <p>
         * <tt>POST /callsets/search</tt> accepts a {@link SearchCallSetsRequest} and
         * returns a {@link SearchCallSetsResponse}.
         *
         * @param request the SearchVariantsRequest we'll issue
         * @param wt      If supplied, captures the data going across the wire
         */
        public SearchCallSetsResponse searchCallSets(SearchCallSetsRequest request,
                                                     WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchCallSets(request);
        }
    }

    /**
     * Inner class holding all reads-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class Reads implements ReadMethods {

        /**
         * Gets a list of {@link ReadAlignment} matching the search criteria.
         * <p>
         * <tt>POST /reads/search</tt> accepts a {@link SearchReadsRequest} and returns
         * a {@link SearchReadsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public SearchReadsResponse searchReads(SearchReadsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReads();
            SearchReadsResponse response = new SearchReadsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchReadsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of {@link ReadAlignment} matching the search criteria.
         * <p>
         * <tt>POST /reads/search</tt> accepts a {@link SearchReadsRequest} and returns
         * a {@link SearchReadsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         * @return the server's response (deserialized into an Avro-defined object)
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReadsResponse searchReads(SearchReadsRequest request, WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReads(request);
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
        @Override
        public SearchReadGroupSetsResponse searchReadGroupSets(SearchReadGroupSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReadGroupSets();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            SearchReadGroupSetsResponse response = new SearchReadGroupSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            //aj.setDeserMode(AvroJson.DESER_MODE.AVRO_DIRECT);
            response = (SearchReadGroupSetsResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a {@link ReadGroupSet} by ID.
         * <tt>GET /readgroupsets/{id}</tt> will return a JSON version of {@link ReadGroupSet}.
         *
         * @param id the ID of the read group set
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public ReadGroupSet getReadGroupSet(String id) throws AvroRemoteException {
            String path = urls.getGetReadGroupSet();
            ReadGroupSet response = new ReadGroupSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (ReadGroupSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a {@link ReadGroup} by ID.
         * <tt>GET /readgroups/{id}</tt> will return a JSON version of {@link ReadGroup}.
         *
         * @param id the ID of the read group
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public ReadGroup getReadGroup(String id) throws AvroRemoteException {
            String path = urls.getGetReadGroup();
            ReadGroup response = new ReadGroup();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (ReadGroup)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of datasets accessible through the API.
         * <tt>POST /datasets/search</tt> accepts a {@link SearchDatasetsRequest}
         * and returns a {@link SearchDatasetsResponse}.
         *
         * @param request the {@link SearchDatasetsRequest} request
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public SearchDatasetsResponse searchDatasets(SearchDatasetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchDatasets();
            SearchDatasetsResponse response = new SearchDatasetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchDatasetsResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a {@link Dataset} by ID.
         * <tt>GET /datasets/{id}</tt> returns a {@link Dataset}.
         *
         * @param id the ID of the dataset
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public Dataset getDataset(String id) throws AvroRemoteException {
            String path = urls.getGetDataset();
            Dataset response = new Dataset();
            final AvroJson aj =
                    new AvroJson<>(response, urls.getUrlRoot(), path, wireTracker);
            response = (Dataset)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link ReadGroupSet} matching the search criteria.
         * <p>
         * <tt>POST /readgroupsets/search</tt> accepts a {@link SearchReadGroupSetsRequest}
         * and returns a {@link SearchReadGroupSetsResponse}.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReadGroupSetsResponse searchReadGroupSets(SearchReadGroupSetsRequest
                                                                       request, WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReadGroupSets(request);
        }
    }

    /**
     * Inner class holding all references-related methods.  Gathering them in an inner class like
     * this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class References implements ReferenceMethods {

        /**
         * Gets a list of {@link ReferenceSet} matching the search criteria.
         * <p>
         * <tt>POST /referencesets/search</tt> accepts a {@link SearchReferenceSetsRequest}
         * and returns a {@link SearchReferenceSetsResponse}.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public SearchReferenceSetsResponse searchReferenceSets(SearchReferenceSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReferencesets();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            SearchReferenceSetsResponse response = new SearchReferenceSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchReferenceSetsResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a {@link ReferenceSet} by ID.
         * <tt>GET /referencesets/{id}</tt> returns a {@link ReferenceSet}.
         *
         * @param id the reference set ID
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public ReferenceSet getReferenceSet(String id) throws AvroRemoteException {
            String path = urls.getReferenceSets();
            ReferenceSet response = new ReferenceSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (ReferenceSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link ReferenceSet} matching the search criteria.
         * <p>
         * <tt>POST /referencesets/search</tt> accepts a
         * {@link SearchReferenceSetsRequest} and returns a {@link SearchReferenceSetsResponse}.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         * @throws AvroRemoteException if there's a communication problem
         */
        public SearchReferenceSetsResponse searchReferenceSets(SearchReferenceSetsRequest request,
                                                               WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReferenceSets(request);
        }

        /**
         * Gets a {@link ReferenceSet} by ID.
         * <tt>GET /referencesets/{id}</tt> returns a {@link ReferenceSet}.
         *
         * @param id the reference set ID
         * @param wt If supplied, captures the data going across the wire
         * @throws AvroRemoteException if there's a communication problem
         */
        public ReferenceSet getReferenceSet(String id, WireTracker wt) throws AvroRemoteException {
            wireTracker = wt;
            return getReferenceSet(id);
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
        @Override
        public SearchReferencesResponse searchReferences(SearchReferencesRequest request)
                throws AvroRemoteException {

            String path = urls.getSearchReferences();
            SearchReferencesResponse response = new SearchReferencesResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchReferencesResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a {@link Reference} by ID.
         * <tt>GET /references/{id}</tt> returns a {@link Reference}.
         *
         * @param id the reference set ID
         * @throws AvroRemoteException if there's a communication problem
         */
        @Override
        public Reference getReference(String id) throws AvroRemoteException {
            String path = urls.getReference();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            Reference response = new Reference();
            final AvroJson aj =
                    new AvroJson<>(response, urls.getUrlRoot(), path, wireTracker);
            response = (Reference)aj.doGetResp(id);

            return response;
        }

        /**
         * Add <tt>key</tt> = <tt>value</tt> to the {@link Map} if <tt>value</tt> is not <tt>null</tt>.
         * @param map the Map into which we might insert
         * @param key the key
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
        @Override
        public ListReferenceBasesResponse getReferenceBases(String id,
                                                            ListReferenceBasesRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReferenceBases();
            ListReferenceBasesResponse response = new ListReferenceBasesResponse();
            final AvroJson aj =
                    new AvroJson<>(response, urls.getUrlRoot(), path, wireTracker);
            // collect query params from request
            final Map<String, Object> params = new HashMap<>();
            putInMapIfValueNotNull(params, "start", request.getStart());
            putInMapIfValueNotNull(params, "end", request.getEnd());
            putInMapIfValueNotNull(params, "pageToken", request.getPageToken());
            response = (ListReferenceBasesResponse)aj.doGetResp(id, params);

            return response;
        }
    }

    /**
     * Inner class holding all variant annotation-related methods.  Gathering them in an inner class like this
     * makes it a little easier for someone writing tests to use their IDE's auto-complete
     * to type method names.
     */
    public class VariantAnnotations implements AlleleAnnotationMethods {

        /**
         * Gets a list of {@link VariantAnnotationSet} matching the search criteria via
         * <tt>POST /variantannotationsets/search</tt>.
         *
         * @param request the {@link SearchVariantAnnotationSetsRequest} we'll issue
         */
        @Override
        public SearchVariantAnnotationSetsResponse searchVariantAnnotationSets(SearchVariantAnnotationSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariantAnnotationSets();
            SearchVariantAnnotationSetsResponse response = new SearchVariantAnnotationSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchVariantAnnotationSetsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a {@link VariantAnnotationSet} by ID.
         * <tt>GET /variantannotationsets/{id}</tt> will return a JSON version of {@link VariantAnnotationSet}.
         *
         * @param id the ID of the variant annotation set
         */
        @Override
        public VariantAnnotationSet getVariantAnnotationSet(String id) throws AvroRemoteException {
            String path = urls.getGetVariantAnnotationSet();
            VariantAnnotationSet response = new VariantAnnotationSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (VariantAnnotationSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of {@link VariantAnnotationSet} matching the search criteria.
         * <p>
         * <tt>POST /variantannotationsets/search</tt> accepts a {@link SearchAnnotationVariantSetsRequest}
         * as the post body and returns a {@link SearchVariantAnnotationSetsResponse}.
         *
         * @param request the SearchVariantAnnotationSetsRequest we'll issue
         * @param wt      If supplied, captures the data going across the wire
         */
        public SearchVariantAnnotationSetsResponse searchVariantAnnotationSets(SearchVariantAnnotationSetsRequest request,
                                                           WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariantAnnotationSets(request);
        }

        /**
         * Gets a list of {@link VariantAnnotation} matching the search criteria.
         * <p>
         * <tt>POST /variantannotations/search</tt> accepts a {@link SearchVariantAnnotationsRequest}
         * and returns a {@link SearchVariantAnnotationsResponse}.
         *
         * @param request the {@link SearchVariantAnnotationsRequest} we'll issue
         */
        @Override
        public SearchVariantAnnotationsResponse searchVariantAnnotations(SearchVariantAnnotationsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariantAnnotations();
            SearchVariantAnnotationsResponse response = new SearchVariantAnnotationsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (SearchVariantAnnotationsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of {@link VariantAnnotation} matching the search criteria.
         * <p>
         * <tt>POST /variantannotations/search</tt> accepts a {@link SearchVariantAnnotationsRequest}
         * and returns a {@link SearchVariantAnnotationsResponse}.
         *
         * @param request the SearchVariantAnnotationsRequest we'll issue
         * @param wt      If supplied, captures the data going across the wire
         */

        public SearchVariantAnnotationsResponse searchVariantAnnotations(SearchVariantAnnotationsRequest request,
                                                     WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariantAnnotations(request);
        }
  }


}
