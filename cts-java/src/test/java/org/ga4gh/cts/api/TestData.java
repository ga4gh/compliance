package org.ga4gh.cts.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.ga4gh.models.Reference;
import org.ga4gh.models.ReferenceSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines important constants that pertain to and describe the official test data.
 * It contains no methods and you cannot instantiate it.
 *
 * @author Herb Jellinek
 */
public class TestData {

    /**
     * You can't instantiate one of these.
     */
    private TestData() {
    }

    /**
     * The default ID of the dataset that holds the test data.
     */
    public static final String DEFAULT_DATASET_ID = "compliance-dataset1";

    /**
     * The name of the Java system property that sets the name of the compliance dataset,
     * if the default (<tt>compliance-dataset1</tt>) is not correct.
     */
    private static final String DATASET_PROP_NAME = "ctk.tgt.dataset_id";

    /**
     * The names of the readgroup sets in the standard compliance dataset.
     */
    public static final String[] EXPECTED_READGROUPSETS_NAMES = {
            "1kg-low-coverage",
    };

    /**
     * The names of known-good read groups.
     */
    public static final SetMultimap<String, String> EXPECTED_READGROUPSET_READGROUP_NAMES =
            HashMultimap.create();

    static {
        EXPECTED_READGROUPSET_READGROUP_NAMES.putAll("1kg-low-coverage",
                                                     Arrays.asList("BRCA1_HG00096",
                                                                   "BRCA1_HG00099",
                                                                   "BRCA1_HG00101"));
    }

    /**
     * The names of all known {@link org.ga4gh.models.ReadGroup} objects, obtained from
     * {@link #EXPECTED_READGROUPSET_READGROUP_NAMES}.
     */
    public static final List<String> EXPECTED_READGROUP_NAMES =
            new ArrayList<>(EXPECTED_READGROUPSET_READGROUP_NAMES.values());

    /**
     * The names of all {@link ReferenceSet}s.
     */
    public static final String[] EXPECTED_REFERENCESET_NAMES = {
            "example_1"
    };

    /**
     * The names of all {@link Reference}s.
     */
    public static final String[] EXPECTED_REFERENCE_NAMES = {
            "example_1:simple",
    };

    /**
     * Return the ID of the compliance dataset on the server being tested.
     * By default this is the value of {@link #DEFAULT_DATASET_ID}, <tt>compliance-dataset1</tt>, but
     * it can be overridden by setting the Java property <tt>-Dctk.tgt.dataset_id</tt>.
     */
    public static String getDatasetId() {
        final String propValue = System.getProperty(DATASET_PROP_NAME);
        if (propValue != null) {
            return propValue;
        } else {
            return DEFAULT_DATASET_ID;
        }
    }

}
