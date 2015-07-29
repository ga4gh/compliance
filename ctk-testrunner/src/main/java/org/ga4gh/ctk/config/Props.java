package org.ga4gh.ctk.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.stereotype.*;

/**
 * <p>Access to runtime environment properties.</p>
 * <p>This class uses Spring @Value injection to get properties from the runtime
 * environment into simple strings bound to the class; the instance can then be
 * injected (by Spring) into objects that want these control variables.</p>
 *
 * <p>This works when the application is run under Spring (e.g., when the application jar
 *  is run from the command line or the app is launched using mvn spring-boot:run). </p>
 *
 * <p>To use the Props class, just inject it into your class, perhaps using a setter:</p>
 * <pre>
 * {@code
 *    &#64;Autowired
 *     Props props;
 *     public void setProps(Props cfg){this.cfg = cfg;}
 * }
 * </pre>
 * <p>Your IDE will now autocomplete uses of 'props' (or whatever you name it) with
 * the properties Props knows about.</p>
 * <p>Note that the properties can be supplied with dots or underscores, but will be
 * accessed via the java names (e.g., property "ctk.pattern.testclass" or property
 * "ctk_pattern_testclass" would both be injected into the variable named "ctk_pattern_testclass".
 * Spring handles the step of understanding dots and underscores in the environment and properties files,
 * but you have to decide what the properties name is here in this class.</p>
 * <p>This class collects the "ctk.*" config values supplied via Spring's
 * normal configuration precedence order:
 * <ul>
 * <li>command line arguments (anything starting with double-dash,
 * e.g., --foo.url=192.168.2.115:8000 creates or overrides a
 * property 'foo.url)</li>
 * <li>NDI attributes from java:comp/env</li>
 * <li>JVM System properties (System.getProperties())</li>
 * <li>OS environment variables</li>
 * <li>A RandomValuePropertySource that only has properties in random.*</li>
 * <li>Profile-specific application properties outside of the packaged jar
 * (application-{profile}.properties and YAML variants</li>
 * <li>Profile-specific application properties packaged inside the jar
 * (application-{profile}.properties and YAML variants)</li>
 * <li>Application properties outside of the jars
 * (defaulttransport.properties and YAML variants)</li>
 * <li>Application properties packaged inside the jars
 * (defaulttransport.properties and YAML variants)</li>
 * <li>@PropertySource annotations on @Configuration classes (such as this class)</li>
 * <li>Default properties (specified using SpringApplication.setDefaultProperties)</li>
 * </ul>
 * <p>Property/YAML files can be in 4 locations:
 * <ul>
 * <li>(highest priority) externally, in the {@code /config} directory under the app's start dir</li>
 * <li>externally, directly in the app's start dir</li>
 * <li>internally, in the /config package (not used in the CTK)</li>
 * <li>(lowest priority) internally at the root of the classpath (from the "resources/" dir in the source tree)</li>
 * </ul>
 * <p>YAML files seem to take precedence over .properties files of the same name.</p>
 * <p>Profiles used in the CTK:</p>
 * <ul><li>none so far</li></ul>
 * <p>Created by Wayne Stidolph on 6/13/2015.</p>
 */
@Component
@ConfigurationProperties(prefix = "ctk")
@PropertySource("classpath:application.properties")
@Data
public class Props {

    // NOTE getters and setters, if needed, are provided for us by Lombok
    // if we make the field private, but public variables don't need them
    // and seem (to me) cleaner here

    @Value("${ctk.matchstr}")
    public String ctk_matchstr;
    @Value("${ctk.testpackage}")
    public String ctk_testpackage;
    @Value("${ctk.pattern.testclass}")
    public String ctk_pattern_testclass;
    @Value("${ctk.pattern.testsuite}")
    public String ctk_pattern_testsuite;

    @Value("${ctk.antfile}")
    public String ctk_antfile;

    @Value("${ctk.antlog.consolelogger}")
    public String ctk_antlog_consolelogger;

    @Value("${ctk.antlog.clearstats}")
    public String ctk_antlog_clearstats;

    @Value("${ctk.testjar}")
    public String ctk_testjar;

    @Value("${ctk.reporttitle}")
    public String ctk_report_title;


    /* logging control (name of the test/traffic logs) not yet working */
    /*
    @Value("${ctk.logging.systest}")
    public String ctk_logging_systest;
    @Value("${ctk.logging.systest.traffic}")
    public String ctk_logging_systest_traffic;
    */

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
