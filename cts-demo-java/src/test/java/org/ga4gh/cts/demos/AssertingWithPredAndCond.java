package org.ga4gh.cts.demos;

import org.ga4gh.ctk.CtkLogs;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>Demonstrate using predicates and conditions in AssertJ.</p>
 */
public class AssertingWithPredAndCond implements CtkLogs {

    @Test
    public void predicateAndConditionDemoTest() {
        final List<String> data = Arrays.asList("hello", "there", "everyone");

        assertThat(data).filteredOn(word -> word.contains("er")).hasSize(2);
    }

}
