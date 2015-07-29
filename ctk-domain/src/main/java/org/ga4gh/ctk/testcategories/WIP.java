package org.ga4gh.ctk.testcategories;

/**
 * <p>Marker interface for tests which are somehow "work in progress"
 * (whether that's the test itself, or thing being tested, is up to you).</p>
 * <p>This is a handy test-tag; when you're working on something, add
 * the WIP category tag {@code @Categories(WIP.class)} to the test class or
 * test method signature, and then the WIPTestSuite will pick up only your tagged tests.</p>
 * <p>If no tests are WIP-categorized, you'll get a {@code NoTestsRemainException}
 * from the WIPTestSuite execution</p>
 *
 * <h2>Remove this tag when all your tests pass!</h2>
 *
 * Created by Wayne Stidolph on 6/7/2015.
 */
public interface WIP { /* category marker */
}
