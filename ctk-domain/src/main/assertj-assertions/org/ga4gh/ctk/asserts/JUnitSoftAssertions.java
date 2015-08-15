package org.ga4gh.ctk.asserts;

import org.assertj.core.api.ErrorCollector;
import org.assertj.core.internal.cglib.proxy.Enhancer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class JUnitSoftAssertions implements TestRule {

  /** Collects error messages of all AssertionErrors thrown by the proxied method. */
  protected final ErrorCollector collector = new ErrorCollector();

  /** Creates a new </code>{@link JUnitSoftAssertions}</code>. */
  public JUnitSoftAssertions() {
    super();
  }

  /**
   * TestRule implementation that verifies that no proxied assertion methods have failed.
   */
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        base.evaluate();
        MultipleFailureException.assertEmpty(collector.errors());
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  protected <T, V> V proxy(Class<V> assertClass, Class<T> actualClass, T actual) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(assertClass);
    enhancer.setCallback(collector);
    return (V) enhancer.create(new Class[] { actualClass }, new Object[] { actual });
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.GAExceptionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.GAExceptionAssert assertThat(org.ga4gh.methods.GAException actual) {
    return proxy(org.ga4gh.methods.GAExceptionAssert.class, org.ga4gh.methods.GAException.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.ListReferenceBasesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.ListReferenceBasesRequestAssert assertThat(org.ga4gh.methods.ListReferenceBasesRequest actual) {
    return proxy(org.ga4gh.methods.ListReferenceBasesRequestAssert.class, org.ga4gh.methods.ListReferenceBasesRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.ListReferenceBasesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.ListReferenceBasesResponseAssert assertThat(org.ga4gh.methods.ListReferenceBasesResponse actual) {
    return proxy(org.ga4gh.methods.ListReferenceBasesResponseAssert.class, org.ga4gh.methods.ListReferenceBasesResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.RPCAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.RPCAssert assertThat(org.ga4gh.methods.RPC actual) {
    return proxy(org.ga4gh.methods.RPCAssert.class, org.ga4gh.methods.RPC.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.ReadMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.ReadMethodsAssert assertThat(org.ga4gh.methods.ReadMethods actual) {
    return proxy(org.ga4gh.methods.ReadMethodsAssert.class, org.ga4gh.methods.ReadMethods.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.ReferenceMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.ReferenceMethodsAssert assertThat(org.ga4gh.methods.ReferenceMethods actual) {
    return proxy(org.ga4gh.methods.ReferenceMethodsAssert.class, org.ga4gh.methods.ReferenceMethods.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchCallSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchCallSetsRequestAssert assertThat(org.ga4gh.methods.SearchCallSetsRequest actual) {
    return proxy(org.ga4gh.methods.SearchCallSetsRequestAssert.class, org.ga4gh.methods.SearchCallSetsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchCallSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchCallSetsResponseAssert assertThat(org.ga4gh.methods.SearchCallSetsResponse actual) {
    return proxy(org.ga4gh.methods.SearchCallSetsResponseAssert.class, org.ga4gh.methods.SearchCallSetsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchDatasetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchDatasetsRequestAssert assertThat(org.ga4gh.methods.SearchDatasetsRequest actual) {
    return proxy(org.ga4gh.methods.SearchDatasetsRequestAssert.class, org.ga4gh.methods.SearchDatasetsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchDatasetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchDatasetsResponseAssert assertThat(org.ga4gh.methods.SearchDatasetsResponse actual) {
    return proxy(org.ga4gh.methods.SearchDatasetsResponseAssert.class, org.ga4gh.methods.SearchDatasetsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReadGroupSetsRequestAssert assertThat(org.ga4gh.methods.SearchReadGroupSetsRequest actual) {
    return proxy(org.ga4gh.methods.SearchReadGroupSetsRequestAssert.class, org.ga4gh.methods.SearchReadGroupSetsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReadGroupSetsResponseAssert assertThat(org.ga4gh.methods.SearchReadGroupSetsResponse actual) {
    return proxy(org.ga4gh.methods.SearchReadGroupSetsResponseAssert.class, org.ga4gh.methods.SearchReadGroupSetsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReadsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReadsRequestAssert assertThat(org.ga4gh.methods.SearchReadsRequest actual) {
    return proxy(org.ga4gh.methods.SearchReadsRequestAssert.class, org.ga4gh.methods.SearchReadsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReadsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReadsResponseAssert assertThat(org.ga4gh.methods.SearchReadsResponse actual) {
    return proxy(org.ga4gh.methods.SearchReadsResponseAssert.class, org.ga4gh.methods.SearchReadsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReferenceSetsRequestAssert assertThat(org.ga4gh.methods.SearchReferenceSetsRequest actual) {
    return proxy(org.ga4gh.methods.SearchReferenceSetsRequestAssert.class, org.ga4gh.methods.SearchReferenceSetsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReferenceSetsResponseAssert assertThat(org.ga4gh.methods.SearchReferenceSetsResponse actual) {
    return proxy(org.ga4gh.methods.SearchReferenceSetsResponseAssert.class, org.ga4gh.methods.SearchReferenceSetsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReferencesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReferencesRequestAssert assertThat(org.ga4gh.methods.SearchReferencesRequest actual) {
    return proxy(org.ga4gh.methods.SearchReferencesRequestAssert.class, org.ga4gh.methods.SearchReferencesRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchReferencesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchReferencesResponseAssert assertThat(org.ga4gh.methods.SearchReferencesResponse actual) {
    return proxy(org.ga4gh.methods.SearchReferencesResponseAssert.class, org.ga4gh.methods.SearchReferencesResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchVariantSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchVariantSetsRequestAssert assertThat(org.ga4gh.methods.SearchVariantSetsRequest actual) {
    return proxy(org.ga4gh.methods.SearchVariantSetsRequestAssert.class, org.ga4gh.methods.SearchVariantSetsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchVariantSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchVariantSetsResponseAssert assertThat(org.ga4gh.methods.SearchVariantSetsResponse actual) {
    return proxy(org.ga4gh.methods.SearchVariantSetsResponseAssert.class, org.ga4gh.methods.SearchVariantSetsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchVariantsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchVariantsRequestAssert assertThat(org.ga4gh.methods.SearchVariantsRequest actual) {
    return proxy(org.ga4gh.methods.SearchVariantsRequestAssert.class, org.ga4gh.methods.SearchVariantsRequest.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.SearchVariantsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.SearchVariantsResponseAssert assertThat(org.ga4gh.methods.SearchVariantsResponse actual) {
    return proxy(org.ga4gh.methods.SearchVariantsResponseAssert.class, org.ga4gh.methods.SearchVariantsResponse.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.methods.VariantMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.methods.VariantMethodsAssert assertThat(org.ga4gh.methods.VariantMethods actual) {
    return proxy(org.ga4gh.methods.VariantMethodsAssert.class, org.ga4gh.methods.VariantMethods.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.CallAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.CallAssert assertThat(org.ga4gh.models.Call actual) {
    return proxy(org.ga4gh.models.CallAssert.class, org.ga4gh.models.Call.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.CallSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.CallSetAssert assertThat(org.ga4gh.models.CallSet actual) {
    return proxy(org.ga4gh.models.CallSetAssert.class, org.ga4gh.models.CallSet.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.CigarOperationAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.CigarOperationAssert assertThat(org.ga4gh.models.CigarOperation actual) {
    return proxy(org.ga4gh.models.CigarOperationAssert.class, org.ga4gh.models.CigarOperation.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.CigarUnitAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.CigarUnitAssert assertThat(org.ga4gh.models.CigarUnit actual) {
    return proxy(org.ga4gh.models.CigarUnitAssert.class, org.ga4gh.models.CigarUnit.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.DatasetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.DatasetAssert assertThat(org.ga4gh.models.Dataset actual) {
    return proxy(org.ga4gh.models.DatasetAssert.class, org.ga4gh.models.Dataset.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ExperimentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ExperimentAssert assertThat(org.ga4gh.models.Experiment actual) {
    return proxy(org.ga4gh.models.ExperimentAssert.class, org.ga4gh.models.Experiment.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ExternalIdentifierAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ExternalIdentifierAssert assertThat(org.ga4gh.models.ExternalIdentifier actual) {
    return proxy(org.ga4gh.models.ExternalIdentifierAssert.class, org.ga4gh.models.ExternalIdentifier.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.FragmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.FragmentAssert assertThat(org.ga4gh.models.Fragment actual) {
    return proxy(org.ga4gh.models.FragmentAssert.class, org.ga4gh.models.Fragment.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.LinearAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.LinearAlignmentAssert assertThat(org.ga4gh.models.LinearAlignment actual) {
    return proxy(org.ga4gh.models.LinearAlignmentAssert.class, org.ga4gh.models.LinearAlignment.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.MetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.MetadataAssert assertThat(org.ga4gh.models.Metadata actual) {
    return proxy(org.ga4gh.models.MetadataAssert.class, org.ga4gh.models.Metadata.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.PositionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.PositionAssert assertThat(org.ga4gh.models.Position actual) {
    return proxy(org.ga4gh.models.PositionAssert.class, org.ga4gh.models.Position.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ProgramAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ProgramAssert assertThat(org.ga4gh.models.Program actual) {
    return proxy(org.ga4gh.models.ProgramAssert.class, org.ga4gh.models.Program.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReadAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReadAlignmentAssert assertThat(org.ga4gh.models.ReadAlignment actual) {
    return proxy(org.ga4gh.models.ReadAlignmentAssert.class, org.ga4gh.models.ReadAlignment.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReadGroupAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReadGroupAssert assertThat(org.ga4gh.models.ReadGroup actual) {
    return proxy(org.ga4gh.models.ReadGroupAssert.class, org.ga4gh.models.ReadGroup.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReadGroupSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReadGroupSetAssert assertThat(org.ga4gh.models.ReadGroupSet actual) {
    return proxy(org.ga4gh.models.ReadGroupSetAssert.class, org.ga4gh.models.ReadGroupSet.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReadStatsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReadStatsAssert assertThat(org.ga4gh.models.ReadStats actual) {
    return proxy(org.ga4gh.models.ReadStatsAssert.class, org.ga4gh.models.ReadStats.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReadsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReadsAssert assertThat(org.ga4gh.models.Reads actual) {
    return proxy(org.ga4gh.models.ReadsAssert.class, org.ga4gh.models.Reads.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReferenceAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReferenceAssert assertThat(org.ga4gh.models.Reference actual) {
    return proxy(org.ga4gh.models.ReferenceAssert.class, org.ga4gh.models.Reference.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReferenceSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReferenceSetAssert assertThat(org.ga4gh.models.ReferenceSet actual) {
    return proxy(org.ga4gh.models.ReferenceSetAssert.class, org.ga4gh.models.ReferenceSet.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.ReferencesAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.ReferencesAssert assertThat(org.ga4gh.models.References actual) {
    return proxy(org.ga4gh.models.ReferencesAssert.class, org.ga4gh.models.References.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.StrandAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.StrandAssert assertThat(org.ga4gh.models.Strand actual) {
    return proxy(org.ga4gh.models.StrandAssert.class, org.ga4gh.models.Strand.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.VariantAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.VariantAssert assertThat(org.ga4gh.models.Variant actual) {
    return proxy(org.ga4gh.models.VariantAssert.class, org.ga4gh.models.Variant.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.VariantSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.VariantSetAssert assertThat(org.ga4gh.models.VariantSet actual) {
    return proxy(org.ga4gh.models.VariantSetAssert.class, org.ga4gh.models.VariantSet.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.VariantSetMetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.VariantSetMetadataAssert assertThat(org.ga4gh.models.VariantSetMetadata actual) {
    return proxy(org.ga4gh.models.VariantSetMetadataAssert.class, org.ga4gh.models.VariantSetMetadata.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.models.VariantsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.models.VariantsAssert assertThat(org.ga4gh.models.Variants actual) {
    return proxy(org.ga4gh.models.VariantsAssert.class, org.ga4gh.models.Variants.class, actual);
  }

}
