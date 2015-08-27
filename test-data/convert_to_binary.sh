#!/bin/sh

# exit on any error
set -e

##
### references
##

# zip prepare the reference(s) with bgzip
bgzip -c hs37d5_chr17_41196312-41277500.fa > hs37d5_chr17_41196312-41277500.fa.gz

# index with samtools
samtools faidx hs37d5_chr17_41196312-41277500.fa.gz

##
### variants
##

# zip with bgzip
bgzip -c 1000g_offset.HG00096_HG00099_HG00101.2010502.17_41196312-41277500.vcf > 1000g_offset.HG00096_HG00099_HG00101.2010502.17_41196312-41277500.vcf.gz

# index with tabix
tabix -p vcf 1000g_offset.HG00096_HG00099_HG00101.2010502.17_41196312-41277500.vcf.gz

##
### reads
##

# make bam files from sam files
samtools view -b -h -o BRCA1_HG00096.bam BRCA1_HG00096_hg37_offset.sam
samtools view -b -h -o BRCA1_HG00099.bam BRCA1_HG00099_hg37_offset.sam
samtools view -b -h -o BRCA1_HG00101.bam BRCA1_HG00101_hg37_offset.sam

# index bam files with samtools
for b in *.bam; do
    samtools index $b
done

echo Conversion complete.
