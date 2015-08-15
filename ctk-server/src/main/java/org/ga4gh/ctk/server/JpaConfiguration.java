package org.ga4gh.ctk.server;

/**
 * Created by Wayne Stidolph on 7/27/2015.
 */

import org.springframework.boot.orm.jpa.*;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.*;

@Configuration
@EnableJpaRepositories
@EntityScan(basePackages="org.ga4gh.ctk.domain")
public class JpaConfiguration {
}
