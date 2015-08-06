package org.ga4gh.ctk.asserts;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class Assertions {

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.GAExceptionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.GAExceptionAssert assertThat(org.ga4gh.methods.GAException actual) {
    return new org.ga4gh.methods.GAExceptionAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ListReferenceBasesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ListReferenceBasesRequestAssert assertThat(org.ga4gh.methods.ListReferenceBasesRequest actual) {
    return new org.ga4gh.methods.ListReferenceBasesRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ListReferenceBasesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ListReferenceBasesResponseAssert assertThat(org.ga4gh.methods.ListReferenceBasesResponse actual) {
    return new org.ga4gh.methods.ListReferenceBasesResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.RPCAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.RPCAssert assertThat(org.ga4gh.methods.RPC actual) {
    return new org.ga4gh.methods.RPCAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ReadMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ReadMethodsAssert assertThat(org.ga4gh.methods.ReadMethods actual) {
    return new org.ga4gh.methods.ReadMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ReferenceMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ReferenceMethodsAssert assertThat(org.ga4gh.methods.ReferenceMethods actual) {
    return new org.ga4gh.methods.ReferenceMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchCallSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchCallSetsRequestAssert assertThat(org.ga4gh.methods.SearchCallSetsRequest actual) {
    return new org.ga4gh.methods.SearchCallSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchCallSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchCallSetsResponseAssert assertThat(org.ga4gh.methods.SearchCallSetsResponse actual) {
    return new org.ga4gh.methods.SearchCallSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchDatasetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchDatasetsRequestAssert assertThat(org.ga4gh.methods.SearchDatasetsRequest actual) {
    return new org.ga4gh.methods.SearchDatasetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchDatasetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchDatasetsResponseAssert assertThat(org.ga4gh.methods.SearchDatasetsResponse actual) {
    return new org.ga4gh.methods.SearchDatasetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadGroupSetsRequestAssert assertThat(org.ga4gh.methods.SearchReadGroupSetsRequest actual) {
    return new org.ga4gh.methods.SearchReadGroupSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadGroupSetsResponseAssert assertThat(org.ga4gh.methods.SearchReadGroupSetsResponse actual) {
    return new org.ga4gh.methods.SearchReadGroupSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadsRequestAssert assertThat(org.ga4gh.methods.SearchReadsRequest actual) {
    return new org.ga4gh.methods.SearchReadsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadsResponseAssert assertThat(org.ga4gh.methods.SearchReadsResponse actual) {
    return new org.ga4gh.methods.SearchReadsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferenceSetsRequestAssert assertThat(org.ga4gh.methods.SearchReferenceSetsRequest actual) {
    return new org.ga4gh.methods.SearchReferenceSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferenceSetsResponseAssert assertThat(org.ga4gh.methods.SearchReferenceSetsResponse actual) {
    return new org.ga4gh.methods.SearchReferenceSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferencesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferencesRequestAssert assertThat(org.ga4gh.methods.SearchReferencesRequest actual) {
    return new org.ga4gh.methods.SearchReferencesRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferencesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferencesResponseAssert assertThat(org.ga4gh.methods.SearchReferencesResponse actual) {
    return new org.ga4gh.methods.SearchReferencesResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantSetsRequestAssert assertThat(org.ga4gh.methods.SearchVariantSetsRequest actual) {
    return new org.ga4gh.methods.SearchVariantSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantSetsResponseAssert assertThat(org.ga4gh.methods.SearchVariantSetsResponse actual) {
    return new org.ga4gh.methods.SearchVariantSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantsRequestAssert assertThat(org.ga4gh.methods.SearchVariantsRequest actual) {
    return new org.ga4gh.methods.SearchVariantsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantsResponseAssert assertThat(org.ga4gh.methods.SearchVariantsResponse actual) {
    return new org.ga4gh.methods.SearchVariantsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.VariantMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.VariantMethodsAssert assertThat(org.ga4gh.methods.VariantMethods actual) {
    return new org.ga4gh.methods.VariantMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CallAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CallAssert assertThat(org.ga4gh.models.Call actual) {
    return new org.ga4gh.models.CallAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CallSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CallSetAssert assertThat(org.ga4gh.models.CallSet actual) {
    return new org.ga4gh.models.CallSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CigarOperationAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CigarOperationAssert assertThat(org.ga4gh.models.CigarOperation actual) {
    return new org.ga4gh.models.CigarOperationAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CigarUnitAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CigarUnitAssert assertThat(org.ga4gh.models.CigarUnit actual) {
    return new org.ga4gh.models.CigarUnitAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.DatasetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.DatasetAssert assertThat(org.ga4gh.models.Dataset actual) {
    return new org.ga4gh.models.DatasetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ExperimentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ExperimentAssert assertThat(org.ga4gh.models.Experiment actual) {
    return new org.ga4gh.models.ExperimentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ExternalIdentifierAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ExternalIdentifierAssert assertThat(org.ga4gh.models.ExternalIdentifier actual) {
    return new org.ga4gh.models.ExternalIdentifierAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.FragmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.FragmentAssert assertThat(org.ga4gh.models.Fragment actual) {
    return new org.ga4gh.models.FragmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.LinearAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.LinearAlignmentAssert assertThat(org.ga4gh.models.LinearAlignment actual) {
    return new org.ga4gh.models.LinearAlignmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.MetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.MetadataAssert assertThat(org.ga4gh.models.Metadata actual) {
    return new org.ga4gh.models.MetadataAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.PositionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.PositionAssert assertThat(org.ga4gh.models.Position actual) {
    return new org.ga4gh.models.PositionAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ProgramAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ProgramAssert assertThat(org.ga4gh.models.Program actual) {
    return new org.ga4gh.models.ProgramAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadAlignmentAssert assertThat(org.ga4gh.models.ReadAlignment actual) {
    return new org.ga4gh.models.ReadAlignmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadGroupAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadGroupAssert assertThat(org.ga4gh.models.ReadGroup actual) {
    return new org.ga4gh.models.ReadGroupAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadGroupSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadGroupSetAssert assertThat(org.ga4gh.models.ReadGroupSet actual) {
    return new org.ga4gh.models.ReadGroupSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadStatsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadStatsAssert assertThat(org.ga4gh.models.ReadStats actual) {
    return new org.ga4gh.models.ReadStatsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadsAssert assertThat(org.ga4gh.models.Reads actual) {
    return new org.ga4gh.models.ReadsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferenceAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferenceAssert assertThat(org.ga4gh.models.Reference actual) {
    return new org.ga4gh.models.ReferenceAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferenceSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferenceSetAssert assertThat(org.ga4gh.models.ReferenceSet actual) {
    return new org.ga4gh.models.ReferenceSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferencesAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferencesAssert assertThat(org.ga4gh.models.References actual) {
    return new org.ga4gh.models.ReferencesAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.StrandAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.StrandAssert assertThat(org.ga4gh.models.Strand actual) {
    return new org.ga4gh.models.StrandAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantAssert assertThat(org.ga4gh.models.Variant actual) {
    return new org.ga4gh.models.VariantAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantSetAssert assertThat(org.ga4gh.models.VariantSet actual) {
    return new org.ga4gh.models.VariantSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantSetMetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantSetMetadataAssert assertThat(org.ga4gh.models.VariantSetMetadata actual) {
    return new org.ga4gh.models.VariantSetMetadataAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantsAssert assertThat(org.ga4gh.models.Variants actual) {
    return new org.ga4gh.models.VariantsAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
