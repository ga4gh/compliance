package org.ga4gh.cts.core;

import junitparams.*;
import org.junit.*;
import org.junit.experimental.categories.*;
import org.junit.runner.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Wayne Stidolph on 8/19/2015.
 */
@Category(CoreTests.class)
@RunWith(JUnitParamsRunner.class)
public class PropSetIT {

    @Test
    @Parameters({ // properties ecplicitly passed in via AntExecutor
    "ant.file",
    "ctk.testjar",
    "ctk.testclassroots",
    "ctk.matchstr",
    "ctk.reporttitle",
    "ctk.todir",
    "ctk.runkey",
    "ctk.testmethodkey",
    "ctk.domaintypesfile",
    "ctk.defaulttransportfile",
    })
    public void ensurePropertiesVisible(String theProp) throws Exception {
        assertThat(System.getProperty(theProp)).isNotEmpty();
    }
}
