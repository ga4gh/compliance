package org.ga4gh.ctk.transport.protocols;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.*;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.WireTracker;
import org.ga4gh.ctk.transport.avrojson.AvroJson;

/**
 * This class provides an interface to/facade for the GA4GH server we're testing.
 * <p>
 *     Methods are categorized by functional area:
 *     <ul>
 *         <li>{@link #reads reads}</li>
 *         <li>{@link #variants variants}</li>
 *         <li>{@link #references references}</li>
 *     </ul>
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
     * Provides access to variants-related methods.  For example,
     * <pre>
     *     myClient.references.searchReferenceSets(...);
     * </pre>
     */
    public final References references = new References();

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

    ///
    //// From VariantsProtocolClient.
    ///

    public class Variants implements org.ga4gh.GAVariantMethods {

        /**
         * Gets a list of `GAVariantSet` matching the search criteria.
         * <p>
         * `POST /variantsets/search` must accept a JSON version of
         * `GASearchVariantSetsRequest` as the post body and will return a JSON version
         * of `GASearchVariantSetsResponse`.
         *
         * @param request the GASearchVariantSetsRequest we're issuing
         */
        @Override
        public GASearchVariantSetsResponse searchVariantSets(GASearchVariantSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariantSets();
            GASearchVariantSetsResponse response = new GASearchVariantSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchVariantSetsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of `GAVariantSet` matching the search criteria.
         * <p>
         * `POST /variantsets/search` must accept a JSON version of
         * `GASearchVariantSetsRequest` as the post body and will return a JSON version
         * of `GASearchVariantSetsResponse`.
         *
         * @param request the GASearchVariantSetsRequest we're issuing
         * @param wt      If supplied, captures the data going across the wire
         */
        public GASearchVariantSetsResponse searchVariantSets(GASearchVariantSetsRequest request,
                                                             WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariantSets(request);
        }

        /**
         * Gets a list of `GAVariant` matching the search criteria.
         * <p>
         * `POST /variants/search` must accept a JSON version of `GASearchVariantsRequest`
         * as the post body and will return a JSON version of `GASearchVariantsResponse`.
         *
         * @param request the GASearchVariantsRequest we're issuing
         */
        @Override
        public GASearchVariantsResponse searchVariants(GASearchVariantsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchVariants();
            GASearchVariantsResponse response = new GASearchVariantsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchVariantsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of `GAVariant` matching the search criteria.
         * <p>
         * `POST /variants/search` must accept a JSON version of `GASearchVariantsRequest`
         * as the post body and will return a JSON version of `GASearchVariantsResponse`.
         *
         * @param request the GASearchVariantsRequest we're issuing
         * @param wt      If supplied, captures the data going across the wire
         */

        public GASearchVariantsResponse searchVariants(GASearchVariantsRequest request,
                                                       WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchVariants(request);
        }

        /**
         * Gets a list of `GACallSet` matching the search criteria.
         * <p>
         * `POST /callsets/search` must accept a JSON version of `GASearchCallSetsRequest`
         * as the post body and will return a JSON version of `GASearchCallSetsResponse`.
         *
         * @param request the GASearchCallSetsRequest we're issuing
         */
        @Override
        public GASearchCallSetsResponse searchCallSets(GASearchCallSetsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchCallsets();
            GASearchCallSetsResponse response = new GASearchCallSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchCallSetsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of `GACallSet` matching the search criteria.
         * <p>
         * `POST /callsets/search` must accept a JSON version of `GASearchCallSetsRequest`
         * as the post body and will return a JSON version of `GASearchCallSetsResponse`.
         *
         * @param request the GASearchVariantsRequest we're issuing
         * @param wt      If supplied, captures the data going across the wire
         */
        public GASearchCallSetsResponse searchCallSets(GASearchCallSetsRequest request,
                                                       WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchCallSets(request);
        }
    }

    ///
    //// From ReadsProtocolClient.
    ///

    public class Reads implements org.ga4gh.GAReadMethods {

        /**
         * Gets a list of `GAReadAlignment` matching the search criteria.
         * <p>
         * `POST /reads/search` must accept a JSON version of `GASearchReadsRequest` as
         * the post body and will return a JSON version of `GASearchReadsResponse`.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException
         * @throws GAException
         */
        @Override
        public GASearchReadsResponse searchReads(GASearchReadsRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReads();
            GASearchReadsResponse response = new GASearchReadsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchReadsResponse)aj.doPostResp();
            return response;
        }

        /**
         * Gets a list of `GAReadAlignment` matching the search criteria.
         * <p>
         * `POST /reads/search` must accept a JSON version of `GASearchReadsRequest` as
         * the post body and will return a JSON version of `GASearchReadsResponse`.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         * @return the server's response (deserialized into an Avro-defined object)
         * @throws AvroRemoteException
         * @throws GAException
         */
        public GASearchReadsResponse searchReads(GASearchReadsRequest request, WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReads(request);
        }

        /**
         * Gets a list of `GAReadGroupSet` matching the search criteria.
         * <p>
         * `POST /readgroupsets/search` must accept a JSON version of
         * `GASearchReadGroupSetsRequest` as the post body and will return a JSON
         * version of `GASearchReadGroupSetsResponse`.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @throws AvroRemoteException
         * @throws GAException
         */
        @Override
        public GASearchReadGroupSetsResponse searchReadGroupSets(GASearchReadGroupSetsRequest
                                                                                 request)

                throws AvroRemoteException {
            String path = urls.getSearchReadGroupSets();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            GASearchReadGroupSetsResponse response = new GASearchReadGroupSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            //aj.setDeserMode(AvroJson.DESER_MODE.AVRO_DIRECT);
            response = (GASearchReadGroupSetsResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a list of `GAReadGroupSet` matching the search criteria.
         * <p>
         * `POST /readgroupsets/search` must accept a JSON version of
         * `GASearchReadGroupSetsRequest` as the post body and will return a JSON
         * version of `GASearchReadGroupSetsResponse`.</p>
         *
         * @param request filled-in Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         * @throws AvroRemoteException
         * @throws GAException
         */
        public GASearchReadGroupSetsResponse searchReadGroupSets(GASearchReadGroupSetsRequest
                                                                         request, WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReadGroupSets(request);
        }
    }

    ///
    //// Reference methods.
    ///
    public class References implements org.ga4gh.GAReferenceMethods {

        /**
         * Gets a list of `GAReferenceSet` matching the search criteria.
         * <p>
         * `POST /referencesets/search` must accept a JSON version of
         * `GASearchReferenceSetsRequest` as the post body and will return a JSON
         * version of `GASearchReferenceSetsResponse`.
         *
         * @param request Avro object to be serialized as JSON to the server
         */
        @Override
        public GASearchReferenceSetsResponse searchReferenceSets(GASearchReferenceSetsRequest
                                                                                 request)
                throws AvroRemoteException {
            String path = urls.getSearchReferencesets();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            GASearchReferenceSetsResponse response = new GASearchReferenceSetsResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchReferenceSetsResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a `GAReferenceSet` by ID.
         * `GET /referencesets/{id}` will return a JSON version of `GAReferenceSet`.
         *
         * @param id the reference set ID
         */
        @Override
        public GAReferenceSet getReferenceSet(String id) throws AvroRemoteException {
            String path = urls.getReferenceSets();
            GAReferenceSet response = new GAReferenceSet();
            final AvroJson aj = new AvroJson<>(response, urls.getUrlRoot(), path);
            response = (GAReferenceSet)aj.doGetResp(id);
            return response;
        }

        /**
         * Gets a list of `GAReferenceSet` matching the search criteria.
         * <p>
         * `POST /referencesets/search` must accept a JSON version of
         * `GASearchReferenceSetsRequest` as the post body and will return a JSON
         * version of `GASearchReferenceSetsResponse`.
         *
         * @param request Avro object to be serialized as JSON to the server
         * @param wt      If supplied, captures the data going across the wire
         */
        public GASearchReferenceSetsResponse searchReferenceSets(GASearchReferenceSetsRequest
                                                                         request, WireTracker wt)
                throws AvroRemoteException {
            wireTracker = wt;
            return searchReferenceSets(request);
        }

        /**
         * Gets a `GAReferenceSet` by ID.
         * `GET /referencesets/{id}` will return a JSON version of `GAReferenceSet`.
         *
         * @param id the reference set ID
         * @param wt If supplied, captures the data going across the wire
         */
        public GAReferenceSet getReferenceSet(String id, WireTracker wt) throws AvroRemoteException {
            wireTracker = wt;
            return getReferenceSet(id);
        }

        /**
         * Gets a list of `GAReference` matching the search criteria.
         * <p>
         * `POST /references/search` must accept a JSON version of
         * `GASearchReferencesRequest` as the post body and will return a JSON
         * version of `GASearchReferencesResponse`.
         *
         * @param request Avro object to be serialized as JSON to the server
         */
        @Override
        public GASearchReferencesResponse searchReferences(GASearchReferencesRequest request)
                throws AvroRemoteException {

            String path = urls.getSearchReferences();
            GASearchReferencesResponse response = new GASearchReferencesResponse();
            final AvroJson aj =
                    new AvroJson<>(request, response, urls.getUrlRoot(), path, wireTracker);
            response = (GASearchReferencesResponse)aj.doPostResp();

            return response;
        }

        /**
         * Gets a `GAReference` by ID.
         * `GET /references/{id}` will return a JSON version of `GAReference`.
         *
         * @param id the reference set ID
         */
        @Override
        public GAReference getReference(String id) throws AvroRemoteException {
            String path = urls.getReference();
            // we use an empty concrete response class to pass into the Parameterized AvroJson
            // as a quick way to get the class name and such; this object actually gets replaced
            // with the filled-in Response object constructed in AvroJson and passed back
            GAReference response = new GAReference();
            final AvroJson aj =
                    new AvroJson<>(response, urls.getUrlRoot(), path, wireTracker);
            response = (GAReference)aj.doGetResp(id);

            return response;
        }

        /**
         * Lists `GAReference` bases by ID and optional range.
         * `GET /references/{id}/bases` will return a JSON version of
         * `GAListReferenceBasesResponse`.
         *
         * @param id      the reference set ID
         * @param request Avro object to be serialized as JSON to the server
         */
        @Override
        public GAListReferenceBasesResponse getReferenceBases(String id,
                                                              GAListReferenceBasesRequest request)
                throws AvroRemoteException {
            String path = urls.getSearchReferenceBases();
            GAListReferenceBasesResponse response = new GAListReferenceBasesResponse();
            final AvroJson aj =
                    new AvroJson<>(response, urls.getUrlRoot(), path, wireTracker);
            response = (GAListReferenceBasesResponse)aj.doGetResp(id);

            return response;
        }
    }
}
