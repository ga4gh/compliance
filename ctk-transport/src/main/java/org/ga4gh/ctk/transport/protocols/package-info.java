/**
 * <p>Provide test-oriented communication to Server.</p>
 *
 * <p>This package currently provides the  communications between the tests
 * and the Server's JSON/HTTP endpoints (with messages and data elements as defined
 * in the Avro IDL).</p>
 *
 * <p>(There is an URLMAPPING interface for access to static strings defining
 * Server endpoints (as extracted from the IDL comments).</p>
 *
 * <p>This package provides RespCode as an enumeration/abstraction for the HTTP response
 * code integers. This class will provide GA4GH-specific comparators and trackers. This
 * is intended to be additional isolation from the HTTP layer in case the GA4GH effort
 * moves away from JSON/HTTP to a binary protocol while still needing to track communications
 * success/fail states.</p>
 *
 * <p>The {@link org.ga4gh.ctk.transport.protocols.Client} class implements
 * the messages defined in the IDL and presents a pure Java interface
 * for test classes to use.  Most such methods take a single Request object and return
 * a single Response object; some take a simple parameter (e.g. a {@link java.lang.String} instead
 * of a Request object.  Each endpoint method also has an overloaded version which
 * takes an additional WireTracker object the test can supply. If present,
 * the WireTracker captures the details of the wire communications such as actual JSON
 * strings, response code, timing.</p>
 *
 * <p>Created by Wayne Stidolph on 6/9/2015.</p>
 */
package org.ga4gh.ctk.transport.protocols;