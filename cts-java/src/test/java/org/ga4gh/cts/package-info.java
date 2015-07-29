/**
 * <p>Tests for the system under test.</p>
 * <p>The subpackages of this package are:</p>
 * <ul>
 *     <li>api - the collection of API tests (each packaged in e.g., reads, references, variants, ...)</li>
 *     <li>transport - tests of the transport mechanisms (e.g., avro/json)</li>
 * </ul>
 * <p>At the top level of this package we have the TestSuites that execute
 * tests across the whole set of tests (e.g., to collect Work In Progress tests,
 * or all tests related to 'read'). This is simple at first but as test scenarios
 * evolve that cut across APIs, these grouping will become more useful.</p>
 *
 *
 * Created by Wayne Stidolph on 6/7/2015.
 */
package org.ga4gh.cts;