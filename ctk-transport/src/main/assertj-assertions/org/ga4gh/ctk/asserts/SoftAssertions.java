package org.ga4gh.ctk.asserts;

import org.assertj.core.api.ErrorCollector;
import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.internal.cglib.proxy.Enhancer;

import java.util.List;

import static org.assertj.core.groups.Properties.extractProperty;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
public class SoftAssertions {

  /** Collects error messages of all AssertionErrors thrown by the proxied method. */
  protected final ErrorCollector collector = new ErrorCollector();

  /** Creates a new </code>{@link SoftAssertions}</code>. */
  public SoftAssertions() {
  }

  /**
   * Verifies that no proxied assertion methods have failed.
   *
   * @throws SoftAssertionError if any proxied assertion objects threw
   */
  public void assertAll() {
    List<Throwable> errors = collector.errors();
    if (!errors.isEmpty()) {
      throw new SoftAssertionError(extractProperty("message", String.class).from(errors));
    }
  }

  @SuppressWarnings("unchecked")
  protected <T, V> V proxy(Class<V> assertClass, Class<T> actualClass, T actual) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(assertClass);
    enhancer.setCallback(collector);
    return (V) enhancer.create(new Class[] { actualClass }, new Object[] { actual });
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.ctk.transport.RespCodeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.ctk.transport.RespCodeAssert assertThat(org.ga4gh.ctk.transport.RespCode actual) {
    return proxy(org.ga4gh.ctk.transport.RespCodeAssert.class, org.ga4gh.ctk.transport.RespCode.class, actual);
  }

  /**
   * Creates a new "soft" instance of <code>{@link org.ga4gh.ctk.transport.WireTrackerAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created "soft" assertion object.
   */
  public org.ga4gh.ctk.transport.WireTrackerAssert assertThat(org.ga4gh.ctk.transport.WireTracker actual) {
    return proxy(org.ga4gh.ctk.transport.WireTrackerAssert.class, org.ga4gh.ctk.transport.WireTracker.class, actual);
  }

}
