/**
 * <p>Provide persistence support for the CTK test runs.</p>
 * <p>There is one domain entity here, the TrafficLog. This is
 * a simple data access object, with fields to track the various
 * aspects of a single CTK/target Request/Response interaction.
 * The data element is then stored and retrieved by the
 * TestActivityDataService; this way, a test can "look back" over
 * the activity by querying the TestActivityDataService.</p>
 * <p>This package also provides the:</p>
 * <ul>
 * <li>TrafficLogBuilder (for a builder-style
 * interface for making a TrafficLog, which is easier in an IDE)</li>
 * <li>TrafficLogRepository, a interface to define CRUD and custom query
 * operations for a JPA datastore, which is normally supplied by Spring Data
 * JPA via a embedded H2; the default configuration for this datastore is
 * 'dev' mode, so the data is dropped at the end of a session. However, this
 * could be reconfigured to use a stable 'production' datastore if long term
 * or unified storage of the TrafficLog data items were desirable.</li>
 * </ul>
 * Created by Wayne Stidolph on 7/24/2015.
 */
package org.ga4gh.ctk.domain;