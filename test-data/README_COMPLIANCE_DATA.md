# GA4GH Server Compliance Tesing Suite Data README

The data in this directory is a small curated subset of the larger [1000 genomes](http://www.1000genomes.org) dataset maintained by NCBI and EBI.


It centers on data pertinent to several small genes: BRCA1, *** TODO: other genes ***
For each gene, the following files are supplied:

* `hs37d5_<REGION>.fa`   - the FASTA file containing GRCh37 reference bases for the gene's region
* `1000g_offset.<UNDERSCORE_SEPARATED_SAMPLE_IDS>.2010502.<REGION>.vcf` - the VCF containing variant calls for the gene (including selected specific sample data)
* `BRCA1_<SAMPLE_ID>_hg37_offset.sam` - Reads corresponding to the above, for the given sample. There will be more than one of these per region.

where `<REGION>` is of the form `<CHROMOSOME_NAME>_<START_POSITION>-<END_POSITION>`

## Examples

For the gene BRCA1, which is contained on chromosome 17 in the region `41196312-41277500` (coordinates in the `GRCh37` reference), the files will be:

    hs37d5_chr17_41196312-41277500.fa
    1000g_offset.HG00096_HG00099_HG00101.2010502.17_41196312-41277500.vcf
    BRCA1_HG00096_hg37_offset.sam
    BRCA1_HG00099_hg37_offset.sam
    BRCA1_HG00101_hg37_offset.sam

Each VCF and SAM (reads) file is modified to refer to the artificially truncated reference FASTA, and its internal coordinates are offset
to refer to the correct position within that reference.


The line in the originally sourced SAM file that starts with:

    HG00099.mapped.ILLUMINA.bwa.GBR.low_coverage.20130415.bam.cram:192629003    147 17  41197590    60  101M    =   41197460    -231    ACAGTA...

is translated to:

    HG00099.mapped.ILLUMINA.bwa.GBR.low_coverage.20130415.bam.cram:192629003    147 ref 1278    60  101M    =   1148    -231    ACAGTA...

Since the compliance data are provided as human-readable FASTA, SAM and VCF files, these will likely need to be converted to equivalent
optimized and indexed files when imported into the server being tested.

## Using data with compliance suite

The class `org.ga4gh.cts.api.TestData` in the GA4GH compliance suite (`TestData.java` in the `cts-java` module) describes the server's
data as the compliance tests expect it to be.  If you make any additions or changes to the existing
compliance test data, you will almost certainly need to make corresponding changes to the `TestData`
class.


## License and contact

This data is provided under *** TODO: What license? *** by the [GA4GH consortium](http://ga4gh.org)
