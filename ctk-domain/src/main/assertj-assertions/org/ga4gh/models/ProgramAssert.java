package org.ga4gh.models;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

/**
 * {@link Program} specific assertions - Generated by CustomAssertionGenerator.
 */
public class ProgramAssert extends AbstractAssert<ProgramAssert, Program> {

  /**
   * Creates a new <code>{@link ProgramAssert}</code> to make assertions on actual Program.
   * @param actual the Program we want to make assertions on.
   */
  public ProgramAssert(Program actual) {
    super(actual, ProgramAssert.class);
  }

  /**
   * An entry point for ProgramAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myProgram)</code> and get specific assertion with code completion.
   * @param actual the Program we want to make assertions on.
   * @return a new <code>{@link ProgramAssert}</code>
   */
  public static ProgramAssert assertThat(Program actual) {
    return new ProgramAssert(actual);
  }

  /**
   * Verifies that the actual Program's classSchema is equal to the given one.
   * @param classSchema the given classSchema to compare the actual Program's classSchema to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's classSchema is not equal to the given one.
   */
  public ProgramAssert hasClassSchema(org.apache.avro.Schema classSchema) {
    // check that actual Program we want to make assertions on is not null.
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
   * Verifies that the actual Program's commandLine is equal to the given one.
   * @param commandLine the given commandLine to compare the actual Program's commandLine to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's commandLine is not equal to the given one.
   */
  public ProgramAssert hasCommandLine(String commandLine) {
    // check that actual Program we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting commandLine of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualCommandLine = actual.getCommandLine();
    if (!Objects.areEqual(actualCommandLine, commandLine)) {
      failWithMessage(assertjErrorMessage, actual, commandLine, actualCommandLine);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Program's id is equal to the given one.
   * @param id the given id to compare the actual Program's id to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's id is not equal to the given one.
   */
  public ProgramAssert hasId(String id) {
    // check that actual Program we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting id of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualId = actual.getId();
    if (!Objects.areEqual(actualId, id)) {
      failWithMessage(assertjErrorMessage, actual, id, actualId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Program's name is equal to the given one.
   * @param name the given name to compare the actual Program's name to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's name is not equal to the given one.
   */
  public ProgramAssert hasName(String name) {
    // check that actual Program we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting name of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualName = actual.getName();
    if (!Objects.areEqual(actualName, name)) {
      failWithMessage(assertjErrorMessage, actual, name, actualName);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Program's prevProgramId is equal to the given one.
   * @param prevProgramId the given prevProgramId to compare the actual Program's prevProgramId to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's prevProgramId is not equal to the given one.
   */
  public ProgramAssert hasPrevProgramId(String prevProgramId) {
    // check that actual Program we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting prevProgramId of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualPrevProgramId = actual.getPrevProgramId();
    if (!Objects.areEqual(actualPrevProgramId, prevProgramId)) {
      failWithMessage(assertjErrorMessage, actual, prevProgramId, actualPrevProgramId);
    }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Program's schema is equal to the given one.
   * @param schema the given schema to compare the actual Program's schema to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's schema is not equal to the given one.
   */
  public ProgramAssert hasSchema(org.apache.avro.Schema schema) {
    // check that actual Program we want to make assertions on is not null.
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
   * Verifies that the actual Program's version is equal to the given one.
   * @param version the given version to compare the actual Program's version to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Program's version is not equal to the given one.
   */
  public ProgramAssert hasVersion(String version) {
    // check that actual Program we want to make assertions on is not null.
    isNotNull();

    // overrides the default error message with a more explicit one
    String assertjErrorMessage = "\nExpecting version of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
    
    // null safe check
    String actualVersion = actual.getVersion();
    if (!Objects.areEqual(actualVersion, version)) {
      failWithMessage(assertjErrorMessage, actual, version, actualVersion);
    }

    // return the current assertion for method chaining
    return this;
  }






}
