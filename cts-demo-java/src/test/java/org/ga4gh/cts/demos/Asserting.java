package org.ga4gh.cts.demos;

import ga4gh.ReferenceServiceOuterClass.SearchReferencesRequest;
import ga4gh.ReferenceServiceOuterClassSearchReferencesRequestAssert;
import ga4gh.VariantServiceOuterClass.SearchVariantSetsRequest;
import ga4gh.VariantServiceOuterClassSearchVariantSetsRequestAssert;
import org.ga4gh.ctk.CtkLogs;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates various uses of assertThat(), both plain vanilla AssertJ and
 * custom-generated ones for our domain classes.
 */
public class Asserting implements CtkLogs {

    /**
     * Demonstrate how to test attributes of an object using plain-old-AssertJ, as well
     * as a version of assertThat that we generated for the domain object (in this case,
     * {@link SearchReferencesRequest}).
     */
    @Test
    public void demoTestingObjectAttributes1() {
        final SearchReferencesRequest req =
                SearchReferencesRequest.newBuilder()
                                         .setPageSize(121).build();

        // plain old AssertJ
        assertThat(req.getPageSize()).isEqualTo(121);

        // custom assertThat for SearchReferencesRequest
        ReferenceServiceOuterClassSearchReferencesRequestAssert.assertThat(req).hasPageSize(121);
    }

    /**
     * Demonstrate how to test attributes of an object using plain-old-AssertJ, as well
     * as a version of assertThat that we generated for the domain object (in this case,
     * {@link SearchVariantSetsRequest}).  Notice that the domain object-specific version lets us
     * be more concise, at the cost of having to use a long class name prefix if we mix uses of
     * plain assertThat and domain-specific versions.
     */
    @Test
    public void demoTestingObjectAttributes2() {
        final SearchVariantSetsRequest req =
                SearchVariantSetsRequest.newBuilder()
                        .setDatasetId("compliance-dataset1")
                        .setPageSize(341).build();

        // plain old AssertJ
        assertThat(req.getPageSize()).isEqualTo(341);
        assertThat(req.getDatasetId()).isEqualTo("compliance-dataset1");

        // custom assertThat for SearchVariantSetsRequest
        VariantServiceOuterClassSearchVariantSetsRequestAssert.assertThat(req)
                .hasPageSize(341)
                .hasDatasetId("compliance-dataset1");
    }


}
