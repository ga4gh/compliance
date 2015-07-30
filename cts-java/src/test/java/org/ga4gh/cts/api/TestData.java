package org.ga4gh.cts.api;

import org.ga4gh.models.Reference;
import org.ga4gh.models.ReferenceSet;

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
     * The ID of the dataset that holds the test data.
     */
    public static final String DATASET_ID = "compliance-dataset1";

    /**
     * The names of the readgroup sets in {@link #DATASET_ID}.
     */
    public static final String[] EXPECTED_READGROUPSETS_NAMES = {
            "compliance-dataset1:1kg-low-coverage",
            "compliance-dataset1:wgBam",
    };

    /**
     * The names of known-good read groups.
     */
    public static final String[] SOME_EXPECTED_READGROUP_NAMES = {
            "compliance-dataset1:wgBam:wgEncodeUwRepliSeqBg02esG1bAlnRep1_sample",
            "compliance-dataset1:wgBam:wgEncodeUwRepliSeqBg02esG2AlnRep1_sample",
            "compliance-dataset1:1kg-low-coverage:HG00096.mapped.ILLUMINA.bwa.GBR.low_coverage.20120522",
            "compliance-dataset1:1kg-low-coverage:HG00533.mapped.ILLUMINA.bwa.CHS.low_coverage.20120522",
            "compliance-dataset1:1kg-low-coverage:HG00534.mapped.ILLUMINA.bwa.CHS.low_coverage.20120522",
    };

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

}
