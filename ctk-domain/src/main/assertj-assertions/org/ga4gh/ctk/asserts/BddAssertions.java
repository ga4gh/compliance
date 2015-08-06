package org.ga4gh.ctk.asserts;

/**
 * Entry point for BDD assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class BddAssertions {

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.GAExceptionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.GAExceptionAssert then(org.ga4gh.methods.GAException actual) {
    return new org.ga4gh.methods.GAExceptionAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ListReferenceBasesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ListReferenceBasesRequestAssert then(org.ga4gh.methods.ListReferenceBasesRequest actual) {
    return new org.ga4gh.methods.ListReferenceBasesRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ListReferenceBasesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ListReferenceBasesResponseAssert then(org.ga4gh.methods.ListReferenceBasesResponse actual) {
    return new org.ga4gh.methods.ListReferenceBasesResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.RPCAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.RPCAssert then(org.ga4gh.methods.RPC actual) {
    return new org.ga4gh.methods.RPCAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ReadMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ReadMethodsAssert then(org.ga4gh.methods.ReadMethods actual) {
    return new org.ga4gh.methods.ReadMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.ReferenceMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.ReferenceMethodsAssert then(org.ga4gh.methods.ReferenceMethods actual) {
    return new org.ga4gh.methods.ReferenceMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchCallSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchCallSetsRequestAssert then(org.ga4gh.methods.SearchCallSetsRequest actual) {
    return new org.ga4gh.methods.SearchCallSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchCallSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchCallSetsResponseAssert then(org.ga4gh.methods.SearchCallSetsResponse actual) {
    return new org.ga4gh.methods.SearchCallSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchDatasetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchDatasetsRequestAssert then(org.ga4gh.methods.SearchDatasetsRequest actual) {
    return new org.ga4gh.methods.SearchDatasetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchDatasetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchDatasetsResponseAssert then(org.ga4gh.methods.SearchDatasetsResponse actual) {
    return new org.ga4gh.methods.SearchDatasetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadGroupSetsRequestAssert then(org.ga4gh.methods.SearchReadGroupSetsRequest actual) {
    return new org.ga4gh.methods.SearchReadGroupSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadGroupSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadGroupSetsResponseAssert then(org.ga4gh.methods.SearchReadGroupSetsResponse actual) {
    return new org.ga4gh.methods.SearchReadGroupSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadsRequestAssert then(org.ga4gh.methods.SearchReadsRequest actual) {
    return new org.ga4gh.methods.SearchReadsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReadsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReadsResponseAssert then(org.ga4gh.methods.SearchReadsResponse actual) {
    return new org.ga4gh.methods.SearchReadsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferenceSetsRequestAssert then(org.ga4gh.methods.SearchReferenceSetsRequest actual) {
    return new org.ga4gh.methods.SearchReferenceSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferenceSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferenceSetsResponseAssert then(org.ga4gh.methods.SearchReferenceSetsResponse actual) {
    return new org.ga4gh.methods.SearchReferenceSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferencesRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferencesRequestAssert then(org.ga4gh.methods.SearchReferencesRequest actual) {
    return new org.ga4gh.methods.SearchReferencesRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchReferencesResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchReferencesResponseAssert then(org.ga4gh.methods.SearchReferencesResponse actual) {
    return new org.ga4gh.methods.SearchReferencesResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantSetsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantSetsRequestAssert then(org.ga4gh.methods.SearchVariantSetsRequest actual) {
    return new org.ga4gh.methods.SearchVariantSetsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantSetsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantSetsResponseAssert then(org.ga4gh.methods.SearchVariantSetsResponse actual) {
    return new org.ga4gh.methods.SearchVariantSetsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantsRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantsRequestAssert then(org.ga4gh.methods.SearchVariantsRequest actual) {
    return new org.ga4gh.methods.SearchVariantsRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.SearchVariantsResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.SearchVariantsResponseAssert then(org.ga4gh.methods.SearchVariantsResponse actual) {
    return new org.ga4gh.methods.SearchVariantsResponseAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.methods.VariantMethodsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.methods.VariantMethodsAssert then(org.ga4gh.methods.VariantMethods actual) {
    return new org.ga4gh.methods.VariantMethodsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CallAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CallAssert then(org.ga4gh.models.Call actual) {
    return new org.ga4gh.models.CallAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CallSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CallSetAssert then(org.ga4gh.models.CallSet actual) {
    return new org.ga4gh.models.CallSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CigarOperationAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CigarOperationAssert then(org.ga4gh.models.CigarOperation actual) {
    return new org.ga4gh.models.CigarOperationAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.CigarUnitAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.CigarUnitAssert then(org.ga4gh.models.CigarUnit actual) {
    return new org.ga4gh.models.CigarUnitAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.DatasetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.DatasetAssert then(org.ga4gh.models.Dataset actual) {
    return new org.ga4gh.models.DatasetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ExperimentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ExperimentAssert then(org.ga4gh.models.Experiment actual) {
    return new org.ga4gh.models.ExperimentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ExternalIdentifierAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ExternalIdentifierAssert then(org.ga4gh.models.ExternalIdentifier actual) {
    return new org.ga4gh.models.ExternalIdentifierAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.FragmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.FragmentAssert then(org.ga4gh.models.Fragment actual) {
    return new org.ga4gh.models.FragmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.LinearAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.LinearAlignmentAssert then(org.ga4gh.models.LinearAlignment actual) {
    return new org.ga4gh.models.LinearAlignmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.MetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.MetadataAssert then(org.ga4gh.models.Metadata actual) {
    return new org.ga4gh.models.MetadataAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.PositionAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.PositionAssert then(org.ga4gh.models.Position actual) {
    return new org.ga4gh.models.PositionAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ProgramAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ProgramAssert then(org.ga4gh.models.Program actual) {
    return new org.ga4gh.models.ProgramAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadAlignmentAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadAlignmentAssert then(org.ga4gh.models.ReadAlignment actual) {
    return new org.ga4gh.models.ReadAlignmentAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadGroupAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadGroupAssert then(org.ga4gh.models.ReadGroup actual) {
    return new org.ga4gh.models.ReadGroupAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadGroupSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadGroupSetAssert then(org.ga4gh.models.ReadGroupSet actual) {
    return new org.ga4gh.models.ReadGroupSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadStatsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadStatsAssert then(org.ga4gh.models.ReadStats actual) {
    return new org.ga4gh.models.ReadStatsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReadsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReadsAssert then(org.ga4gh.models.Reads actual) {
    return new org.ga4gh.models.ReadsAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferenceAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferenceAssert then(org.ga4gh.models.Reference actual) {
    return new org.ga4gh.models.ReferenceAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferenceSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferenceSetAssert then(org.ga4gh.models.ReferenceSet actual) {
    return new org.ga4gh.models.ReferenceSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.ReferencesAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.ReferencesAssert then(org.ga4gh.models.References actual) {
    return new org.ga4gh.models.ReferencesAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.StrandAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.StrandAssert then(org.ga4gh.models.Strand actual) {
    return new org.ga4gh.models.StrandAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantAssert then(org.ga4gh.models.Variant actual) {
    return new org.ga4gh.models.VariantAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantSetAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantSetAssert then(org.ga4gh.models.VariantSet actual) {
    return new org.ga4gh.models.VariantSetAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantSetMetadataAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantSetMetadataAssert then(org.ga4gh.models.VariantSetMetadata actual) {
    return new org.ga4gh.models.VariantSetMetadataAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.models.VariantsAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.models.VariantsAssert then(org.ga4gh.models.Variants actual) {
    return new org.ga4gh.models.VariantsAssert(actual);
  }

  /**
   * Creates a new <code>{@link BddAssertions}</code>.
   */
  protected BddAssertions() {
    // empty
  }
}
