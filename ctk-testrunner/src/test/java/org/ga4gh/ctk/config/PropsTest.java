package org.ga4gh.ctk.config;

import org.junit.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.rules.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Props Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jun 16, 2015</pre>
 */

@ContextConfiguration(classes = Props.class)
public class PropsTest {

    // These two Rules (new in Spring 4.2) let us use Spring facilities in a test
    // without using the Spring-specific SpringJUnit4ClassRunner
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    Props props;

/*    public void setProps(Props props) {
        this.props = props;
    }*/

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: aPropertyCanBeRead()
     */
    @Test
    public void aPropertyCanBeRead() throws Exception {
        assertThat(props.ctk_testpackage).isNotNull();
    }

    @Test
    public void toStringIsUseful() throws Exception {
        String tostr = props.toString();
        assertThat(tostr).isNotEmpty();
    }


} 
