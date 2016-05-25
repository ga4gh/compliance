# GA4GH Server Compliance Testing Suite Data README

The data in this directory is a small curated subset of the larger [1000 genomes](http://www.1000genomes.org) 
dataset maintained by NCBI and EBI.

It centers on data pertinent to small genes such as BRCA1, _TODO: add data for other genes_
The file `referenceset_hg37.json` contains metadata pertinent to the referenceset used (a small subset of GRCh37)
including a list of all the reference FASTA (`.fa`) files used.
Each FASTA reference file comes supplied with a companion metadata JSON file: For example `ref_brca1.fa` and `ref_brca.json`

For each dataset (ie. gene), the following files are supplied:

* `<dataset>_<variant_group>_<variantFilename>.vcf` - the VCF containing variant calls for the gene 
  (including selected specific sample data),
* `<dataset>_<sample>.sam` - Reads corresponding to the above, for the given sample. 
  There will be more than one of these per dataset.

## Examples

For the gene BRCA1, which is contained on chromosome 17 in the region `41196312-41277500` (coordinates in the `GRCh37` reference), 
the files will be:

```
    ref_brca1.fa
    ref_brca1.json
    brca1_1kgPhase3_variants.vcf
    brca1_HG00096.sam
    brca1_HG00099.sam
    brca1_HG00101.sam
```

Each VCF and SAM (reads) file is modified to refer to the artificially truncated reference FASTA, 
and its internal coordinates are offset to refer to the correct position within that reference.
Thus, a line in the originally sourced SAM file that starts with:

    HG00099.mapped.ILLUMINA.bwa.GBR.low_coverage.20130415.bam.cram:192629003    147 17  41197590    60  101M    =   41197460    -231    ACAGTA...

is translated to:

    HG00099.mapped.ILLUMINA.bwa.GBR.low_coverage.20130415.bam.cram:192629003    147 ref 1278    60  101M    =   1148    -231    ACAGTA...

Since the compliance data are provided as human-readable FASTA, SAM and VCF files, these will likely need to be converted 
to equivalent optimized and indexed files when imported into the server being tested.

Variant Annotation Data

Initial variant annotation data is in the files WASH7P_annotation.vcf and OR4F_annotation.vcf.

## Using the data with the compliance test suite

The class `org.ga4gh.cts.api.TestData` in the GA4GH compliance test suite (`TestData.java` in the `cts-java` module) 
describes the server's data as the compliance tests expect it to be.  If you make any additions or changes to the existing
compliance test data, you will almost certainly need to make corresponding changes to the `TestData` class.

The data contained here will likely need to be converted to a machine-optimized, indexed form for use with a server. 
For example, to obtain compressed and indexed files for use with the reference server,

### Reference:

    bgzip -c <reference_file>.fa > <reference_file>.fa.gz
    samtools faidx <reference_file>.fa.gz

### Reads:

    samtools view -b -h -o <reads_file>.bam <reads_file>.sam
    samtools index <reads_file>.bam

### Variants:

    bgzip -c <variants_file>.vcf > <variants_file>.vcf.gz
    tabix -p vcf <variants_file>.vcf.gz

Please refer to [samtools](http://www.htslib.org/doc/samtools.html) and [tabix](http://www.htslib.org/doc/tabix.html) 
for any further details on converting the data to your preferred indexed binary format.


## License and contact

This data is provided under *** TODO: What license? *** by the [GA4GH consortium](http://ga4gh.org)
