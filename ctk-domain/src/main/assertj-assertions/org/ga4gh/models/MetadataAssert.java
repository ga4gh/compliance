package org.ga4gh.models;

import org.assertj.core.api.AbstractAssert;

/**
 * {@link Metadata} specific assertions - Generated by CustomAssertionGenerator.
 */
public class MetadataAssert extends AbstractAssert<MetadataAssert, Metadata> {

  /**
   * Creates a new <code>{@link MetadataAssert}</code> to make assertions on actual Metadata.
   * @param actual the Metadata we want to make assertions on.
   */
  public MetadataAssert(Metadata actual) {
    super(actual, MetadataAssert.class);
  }

  /**
   * An entry point for MetadataAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myMetadata)</code> and get specific assertion with code completion.
   * @param actual the Metadata we want to make assertions on.
   * @return a new <code>{@link MetadataAssert}</code>
   */
  public static MetadataAssert assertThat(Metadata actual) {
    return new MetadataAssert(actual);
  }

}
