package org.ga4gh.ctk;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * <p>Test result location and persistence support.</p>
 * Created by Wayne Stidolph on 7/20/2015.
 */
public class ResultsSupport implements CtkLogs{

    /**
     * <p>Get results dir.</p>
     * <p>Results fo in a directory named after the target server,
     * and each result goes in its own directory. The result directory is
     * just named with an integer, so we have, for example,
     * testresults/192.168.2.214_8000/1, testresults/192.168.2.214:8000/2, ...</p>
     *
     * @param urlRoot the target server's url root
     * @return the string name for the just-created target directory
     */
     public synchronized static String getResultsDir(String urlRoot) {
        // we could cut down the synchronized size a lot, or even
        // do a temp dir and just rebame it when done, but no need yet
        String resultsbase = "testresults/"; // TODO move to property
        File resultDir = null;
        URL tgt;
        try {
            tgt = new URL(urlRoot);
        } catch (MalformedURLException e) {
            log.warn("Malformed urlRoot " + urlRoot);
            return "";
        }
        int maxseen = 0;
        Path dir = Paths.get(resultsbase + tgt.getAuthority().replace(":", "_"));
        if (dir.toFile().exists()) {
            // if it doesn't exist then
            // we haven't seen this target, so maxseen is fine at zero
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path path : stream) {
                    log.trace("testing file named " + path.getFileName());
                    String name = path.getName(path.getNameCount() - 1).toString();
                    try {
                        int thisDir = Integer.parseInt(name);
                        if (thisDir > maxseen && path.toFile().isDirectory())
                            maxseen = thisDir;
                    } catch (Exception e) {
                    }
                }
            } catch (IOException e) {
                log.warn("getResultsDir for Path " + dir.toString() + " got IOException ", e);
            }
        }
        String paddedMax = String.format("%05d", maxseen + 1);
        String tgtdir = dir.toString() + "/" + paddedMax + "/";
        new File(tgtdir).mkdir();
        log.debug("calculated test results dir of " + tgtdir);
        return tgtdir;
    }
}
