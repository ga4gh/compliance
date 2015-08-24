package org.ga4gh.ctk.services;

import org.ga4gh.ctk.domain.*;
import org.ga4gh.ctk.utility.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * <p>Manage persistance and query on test's TrafficLog data for coverage checks</p>
 * <p>Uses a Spring-supplied embedded DB if avail (as under the Server), or a
 * local-storage option if no Spring repos supplied (as when run under IDE JUnit
 * test runner. This leads to an unfortunate "if repo then ... else ... " structure
 * in many methods, but this seems faster/easier than spinning up a DB
 * for every unit test run. (A design alternative of using a ServiceFactory, seems
 * too heavyweight for this size of service.)</p>
 * Created by Wayne Stidolph on 7/28/2015.
 */
public class TrafficLogService {
    private static Logger log = LoggerFactory.getLogger(TrafficLogService.class);

    private static TrafficLogService instance;
    /* Why not use a FactoryBean with a dependency on the trafficLogRepository
   to generate one of two implementations of TrafficLogService? Because
   this seems clearer (to me) for reading the code, and clarity in reading is
   important here. But, if someone prefers doing it one of the other ways
   (a ConditionalBean, etc) then this is pretty much the starting place.
 */
    public synchronized  static TrafficLogService getService() {
        if(instance == null) {
            instance = new TrafficLogService();
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

    /**
     * Sets traffic log repository. Used by launchers to avaoid propogating Spring dependence.
     *
     * @param tlr the TrafficLogRepsoitory to use (null for "store it local to Service")
     */
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
            long rk = trafficLogMsg.getRunKey();
            if(!isKnownRunkey(rk)){
                log.warn("TrafficLog msg has unknown runKey {}, creating new local Map entry", rk);
                trafficLogMap.put(rk, new LinkedList<TrafficLog>());
            }
            trafficLogMap.get(rk).add(trafficLogMsg);
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

    /**
     * Gets list of endpoints actually attempted in the test run identified by the runkey.
     *
     * @param runkey the runkey
     * @return the used endpoints
     */
    public List<String> getUsedEndpoints(long runkey) {
        log.debug("getUsedEndpoints with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getEndpoint);
    }

    /**
     * Gets Request data types actually used in the test run identified by the runkey.
     *
     * @param runkey the runkey
     * @return the used requests
     */
    public List<String> getUsedRequests(long runkey) {
        log.debug("getUsedRequests (classSent) with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getClassSent);
    }

    /**
     * Gets Response data types actually used in the test run identified by the runkey.
     *
     * @param runkey the runkey
     * @return the used responses
     */
    public List<String> getUsedResponses(long runkey) {
        log.debug("enter getUsedResponses (classReceived) with runkey " + runkey);
        return collectDistinctLoggedValues(runkey, TrafficLog::getClassReceived);
    }

    /**
     * <p>Gets the list of TrafficLog objects generated during the run identified by the runkey.</p>
     * <p>Use log level 'debug' to see whether local or Repository storage was used and
     * how many TrafficLogs were extracted.</p>
     *
     * @param runkey the runkey
     * @return the traffic logs
     */
    public List<TrafficLog> getTrafficLogs(long runkey) {

        List<TrafficLog> msgs;
        if(!isKnownRunkey(runkey)) {
            log.info("getTrafficLogs asked for logs of unknown runkey {}", runkey);
            return new LinkedList<>();
        }
        if (trafficLogRepository != null) {
            msgs = trafficLogRepository.findByRunKey(runkey);
            log.debug("getTrafficLogs {} entries from {} (dump DB)  for runkey {} ", msgs.size(), this, runkey);
            // msgs.forEach((tlm) -> outlog.info(tlm.toString()));
        } else {
            msgs = trafficLogMap.get(runkey).stream().filter(tl -> tl.getRunKey() == runkey).collect(Collectors.toList());
            log.debug("getTrafficLogs {} entries from {} (dump local store) for runkey {}", this, msgs.size(), runkey);
        }
        return msgs;
    }

    static Random randomno = new Random();

    /**
     * Create test run key - a (non-negative) long.
     *
     * Also does any setup the storage needs for hadnlingthe new runkey.
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

    /**
     * <p>Returns true if teh runkey is available in teh current traffic log repository (whether local or Repository).</p>
     * <p>Use 'debug' log to get the list of known runkeys if the passed-in key is not found.</p>
     *
     * @param runkey the runkey
     * @return the boolean
     */
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

    /**
     * Clear static traffic log for an identified runkey. If using non-static storage, doesn't do anything.
     *
     * @param runkey the runkey
     */
    public void clearStaticTrafficLog(long runkey) {
        log.debug("clearTrafficLog dropping static activity data for runkey {}", runkey);
        trafficLogMap.remove(runkey);
    }

    /**
     * Remove all static traffic logs, just drops the store. No runkeys
     * nor data survive. If using a Repository (DB) then it has no effect.
     */
    public void clearStaticTrafficLog() {
        log.debug("clearTrafficLog dropping ALL static activity data");
        trafficLogMap = new HashMap<>();
    }
}
