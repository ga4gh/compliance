package org.ga4gh.ctk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.ga4gh.ctk")
public class CtkServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtkServerApplication.class, args);
    }

}
