package org.ga4gh.ctk.utility;

import org.slf4j.*;

import java.io.*;
import java.util.*;

/**
 * Created by Wayne Stidolph on 7/28/2015.
 */
public class Utils {
    static Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * Make a sorted ("natural sort") List<T> from a Collection<T>
     *
     * @param <T>  the type parameter
     * @param c the Collection (will not be modified)
     * @return the list
     */
    public static
    <T extends Comparable<? super T>> List<T>
    asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        Collections.sort(list);
        return list;
    }

    /**
     * Read fileinto String.
     *
     * @param filename the filename
     * @return the string
     */
    public static String readFile(String filename) {
        String result = "";
        int lineNo=0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                ++lineNo;
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            log.warn("readFile " + filename + " : " + lineNo + " failed due to " + e.getMessage());
        }
        return result;
    }

    public static String getPropEnvValue(String key) {
        if(key == null) {
            log.warn("getPropEnvValue has null param for key");
            return "";
        }

        String possVal=System.getProperty(key);
        if(possVal != null) {
            log.info("Read system Property {} as {}", key, possVal);
            return possVal;
        }

        possVal=System.getenv(key);
        if (possVal != null) {
            log.debug("no Property {} but found in Environment as {}", key, possVal);
            return possVal;
        }
        log.warn("no value found for {} in Properties nor Environment", key);
        return "";
    }
}
