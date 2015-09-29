package org.ga4gh.ctk.transport;

/**
 * Transport-related utilities.
 *
 * @author Herb Jellinek
 */
public class TransportUtils {

    /**
     * You can't instantiate one of these.
     */
    private TransportUtils() {
    }

    /**
     * Create a new URL (as a string) based on a server root (which may include path components
     * and query parameters) and a path that's relative to the server root.
     * <p>For instance, if we have <tt>baseUrl</tt> = <tt>https://locahost:8000/v1.0?param1=foo&param2=bar</tt>
     * and <tt>path</tt> = "datasets/search", the result should be
     * <tt>https://locahost:8000/v1.0/datasets/search?param1=foo&param2=bar</tt>.
     * </p>
     * @param baseUrl a server root URL
     * @param path relative path
     * @return the result of merging the root and path, accounting for path components and query parameters
     * in baseUrl
     */
    public static String makeUrl(String baseUrl, String path) {
        final URL baseAsUrl = new URL(baseUrl);
        String baseUrlPathPortion = baseAsUrl.getPath();

        if (!baseUrlPathPortion.endsWith("/")) {
            baseUrlPathPortion += "/";
        }

        final URL constructed = new URL(baseAsUrl.toJavaURL());
        constructed.setPath(baseUrlPathPortion+path);
        return constructed.toString();
    }

}
