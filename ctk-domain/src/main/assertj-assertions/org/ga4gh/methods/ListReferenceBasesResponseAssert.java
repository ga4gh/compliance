package org.ga4gh.methods;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

/**
 * {@link ListReferenceBasesResponse} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ListReferenceBasesResponseAssert extends AbstractAssert<ListReferenceBasesResponseAssert, ListReferenceBasesResponse> {

  /**
   * Creates a new <code>{@link ListReferenceBasesResponseAssert}</code> to make assertions on actual ListReferenceBasesResponse.
   * @param actual the ListReferenceBasesResponse we want to make assertions on.
   */
  public ListReferenceBasesResponseAssert(ListReferenceBasesResponse actual) {
    super(actual, ListReferenceBasesResponseAssert.class);
  }

  /**
   * An entry point for ListReferenceBasesResponseAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myListReferenceBasesResponse)</code> and get specific assertion with code completion.
   * @param actual the ListReferenceBasesResponse we want to make assertions on.
   * @return a new <code>{@link ListReferenceBasesResponseAssert}</code>
   */
  public static ListReferenceBasesResponseAssert assertThat(ListReferenceBasesResponse actual) {
    return new ListReferenceBasesResponseAssert(actual);
  }

  /**
   * Verifies that the actual ListReferenceBasesResponse's classSchema is equal to the given one.
   * @param classSchema the given classSchema to compare the actual ListReferenceBasesResponse's classSchema to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ListReferenceBasesResponse's classSchema is not equal to the given one.
   */
  public ListReferenceBasesResponseAssert hasClassSchema(org.apache.avro.Schema classSchema) {
    // check that actual ListReferenceBasesResponse we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting classSchema of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    org.apache.avro.Schema actualClassSchema = actual.getClassSchema();
    if (!Objects.areEqual(actualClassSchema, classSchema)) {
      failWithMessage(assertjErrorMessage, actual, classSchema, actualClassSchema);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ListReferenceBasesResponse's nextPageToken is equal to the given one.
   * @param nextPageToken the given nextPageToken to compare the actual ListReferenceBasesResponse's nextPageToken to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ListReferenceBasesResponse's nextPageToken is not equal to the given one.
   */
  public ListReferenceBasesResponseAssert hasNextPageToken(String nextPageToken) {
    // check that actual ListReferenceBasesResponse we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting nextPageToken of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualNextPageToken = actual.getNextPageToken();
    if (!Objects.areEqual(actualNextPageToken, nextPageToken)) {
      failWithMessage(assertjErrorMessage, actual, nextPageToken, actualNextPageToken);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ListReferenceBasesResponse's offset is equal to the given one.
   * @param offset the given offset to compare the actual ListReferenceBasesResponse's offset to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ListReferenceBasesResponse's offset is not equal to the given one.
   */
  public ListReferenceBasesResponseAssert hasOffset(Long offset) {
    // check that actual ListReferenceBasesResponse we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting offset of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    Long actualOffset = actual.getOffset();
    if (!Objects.areEqual(actualOffset, offset)) {
      failWithMessage(assertjErrorMessage, actual, offset, actualOffset);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ListReferenceBasesResponse's schema is equal to the given one.
   * @param schema the given schema to compare the actual ListReferenceBasesResponse's schema to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ListReferenceBasesResponse's schema is not equal to the given one.
   */
  public ListReferenceBasesResponseAssert hasSchema(org.apache.avro.Schema schema) {
    // check that actual ListReferenceBasesResponse we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting schema of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    org.apache.avro.Schema actualSchema = actual.getSchema();
    if (!Objects.areEqual(actualSchema, schema)) {
      failWithMessage(assertjErrorMessage, actual, schema, actualSchema);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual ListReferenceBasesResponse's sequence is equal to the given one.
   * @param sequence the given sequence to compare the actual ListReferenceBasesResponse's sequence to.
   * @return this assertion object.
   * @throws AssertionError - if the actual ListReferenceBasesResponse's sequence is not equal to the given one.
   */
  public ListReferenceBasesResponseAssert hasSequence(String sequence) {
    // check that actual ListReferenceBasesResponse we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting sequence of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualSequence = actual.getSequence();
    if (!Objects.areEqual(actualSequence, sequence)) {
      failWithMessage(assertjErrorMessage, actual, sequence, actualSequence);
    }

    // return the current assertion for method chaining
    return this;
  }




}
