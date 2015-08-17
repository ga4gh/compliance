/**
 * <p>Provide test-support services.</p>
 * <p>These services are used by tests to get information
 * about test activity and about the underlying ctk-schema-driven domain.</p>
 * <ul>
 *     <li>TrafficLogService: collect and supply information about the
 *     activity of the tests themselves (e.g., TrafficLog summaries)</li>
 *     <l>DomainInformationService: collect and supply information about the
 *     underlying domain (e.g., names of Request objects or defined endpoints.
 *     This is intentionally file-driven so that test/API developers can modify
 *     the domain expectations without recompiling the CTK. During CTK build
 *     of distributions (CLI, Server) the files are generated and copied from
 *     their sourceclocations into the lib/ directory, and are re-read on every test run:
 *     <ul>
 *         <li>ctk-transport/src/main/resources/defaulttransport.properties</li>
 *         <li>ctk-domain/src/main/resources/avro-types.json</li>
 *     </ul>
 * </ul>
 * <p>This file-driven nature may require some configuration of IDEs to run the
 * tests that depend on these services. The easiest approach is probably to
 * have the IDE test-runner control the CTK properties:</p>
 * <ul>
 *     <li>ctk.defaulttransportfile: identifies name and location of the properties
 *     file indicating mapping from CTK target methods to target server endpoint URLS
 *     (normally in lib/defaulttransport.properties); file is normal properties with
 *     empty values to indicate a reserved by unused endpoint</li>
 *     <li>ctk.domaindatatypesfile: control location and/or name of the domain data types file
 *     (normally 'avro-types.json'); file sis simple JSON array of fully-qulaified data type names</li>
 * </ul>
 * Created by Wayne Stidolph on 8/5/2015.
 */
package org.ga4gh.ctk.services;