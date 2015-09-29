package org.ga4gh.ctk.transport.avrojson;

import org.ga4gh.ctk.transport.testcategories.AvroTests;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * <p>Tests closely connected to Avro behavior. Putting the Suite
 * in 'systests' for the moment so it's reassuringly in the report,
 * but probably belongs outside systests, as a CTK unit test</p>
 * <p>Created by Wayne Stidolph on 6/7/2015.</p>
 */
@RunWith(Categories.class)
@Categories.IncludeCategory(AvroTests.class)
@Suite.SuiteClasses({AvroMakerTest.class, JsonMakerTest.class})
public class AvroTestSuite {
}
