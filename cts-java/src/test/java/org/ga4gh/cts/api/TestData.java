package org.ga4gh.cts.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.ga4gh.models.ReferenceSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ga4gh.cts.api.Utils.aSingle;


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
     * The default ID of the dataset that holds the test data.  We use something readable so the
     * meaning is clear, but in reality the value of this is unlikely to be human-readable.
     */
    public static final String DEFAULT_DATASET_ID = "compliance-dataset";

    /**
     * The name of the Java system property that sets the ID of the compliance dataset.
     */
    private static final String DATASET_PROP_NAME = "ctk.tgt.dataset_id";

    /**
     * The name of the reference in the standard test data.
     */
    public static final String REFERENCE_NAME = "ref_brca1";

    /**
     * The start of the reference data.
     */
    public static final long REFERENCE_START = 0;

    /**
     * The end of the reference data.
     */
    public static final long REFERENCE_END = 81187;

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
     * The AssemblyID (really, a name) of the test {@link ReferenceSet}.
     */
    public static final String REFERENCESET_ASSEMBLY_ID = "hg37";

    /**
     * Accession "numbers" (names, really) for the test {@link ReferenceSet}.
     */
    public static final List<String> REFERENCESET_ACCESSIONS = aSingle("GA4GH_CTS_01");

    /**
     * MD5 checksum for the test {@link ReferenceSet}.
     */
    public static final String REFERENCESET_MD5_CHECKSUM = "90977a37195d3fd247e4916b5b4cbae8";

    /**
     * NCBI Taxonomy ID (identifies species) for the test {@link ReferenceSet} (the NCBI TaxonId for Homo Sapiens).
     */
    public static final String REFERENCESET_TAXON_ID = "9606";

    /**
     * The name of the BRCA1 reference sequence.
     */
    public static final String REFERENCE_BRCA1_NAME = "ref_brca1";

    /**
     * The Accession "number" (really, a name) of the BRCA1 reference sequence.
     */
    public static final String REFERENCE_BRCA1_ACCESSION = "GA4GH_CTS_01_BRCA1";

    /**
     * MD5 checksum of the BRCA1 reference sequence.
     */
    public static final String REFERENCE_BRCA1_MD5_CHECKSUM = "90977a37195d3fd247e4916b5b4cbae8";

    /**
     * Length of BRCA1 reference sequence.
     */
    public static final long REFERENCE_BRCA1_LENGTH = 81188;

    /**
     * Return the ID of the compliance dataset on the server being tested.
     * By default this is the value of {@link #DEFAULT_DATASET_ID}, but
     * you can override it by setting the Java property <tt>-Dctk.tgt.dataset_id</tt>.
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