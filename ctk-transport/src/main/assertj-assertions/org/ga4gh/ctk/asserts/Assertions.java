package org.ga4gh.ctk.asserts;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class Assertions {

  /**
   * Creates a new instance of <code>{@link org.ga4gh.ctk.transport.RespCodeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.ctk.transport.RespCodeAssert assertThat(org.ga4gh.ctk.transport.RespCode actual) {
    return new org.ga4gh.ctk.transport.RespCodeAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link org.ga4gh.ctk.transport.WireTrackerAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public static org.ga4gh.ctk.transport.WireTrackerAssert assertThat(org.ga4gh.ctk.transport.WireTracker actual) {
    return new org.ga4gh.ctk.transport.WireTrackerAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
