#!/bin/sh

# exit on any error
set -e

##
### references
##

# zip prepare the reference(s) with bgzip
bgzip -c ref_brca1.fa > ref_brca1.fa.gz

# index with samtools
samtools faidx ref_brca1.fa.gz

##
### variants
##

# zip with bgzip
bgzip -c brca1_1kgPhase3_variants.vcf > brca1_1kgPhase3_variants.vcf.gz

# index with tabix
tabix -p vcf 1000g_offset.HG00096_HG00099_HG00101.2010502.17_41196312-41277500.vcf.gz

##
### reads
##

# make bam files from sam files
samtools view -b -h -o brca1_HG00096.bam brca1_HG00096.sam
samtools view -b -h -o brca1_HG00099.bam brca1_HG00099.sam
samtools view -b -h -o brca1_HG00101.bam brca1_HG00101.sam

# index bam files with samtools
for b in *.bam; do
    samtools index $b
done

echo Conversion complete.
