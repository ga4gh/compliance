package org.ga4gh.ctk.config;

import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

/**
 * <p>Centralize Spring framework configuration triggering</p>
 * <p>Main role here is to enable the java configuration and trigger
 * scanning of the org.ga4gh.ctk package for Spring Beans</p>
 * <p>Created by Wayne Stidolph on 6/16/2015.</p>
 */

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.ga4gh.ctk")
public class AppConfig {

    @Bean
    public AppConfig config() {
        return new AppConfig();
    }

/*    @Bean
    public TestFinder testFinder() {
        return new TestFinder();
    }*/

}
