package org.ga4gh.ctk.domain;

import org.springframework.data.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Created by Wayne Stidolph on 7/24/2015.
 */
// theComponent marker keeps IDE happy
@Component
public interface TrafficLogRepository extends CrudRepository<TrafficLog, Long>{
    List<TrafficLog> findByClassSent(String classSent);
    List<TrafficLog> findByClassReceived(String classReceived);
    List<TrafficLog> findByRunKey(long runKey);

    int countByClassSent(String classSent);
    int countByClassReceived(String classReceived);
}
