package org.ga4gh.ctk.services;

import org.ga4gh.ctk.domain.*;
import org.ga4gh.ctk.utility.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * <p>Manage persistance and query on test activity data for coverage checks</p>
 * <p>Uses a Spring-supplied embedded DB if avail (as under the Server), or a
 * local-storage option if no Springrepos supplied (as when run under IDE JUnit
 * test runner.</p>
 * Created by Wayne Stidolph on 7/28/2015.
 */
public class TestActivityDataService {
    private static Logger log = LoggerFactory.getLogger(TestActivityDataService.class);

    private static TestActivityDataService instance;
    /* Why not use a FactoryBean with a dependency on the trafficLogRepository
   to generate one of two implementations of TestActivityDataService? Because
   this seems clearer (to me) for reading the code, and clarity in reading is
   important here. But, if someone prefers doing it one of the other ways
   (a ConditionalBean, etc) then this is pretty much the starting place.
 */
    public synchronized  static TestActivityDataService getService() {
        if(instance == null) {
            instance = new TestActivityDataService();
            log.debug("getService created service {}", instance);
        }
        log.trace("getService returning {}", instance);
        return instance;
    }


    // manual/optional wiring by the launcher, not Autowiring
    // so that we don't get error when launching tests on their
    // own which then use this service without a spring context
    //@Autowired
    private TrafficLogRepository trafficLogRepository;

    // Map to use if no trafficLogRepository set
    private Map<Long, List<TrafficLog>> trafficLogMap = new HashMap<>();

    public void setTrafficLogRepository(TrafficLogRepository tlr) {
        if (tlr == null){
            log.debug("setTrafficLogRepository has null arg, will store activity in static Map for {}", this);
        } else {
            log.debug("setTrafficLogRepository has non-null arg, will store activity in DB for {}", this);
        }
        trafficLogRepository = tlr;
    }

    /**
     * Save a TrafficLogMessage in repo or static/local.
     *
     * @param trafficLogMsg the traffic log msg
     */
    public void save(TrafficLog trafficLogMsg) {
        if(trafficLogMsg == null){
            log.warn("{} got null param to save()", this);
            return;
        }
        if (trafficLogRepository != null) {
            trafficLogRepository.save(trafficLogMsg);
            log.trace("save to DB for runkey {}", trafficLogMsg.getRunKey());
        } else {
            // no Spring init, so just run local, each run its own LinkedList
            trafficLogMap.get(trafficLogMsg.getRunKey()).add(trafficLogMsg);
            log.trace("save to static Map for runkey {}", trafficLogMsg.getRunKey());
        }
    }

    // TODO consider adding runkey value directly to the TrafficLogBuilder?
    public TrafficLogBuilder getTrafficLogBuilder() {
        return new TrafficLogBuilder();
    }

    /**
     * <p>Run a TrafficLog getter-function over the collection of TrafficLogs
     * (test activity) and return the distinct values seen - for example,
     * get the list of endpoints used in the test run using:</p>
     * <code>collectDistinctLoggedValues(myrunkey, TrafficLog::getEndpoints</code>
     * <p>
     * <p>Checks in the attached database (trafficLofRepository) or falls back to
     * checking in the static local Maps</p>
     *
     * @param runkey the runkey
     * @param xtract the getter-function to use
     * @return the list
     */
    public List<String> collectDistinctLoggedValues(long runkey, Function<TrafficLog, String> xtract) {
        log.debug("collectDistinctLoggedValues of {} sees trafficLogRepository as {} ",
                this, trafficLogRepository);

        Set<String> used = new HashSet<>();
        if(!isKnownRunkey(runkey)){
            log.error("collectDistinctLoggedValue {} got unknown runkey: {}", this, runkey);
            return new ArrayList<>();
        }

        if (trafficLogRepository != null) {
            Iterable<TrafficLog> itl = trafficLogRepository.findByRunKey(runkey);
            for (TrafficLog tl : itl) {
                used.add(xtract.apply(tl));
            }
        } else {
            used = trafficLogMap.get(runkey).stream()
                    .filter(tl -> tl.getRunKey() == runkey)
                    .map(xtract)
                    .collect((Collectors.toSet()));
        }
        List<String> sortedStrings = Utils.asSortedList(used);
        log.debug("collectDistinctLoggedValues has 'used' size {}, returns List<String> size {}",
                used.size(), sortedStrings.size());
        return sortedStrings;
    }

    public List<String> getUsedEndpoints(long runkey) {
        log.debug("getUsedEndpoints with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getEndpoint);
    }

    public List<String> getUsedRequests(long runkey) {
        log.debug("getUsedRequests (classSent) with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getClassSent);
    }

    public List<String> getUsedResponses(long runkey) {
        log.debug("enter getUsedResponses (classReceived) with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getClassReceived);
    }

    public void logTraffic(String logname, long runkey) {

        Logger outlog = LoggerFactory.getLogger(logname);
        if (trafficLogRepository != null) {
            log.debug("logTraffic (dump DB) from {} to {} for runkey {}", this, logname, runkey);
            trafficLogRepository.findByRunKey(runkey).forEach((tlm) -> outlog.info(tlm.toString()));
        } else {
            log.debug("logTraffic (dump local store) from {} to {} for runkey {}", this, logname, runkey);
            trafficLogMap.get(runkey).stream().filter(tl -> tl.getRunKey() == runkey).forEach(tlm -> outlog.info(tlm.toString()));
        }
    }

    static Random randomno = new Random();

    /**
     * Create test run key - a (non-negative) long.
     *
     * @return the runKey
     */
    public long createTestRunKey() {
        long newkey = Math.abs(randomno.nextLong()); // FIXME low probability of collisions, but still ...
        if(trafficLogRepository != null) {
            log.debug("using repository, so {} just returns the new runkey {}", this, newkey);
        } else { // no repo, set up local Map
            trafficLogMap.put(newkey, new LinkedList<TrafficLog>());
            log.debug("added runkey {} to trafficLogMap of {}, keyset is {}", this, newkey, trafficLogMap.keySet().toString());
        }

        return newkey;
    }

     boolean isKnownRunkey(long runkey){
        if (trafficLogRepository != null) {
            return true; // FIXME when adding a test activity data set to the repos
        }
        boolean found = (trafficLogMap.containsKey(runkey));
        if(!found){
            log.info("isKnownRunKey of {} did not find key {}", this, runkey);
            log.debug("trafficLogMap keyset is {}", trafficLogMap.keySet().toString());
        }
        return found;
    }

    public void clearStaticTrafficLog(long runkey) {
        log.debug("clearTrafficLog dropping static activity data for runkey {}", runkey);
        trafficLogMap.remove(runkey);
    }
}
