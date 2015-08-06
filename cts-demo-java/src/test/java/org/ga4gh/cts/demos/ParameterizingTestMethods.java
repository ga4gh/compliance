package org.ga4gh.cts.demos;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.ga4gh.ctk.CtkLogs;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>Demonstrate use of Parameters techniques.</p>
 * <p>First, annotate with @RunWith(JUnitParamsRunner.class)</p>
 * @see <a href="https://github.com/Pragmatists/JUnitParams/wiki/Quickstart">JunitParams Quickstart<</a>
 * <p>Created by Wayne Stidolph on 6/30/2015.</p>
 */
@RunWith(JUnitParamsRunner.class)
public class ParameterizingTestMethods implements CtkLogs {

    /**
     * Example of a parameterized test using in-line data.  This method runs once for each
     * String/int pair in the @Parameters declaration.  The data is destructured into the requisite number
     * of actual parameters, and types are converted to agree with the formal parameters.
     * @param word a word, e.g. "this"
     * @param number an int, e.g. 1
     */
    @Parameters({"this,1",
                 "is,2",
                 "a,3",
                 "parameterized,4",
                 "test,5"})
    @Test
    public void parametersDemoTest(String word, int number) {
        assertThat(word).isNotEmpty();
        assertThat(number).isGreaterThanOrEqualTo(1);
    }

}
