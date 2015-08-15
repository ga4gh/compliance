package org.ga4gh.ctk.server;

import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.*;

/**
 * <p>Configure the web server wrapper.</p>
 * <p>Sets up the web server to serve up the static test reports
 * generated by the junit html reporter</p>
 * Created by Wayne Stidolph on 7/17/2015.
 */
@Configuration
@EnableWebMvc
@EnableJpaRepositories(basePackages="org.ga4gh.ctk.domain")
@ComponentScan("org.ga4gh.ctk")
public class CtkServerConfig extends WebMvcConfigurerAdapter{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry
                .addResourceHandler("/testresults/**")
                .addResourceLocations("file:testresults/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
