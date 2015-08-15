package org.ga4gh.ctk.server;

import org.ga4gh.ctk.domain.*;
import org.ga4gh.ctk.services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

import javax.annotation.*;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.ga4gh.ctk")
public class CtkServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtkServerApplication.class, args);
    }

    @Autowired
    TrafficLogRepository trafficLogRepository;

    @PostConstruct
    void setTheTrafficRepo() {
        // manually set the repo, rather than letting Spring do it
        TestActivityDataService.getService().setTrafficLogRepository(trafficLogRepository);
    }

}
