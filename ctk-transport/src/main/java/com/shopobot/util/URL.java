package com.shopobot.util;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a URL for an internet resource. It is not to be
 * confused with a URI. I wrote this class to make a URL class for Java that is
 * more OO than the traditional java.net.URL. It is most useful for modifying
 * URLs for a web application.
 * 
 * @author <a href="https://github.com/juliuss">julius schorzman</a>
 */
public class URL implements Iterable<URL.Parameter> {

  /**
   * Represents a URL parameter. Names and values are automatically encoded on
   * entry and decoded on removal.
   */
  public class Parameter {

    private String name = "";
    private String value = "";

    /**
     * Creates a parameter with the provided name and an empty value. The name
     * is encoded upon storage.
     * 
     * @param name
     *          The name of the parameter. Silently accepts null and empty
     *          strings, however a blank parameter will be returned when
     *          toString() is called.
     */
    public Parameter(String name) {
      this.name = name == null ? "" : urlEncode(name);
    }

    /**
     * Creates a parameter with the provided name and value, both of which are
     * encoded upon storage.
     * 
     * @param name
     *          The name of the parameter. Silently accepts null and empty
     *          strings, however a blank parameter will be returned when
     *          toString() is called.
     * @param value
     *          The value of the parameter. Accepts null and empty strings.
     */
    public Parameter(String name, String value) {
      this.name = name == null ? "" : urlEncode(name);
      this.value = value == null ? "" : urlEncode(value);
    }

    /**
     * This private constructor is used only when parsing existing urls
     */
    private Parameter(String name, String value, boolean noEncode) {
      this.name = name == null ? "" : name;
      this.value = value == null ? "" : value;
    }

    /**
     * Returns the name of this parameter. It is automatically decoded.
     */
    public String getName() {
      return urlDecode(name);
    }

    /**
     * Returns the value of this parameter. It is automatically decoded.
     */
    public String getValue() {
      return urlDecode(value);
    }

    /**
     * Returns an encoded string representation of this parameter, usually
     * name=value If the name is empty an empty string is returned. If the value
     * is empty, the name alone is returned.
     */
    @Override
    public String toString() {
      if (name.isEmpty()) {
        return "";
      }
      if (value.isEmpty()) {
        return name;
      }
      return name + "=" + value;
    }
  }

  /**
   * This enum represents supported Protocols and their default ports.
   */
  public enum Protocol {

    // default ports
    http(80), https(443), ftp(21);

    private int defaultPort;

    private Protocol(int defaultPort) {
      this.defaultPort = defaultPort;
    }

    /**
     * Returns the default port for this protocol. Example: http returns 80.
     */
    public int getDefaultPort() {
      return defaultPort;
    }
  }

  private static final String ENCODING = "UTF-8";
  private static final String ENCODING_ERROR = " is not a supported encoding option";

  /**
   * Returns a URL for the provided String, or null if the URL provided is
   * invalid.
   * 
   * @param url
   *          The external form of a URL.
   * @return A URL object for the provided String, or null if the URL provided
   *         is invalid.
   */
  public static URL get(String url) {
    try {
      return new URL(url);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns a URL for the provided String, or the provided default URL if the
   * URL provided is invalid.
   * 
   * @param url
   *          The external form of a URL.
   * @param defaul
   *          The URL that should be returned in cases where the primary url is
   *          invalid.
   * @return A URL object for the provided String, or null if the URL provided
   *         is invalid.
   */
  public static URL get(String url, URL defaul) {
    try {
      return new URL(url);
    } catch (Exception e) {
      return defaul;
    }
  }

  /**
   * Tests if the provided URL is valid.
   * 
   * @param url
   *          The URL to test.
   * @return true if the URL can be instantiated, false otherwise.
   */
  public static boolean isValid(String url) {
    try {
      new URL(url);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  protected Protocol protocol = Protocol.http;
  protected String username = "";
  protected String password = "";
  protected String host = "";
  protected int port = protocol.defaultPort;
  protected String path = "/";
  protected List<URL.Parameter> parameters = new ArrayList<Parameter>();
  protected String fragment = "";

  /**
   * Creates a URL given a provided URL. Reference URL(String url) for more
   * info.
   */
  public URL(java.net.URI uri) {
    this(uri.toString());
  }

  /**
   * Creates a URL given a provided URL. Reference URL(String url) for more
   * info.
   */
  public URL(java.net.URL url) {
    this(url.toExternalForm());
  }

  /**
   * Creates a URL given a provided string. Must be a valid URL (according to
   * java.net.URI). <b>Note that empty, null, or invalid URLs will throw a
   * RuntimeException</b> (either NullPointerException for a null parameter or
   * IllegalArgumentException for an empty or invalid URL), so the expectation
   * is that the calling method will check the url for correctness. To help with
   * this, you can use the static method:
   * 
   * <pre>
   * {@code isValid(String url);}
   * </pre>
   * 
   * or the factory-like:
   * 
   * <pre>
   * {@code URL.get("an invalid url!")}
   * </pre>
   * 
   * which will not throw an exception and instead return null, or:
   * 
   * <pre>
   * {@code URL.get("an invalid url!","http://www.example.com/a-default-url.html")}
   * </pre>
   * 
   * which will not throw an exception and instead return the default url
   * provided.
   * 
   * @param url
   *          The external form of the URL. Ex: http://www.google.com If this
   *          parameter is empty or invalid an IllegalArgumentException is
   *          thrown. If this parameter is null a NullPointerException.
   * @throws IllegalArgumentException
   *           Thrown if url is not valid.
   * @throws NullPointerException
   *           Thrown if parameter is null.
   */
  public URL(String url) {

    // test the input
    if (url == null)
      throw new NullPointerException("URL cannot be null");
    if (url.isEmpty())
      throw new IllegalArgumentException("URL cannot be empty");

    // if the url does not contain a scheme/protocol, default to http
    // this allows a constructor to be called such as "www.example.com"
    if (!url.contains("://")) {
      url = URL.Protocol.http + "://" + url;
    }

    java.net.URI u;
    try {
      u = new java.net.URI(url);
      if (u.getHost() == null) {
        throw new IllegalArgumentException("The class can only represent URLs.");
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }

    protocol = Protocol.valueOf(u.getScheme());
    username = "";
    password = "";

    host = u.getHost();
    if (host == null) {
      host = "";
    }

    port = u.getPort();
    if (port < 1) {
      port = protocol.defaultPort;
    }

    path = u.getRawPath();
    if (path == null || path.isEmpty()) {
      path = "/";
    }

    fragment = u.getRawFragment();
    if (fragment == null) {
      fragment = "";
    }

    // query string
    parseQueryString(url);

    // parse user info
    String info = u.getUserInfo();
    if (info != null) {
      int pos = info.indexOf(':');
      if ((pos >= 0) && (pos + 1 < info.length())) {
        username = info.substring(0, pos);
        password = info.substring(pos + 1);
      } else {
        username = info;
      }
    }
  }

  /**
   * Adds a parameter to the URL with the provided name and value. Reference
   * addParameter(String name, String value) for more info.
   */
  public URL addParameter(String name, int value) {
    addParameter(name, value + "");
    return this;
  }

  /**
   * Returns a copy of this URL.
   */
  public URL clone() {
    return new URL(this.toString());
  }

  /**
   * Adds a parameter to the URL with the provided name and value. Example:
   * 
   * <pre>
   * {@code URL ( "http://www.shopobot.com/search" ).addParameter( "query" ,
   * "ipod" ).equals( "http://www.shopobot.com/search?query=ipod" );}
   * </pre>
   * 
   * <b>NOTE:</b> Adding a parameter that already exists is supported. Example:
   * 
   * <pre>
   * {@code URL( "http://www.shopobot.com/search?query=ipod" ).addParameter(
   * "query" , "ipad" ).equals(
   * "http://www.shopobot.com/search?query=ipod&query=ipad" );
   * </pre>
   * 
   * If instead you want to replace a parameter's value, use setParameter.
   * 
   * @param name
   *          The name of the parameter. It is automatically encoded if
   *          necessary. If null or empty is passed silently no parameter is
   *          added.
   * @param value
   *          The value of the parameter. It is automatically encoded if
   *          necessary. If null or empty, a "name only" parameter is added.
   */
  public URL addParameter(String name, String value) {
    if (name == null || name.isEmpty()) {
      return this;
    }
    Parameter p = new Parameter(name, (value == null) ? "" : value);
    parameters.add(p);
    return this;
  }

  /**
   * This private method is only used when creating a url that already has
   * parameters and no encoding is necessary.
   */
  private URL addParameterNoEncode(String name, String value) {
    if (name == null || name.isEmpty()) {
      return this;
    }
    Parameter p = new Parameter(name, (value == null) ? "" : value, false);
    parameters.add(p);
    return this;
  }

  /**
   * Decrements by 1 the integer value of the parameter in the URL that matches
   * the provided name. If the URL contains multiple parameters, the first
   * encountered that can be casted to an int will be decremented. If no
   * parameter exists that equals the provided name AND can be casted to an int,
   * the url will not be modified.
   * 
   * @param name
   *          The name of the parameter to decrement. Null and or an empty
   *          string will silently return without changing the url.
   */
  public URL decrementParameter(String name) {
    if (name == null || name.isEmpty()) {
      return this;
    }
    for (URL.Parameter p : parameters) {
      if (p.getName().equals(name)) {
        try {
          int i = Integer.parseInt(p.getValue());
          i--;
          if (i == 1) {
            removeParameter(name);
          } else {
            setParameter(name, i);
          }
          return this;
        } catch (NumberFormatException e) {
          // ignored, keep trying or return default
        }
      }
    }
    return this;
  }

  @Override
  /**
   * Compares this object with a similar object, String, java.net.URL, java.netURI.  Returns true if the external form of the URL is identical.
   */
  public boolean equals(Object anObject) {
    if (anObject == null) {
      return false;
    }
    if (this == anObject) {
      return true;
    }
    if (anObject instanceof String) {
      return (toString().equals(anObject));
    }
    if (anObject instanceof java.net.URL) {
      return (toString().equals(((java.net.URL) anObject).toExternalForm()));
    }
    if (anObject instanceof java.net.URI) {
      return (toString().equals(((java.net.URI) anObject).toString()));
    }
    return false;
  }

  /**
   * Helper method that URL decodes a value.
   */
  private String fragmentDecode(String value) {
    try {
      return FragmentEncoder.decode(value, URL.ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(URL.ENCODING + URL.ENCODING_ERROR, e);
    }
  }

  /**
   * Helper method that URL encodes a value.
   */
  private String fragmentEncode(String value) {
    try {
      return FragmentEncoder.encode(value, URL.ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(URL.ENCODING + URL.ENCODING_ERROR, e);
    }
  }

  /**
   * Returns the fragment portion of this URL. For example, "fragment" from:
   * http://www.google.com/?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   */
  public String getFragment() {
    return fragmentDecode(fragment);
  }

  /**
   * Returns the host portion of this URL. For example, "www.google.com" from:
   * http://www.google.com/?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   */
  public String getHost() {
    return host;
  }

  /**
   * Gets the integer value of the parameter in the URL that matches the
   * provided name. If the URL contains multiple parameters, the first
   * encountered that can be casted to an int will be returned. If no parameter
   * exists with the provided name AND cannot be casted to an int, the provided
   * defaul int value will be returned.
   * 
   * @param name
   *          The name of the parameter to return. The name is automatically
   *          encoded if necessary.
   * @param defaul
   *          If the is no parameter to return, this value is returned instead.
   * @return The value of the parameter, of defaul if none present.
   */
  public int getParameter(String name, int defaul) {
    for (URL.Parameter p : parameters) {
      if (p.getName().equals(name)) {
        try {
          return Integer.parseInt(p.getValue());
        } catch (NumberFormatException e) {
          // ignored, keep trying or return default
        }
      }
    }
    return defaul;
  }

  /**
   * Gets the value of the parameter in the URL that matches the provided name.
   * If the URL contains multiple parameters, the first encountered will be
   * returned. In cases where the URL contains multiple parameters with the same
   * name, used getParameters(String name) instead. If no parameter exists with
   * the provided name, the provided defaul will be returned. If the parameter
   * has no value, "www.example.com/?test", an empty string is returned, <b>not
   * the defaul value</b>.
   * 
   * @param name
   *          The name of the parameter to return. The name is automatically
   *          encoded if necessary.
   * @param defaul
   *          If the is no parameter to return, this value is returned instead.
   * @return The value of the parameter, of defaul if none present.
   */
  public String getParameter(String name, String defaul) {
    for (URL.Parameter p : parameters) {
      if (p.getName().equals(name)) {
        return p.getValue();
      }
    }
    return defaul;
  }

  /**
   * Gets the values of the parameters in the URL that matche the provided name.
   * If the URL contains unique parameter names, use getPatameter instead. If no
   * parameter exists with the provided name, an empty array will be returned.
   * 
   * @param name
   *          The name of the parameter(s) to return. The name is automatically
   *          encoded if necessary.
   * @return A string array of decoded parameter values.
   */
  public String[] getParameters(String name) {
    name = urlEncode(name);
    List<String> array = new ArrayList<String>();
    for (URL.Parameter p : parameters) {
      if (p.getName().equals(name)) {
        array.add(p.getValue());
      }
    }
    return array.toArray(new String[0]);
  }

  /**
   * Returns the password portion of this URL. For example, "pass" from:
   * ftp://user:pass@ftp.example.com/?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   * 
   * @return The password of this url or an empty string if none is present.
   *         Never null.
   */
  public String getPassword() {
    return urlDecode(password);
  }

  /**
   * Returns the path portion of this URL. For example,
   * "/directory/to/resource.html" from:
   * http://www.google.com/directory/to/resource.html?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   * 
   * @return The path of this url. The path will always be at minimum "/" and
   *         will never be empty.
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns the path of this URL split by /. For example,
   * "/directory/to/resource.html" from: returns a List<String> with three
   * elements: [0] : directory [1] : to [2] : resource.html Note that poorly
   * formed URLs, like "//directory///to////resource.html" would return the
   * exact same list as above.
   * 
   * If the path is simply / an empty list is returned.
   */
  public List<String> getPathElements() {
    if (path.replaceAll("/", "").isEmpty()) {
      return new ArrayList<String>();
    }
    /**
     * Sometimes paths are formed poorly with double slashes This regex makes
     * sure doulbe slashes are treated as a single slash.
     */
    String p = path.replaceAll("[/]{2,}", "/");
    return Arrays.asList(p.split("/"));
  }

  /**
   * Returns the parent directory of a directory in this URLs path. For example,
   * if this url's path is: /a/b/c/d.html all of these statements are true:
   * 
   * <pre>
   * getParentDirectory("d.html").equals("c")
   * getParentDirectory("b").equals("a")
   * getParentDirectory("a").equals("")
   * getParentDirectory("not-present").equals("")
   * </pre>
   * 
   * In all fault cases and empty string is returned, including: this url's path
   * is empty/root ("/"), if the provided directory or file is not found, the
   * parent category is root.
   */
  public String getParentDirectory(String child) {
    List<String> elements = getPathElements();
    int i = elements.indexOf(child);
    if (i < 0) {
      return ""; // not found
    } else if (i == 0) {
      return ""; // no parent
    } else {
      return elements.get(elements.indexOf(child) - 1);
    }
  }

  /**
   * Returns the child directory (or filename) of a directory in this URLs path.
   * For example, if this url's path is: /a/b/c/d.html all of these statements
   * are true:
   * 
   * <pre>
   * getParentDirectory("a").equals("b")
   * getParentDirectory("c").equals("d.html")
   * getParentDirectory("d.html").equals("")
   * getParentDirectory("not-present").equals("")
   * </pre>
   * 
   * In all fault cases and empty string is returned, including: this url's path
   * is empty/root ("/"), if the provided directory or file is not found, the
   * matching element is the last element in the path.
   */
  public String getChildDirectory(String parent) {
    List<String> elements = getPathElements();
    int i = elements.indexOf(parent);
    if (i < 0) {
      return ""; // not found
    } else if (i == elements.size() - 1) {
      return ""; // no child
    } else {
      return elements.get(elements.indexOf(parent) + 1);
    }
  }

  /**
   * Returns the port of this URL. If no port is present in the URL, the default
   * for the scheme is provided. For example, 80 from:
   * http://www.google.com/?name=value#fragment
   * 
   * and 8080 from: http://www.google.com:8080/?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   * 
   * @return The port of this url. If no other port is provided, the protocol's
   *         default port will be returned. (Example: 80 for http)
   */
  public int getPort() {
    return port;
  }

  /**
   * Returns the protocol portion of this URL. For example, the enum for http is
   * returned from: http://www.google.com/
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   * 
   * @protocol A Protocol enum representing this urls protocol (or scheme).
   *           Never null.
   */
  public Protocol getProtocol() {
    return protocol;
  }

  /**
   * Returns the user portion of this URL. For example, "user" from:
   * ftp://user:pass@ftp.example.com/?name=value#fragment
   * 
   * For reference the get methods are named as:
   * protocol://username:password@host:port/path?query#fragment
   * 
   * @return The URLs username, or an empty string if none. Never null.
   */
  public String getUsername() {
    return urlDecode(username);
  }

  /**
   * Returns a new URL given a relative path.
   * 
   * For example, if this URL object represents "http://example.com" then
   * calling this method with "/a.html" will return "http://example.com/a.html"
   * 
   * Warning: be careful not to pass a non-relative url.
   * 
   * @param relativePath
   * @return A URL object representing the relative path.
   */
  public URL resolveRelative(String relativePath) {
    relativePath = relativePath.trim();
    URI baseURI = this.toJavaURI();
    URI resultURI = baseURI.resolve(relativePath);
    URL url = new URL(resultURI.toString());
    return url;
  }

  /**
   * Increments by 1 the integer value of the parameter in the URL that matches
   * the provided name. If the URL contains multiple parameters, the first
   * encountered that can be casted to an int will be incremented. If no
   * parameter exists with the provided name AND cannot be casted to an int, the
   * parameter will be added with a value of 2.
   * 
   * @return This object for chaining.
   */
  public URL incrementParameter(String name) {
    for (URL.Parameter p : parameters) {
      if (p.getName().equals(name)) {
        try {
          int i = Integer.parseInt(p.getValue());
          setParameter(name, ++i);
          return this;
        } catch (NumberFormatException e) {
          // ignored, keep trying or return default
        }
      }
    }
    setParameter(name, 2);
    return this;
  }

  /**
   * Returns an Iterator for all parameters represented in this URL. For
   * example, given the url: http://www.google.com/?param1=value1&param2=value2
   * - this iterator would iterate over param2 and param2.
   * 
   * @return An Iterator of this URLs Parameters. Never null.
   */
  public Iterator<Parameter> iterator() {
    return parameters.iterator();
  }

  /**
   * Removes the fragment from this URL. If no fragment exists, no change will
   * occur.
   * 
   * @return This object for chaining.
   */
  public URL removeFragment() {
    this.fragment = "";
    return this;
  }

  /**
   * Removes all parameters with the given name from the URL.
   * 
   * @param name The
   *          name(s) of the parameter(s) to remove. Null or empty string will
   *          cause no changes to occur.
   * @return This object for chaining.
   */
  public URL removeParameter(String... name) {
    if (name == null) {
      return this;
    }
    for (String n : name) {
      if (n == null || n.isEmpty()) {
        return this;
      }
      Iterator<URL.Parameter> it = parameters.iterator();
      while (it.hasNext()) {
        Parameter p = it.next();
        if (p.getName().equals(n)) {
          it.remove();
        }
      }
    }
    return this;
  }

  /**
   * Sets the fragment for this URL. Any existing fragment will be overwritten.
   * A null or empty parameter will remove the fragment completely.
   * 
   * @param fragment
   * @return This object for chaining.
   */
  public URL setFragment(String fragment) {
    this.fragment = (fragment == null) ? "" : fragmentEncode(fragment);
    return this;
  }

  /**
   * 
   * @param host
   * @return This object for chaining.
   */
  public URL setHost(String host) {
    this.host = (host == null) ? "" : host;
    return this;
  }

  /**
   * 
   * @param name
   * @param value
   * @return This object for chaining.
   */
  public URL setParameter(String name, int value) {
    setParameter(name, value + "");
    return this;
  }

  /**
   * Sets the value of this parameter in the URL. If no parameter exists with
   * the provided name, it will be added. Example:
   * 
   * <pre>
   * {@code URL( "http://www.shopobot.com/" ).setParameter( "page" , 1
   * ).equals( "http://www.shopobot.com/?page=1" );}
   * </pre>
   * 
   * Example:
   * 
   * <pre>
   * {@code URL( "http://www.shopobot.com/?page=1" ).setParameter( "page" , 2
   * ).equals( "http://www.shopobot.com/?page=2" );}
   * </pre>
   * 
   * If more than one parameter already exists with this name, the resulting url
   * will only contain one parameter with this name and value.
   * 
   * Example:
   * 
   * <pre>
   * {@code URL( "http://www.shopobot.com/?x=1&x=2" ).setParameter( "x" , 3
   * ).equals( "http://www.shopobot.com/?x=3" );}
   * </pre>
   * 
   * @return This object for chaining.
   */
  public URL setParameter(String name, String value) {
    removeParameter(name);
    addParameter(name, value);
    return this;
  }

  /**
   * 
   * @param password
   * @return This object for chaining.
   */
  public URL setPassword(String password) {
    this.password = urlEncode(password);
    return this;
  }

  /**
   * 
   * @param path
   * @return This object for chaining.
   */
  public URL setPath(String path) {
    if (path.startsWith("/")) {
      this.path = path;
    } else {
      this.path = "/" + path;
    }
    return this;
  }

  /**
   * 
   * @param port
   * @return This object for chaining.
   * @throws IllegalArgumentException
   */
  public URL setPort(int port) throws IllegalArgumentException {
    if (port < 1 || port > 65534) {
      throw new IllegalArgumentException("A valid port value is between 0 and 65535.");
    }
    this.port = port;
    return this;
  }

  /**
   * 
   * @param protocol
   *          The protocol name, in any case, without :// Examples: http, HTTPS,
   *          ftp
   * @return This object for chaining.
   * @throws IllegalArgumentException
   *           If not such protocol is supported, IllegalArgumentException is
   *           thrown.
   */
  public URL setProtocol(String protocol) throws IllegalArgumentException {
    Protocol.valueOf(protocol.toLowerCase());
    return this;
  }

  /**
   * 
   * @param username
   * @return This object for chaining.
   */
  public URL setUsername(String username) {
    this.username = urlEncode(username);
    return this;
  }

  @Override
  /**
   * Returns the full external form of the url.
   */
  public String toString() {
    // authority
    String u = getAuthority();
    // path
    u += toStringFull();
    return u;
  }

  /**
   * Returns the full path, query and fragment to this item without including
   * the domain. For example, http://www.shopobot.com/search?q=1 is returned as
   * /search?q=1
   */
  public String toStringFull() {
    return path + getQueryString() + getFragmentString();
  }

  /**
   * Helper method that just returns the query string of this URL, including a
   * leading "?". If there are no query parameters, an empty string is returned.
   */
  public String getQueryString() {
    String u = "";
    if (parameters.size() > 0) {
      boolean first = true;
      u += "?";
      for (Parameter param : this) {
        if (param.toString().isEmpty()) {
          // skip empty parameters
          continue;
        }
        if (first) {
          u += param;
          first = false;
        } else {
          u += "&" + param;
        }
      }
    }
    return u;
  }

  /**
   * Helper method that just returns the fragment string of this URL, including
   * a leading "#". If there is no fragment, an empty string is returned.
   */
  public String getFragmentString() {
    String u = "";
    if (!fragment.isEmpty()) {
      u += "#" + fragment;
    }
    return u;
  }

  public String toStringRelative(URL relativeTo) {
    String target = getPath();
    String source = relativeTo.getPath();
    String relative = "";

    // first we need to verify that all items preceding the path are identical
    if (!this.getProtocol().equals(relativeTo.getProtocol())) {
      return toString();
    } else if (!this.getHost().equals(relativeTo.getHost())) {
      return toString();
    } else if (!this.getUsername().equals(relativeTo.getUsername())) {
      return toString();
    } else if (!this.getPassword().equals(relativeTo.getPassword())) {
      return toString();
    } else if (this.getPort() != (relativeTo.getPort())) {
      return toString();
    }

    // if path starts with a / remove it
    if (source.startsWith("/"))
      source = source.substring(1);
    if (target.startsWith("/"))
      target = target.substring(1);

    String sourceElements[] = source.isEmpty() ? new String[0] : source.split("/");
    String targetElements[] = target.isEmpty() ? new String[0] : target.split("/");

    // set int common to the number of common elements in the path
    int common = 0;
    for (common = 0; common < sourceElements.length && common < targetElements.length; common++) {
      if (!sourceElements[common].equals(targetElements[common])) {
        // break if we've found a non-equal path element
        break;
      }
    }

    // how many times do we have to go to a previous directory to get from
    // source to target?
    int goUp = sourceElements.length - common;
    if (goUp > 0) {
      for (int i = 0; i < goUp; i++) {
        relative += "../";
      }
    }

    // now, ignore the common elements and tack on the new non-common path
    for (int i = common; i < targetElements.length; i++) {
      relative += (relative.endsWith("../") || relative.isEmpty()) ? targetElements[i] : "/" + targetElements[i];
    }

    // since we remove the trailing slash, add it back in if requested, unless
    // the relative path is empty (because the target and source are equal
    if (target.endsWith("/") && !relative.isEmpty()) {
      relative += "/";
    }

    // add in any target query string or fragments
    if (getQueryString().isEmpty() && !relativeTo.getQueryString().isEmpty()) {
      // this is the most consistent way of removing all parameters
      relative = getPath();
    } else if (!this.getQueryString().equals(relativeTo.getQueryString())) {
      relative += getQueryString() + getFragmentString();
    } else if (!this.getFragmentString().equals(relativeTo.getFragmentString())) {
      relative += getFragmentString();
    }

    return relative;
  }

  /**
   * Helper method that URL decodes a value.
   */
  private String urlDecode(String value) {
    try {
      return URLDecoder.decode(value, URL.ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(URL.ENCODING + URL.ENCODING_ERROR, e);
    }
  }

  /**
   * Helper method that URL encodes a value.
   */
  private String urlEncode(String value) {
    try {
      return URLEncoder.encode(value, URL.ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(URL.ENCODING + URL.ENCODING_ERROR, e);
    }
  }

  /**
   * Helper method used to parse a query string and add the parameters.
   * 
   * @param queryString
   *          Either a whole URL ("http://www.shopobot.com/?query=test") or just
   *          a query string ("?query=test"). If null or "" is pased, this
   *          method silently returns without changing anything.
   */
  private URL parseQueryString(String queryString) {
    // parse query parameters
    if (queryString == null || queryString.isEmpty()) {
      return this;
    }
    int pos = queryString.indexOf('?');
    if (pos > -1) {
      String query = queryString.substring(pos + 1);
      queryString = queryString.substring(0, pos);
      for (String param : query.split("&")) {
        pos = param.indexOf("=");
        if (pos > -1) {
          // regular parameter
          addParameterNoEncode(param.substring(0, pos), param.substring(pos + 1));
        } else {
          // empty parameter
          addParameterNoEncode(param, "");
        }
      }
    }
    return this;
  }

  /**
   * Returns an instance of java.net.URL that represents this URL. If the URL is
   * malformed, null is returned.
   */
  public java.net.URL toJavaURL() {
    try {
      return new java.net.URL(toString());
    } catch (MalformedURLException e) {
      return null;
    }
  }

  /**
   * Returns an instance of java.net.URL that represents this URL. If the URL is
   * malformed, null is returned.
   */
  public java.net.URI toJavaURI() {
    try {
      return new java.net.URI(toString());
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /**
   * Returns "http://www.google.com:80" for the URL
   * "http://www.google.com:80/search&q=test"
   * 
   * @return
   */
  public String getAuthority() {
    // protocol
    String u = protocol.name() + "://";

    // user info
    if (!username.isEmpty()) {
      if (!password.isEmpty()) {
        u += username + ":" + password + "@";
      } else {
        u += username + "@";
      }
    }

    // host, port
    if (port != protocol.defaultPort) {
      u += host + ":" + port;
    } else {
      u += host;
    }
    return u;
  }

  /**
   * Tests if this URL object matches the authority of the provided String (or
   * URL). Example: new
   * URL("www.subdomain.example.com").matchesAuthority("com"); //true new
   * URL("www.subdomain.example.com").matchesAuthority("example.com"); //true
   * new
   * URL("www.subdomain.example.com").matchesAuthority("subdomain.example.com");
   * //true new URL("www.subdomain.example.com").matchesAuthority(
   * "www.subdomain.example.com"); //true
   * 
   * @param authority
   *          A string reresenting the authority to check against this url.
   * @return true if the authority matches, false in all other cases (including
   *         if a null or empty parameter is passed).
   */
  public boolean matchesAuthority(String authority) {
    if (authority == null || authority.isEmpty()) {
      return false;
    }
    // true if a direct match (ex: www.amazon.com and www.amazon.com)
    if (host.equals(authority)) {
      return true;
    }
    // if they passed a domain starting with "." remove it
    if (authority.startsWith(".")) {
      authority = authority.substring(1);
    }
    // true if a subdomain match (ex: www.amazon.com and amazon.com)
    return (host.endsWith("." + authority));
  }
  
  /**
   * Tests if this URL object matches the authority of the provided String (or
   * URL). Example: new
   * URL("www.subdomain.example.com").matchesAuthority("com"); //true new
   * URL("www.subdomain.example.com").matchesAuthority("example.com"); //true
   * new
   * URL("www.subdomain.example.com").matchesAuthority("subdomain.example.com");
   * //true new URL("www.subdomain.example.com").matchesAuthority(
   * "www.subdomain.example.com"); //true
   * 
   * @return true if the authority matches, false in all other cases (including
   *         if a null or empty parameter is passed).
   */
  public boolean matchesAuthority(URL url) {
    return matchesAuthority(url.getHost());
  }

  /**
   * Checks if any of the authorities provided match this URL.  If one matches, true is returned.
   */
  public boolean matchesAuthority(String... authority) {
    for (String s : authority) {
      if (matchesAuthority(s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns number of authority elements. new
   * URL("shopobot.com").getAuthoritySize(2); new
   * URL("en.shopobot.com").getAuthoritySize(3); new
   * URL("www.en.shopobot.com").getAuthoritySize(4);
   */
  public int getAuthoritySize() {
    return host.split("\\.").length;
  }

  /**
   * Returns the authority to position i. For example, given the domain:
   * www.en.shopobot.com, these statements are all true:
   * getAuthority(0).equals("com"); getAuthority(1).equals("shopobot.com");
   * getAuthority(2).equals("en.shopobot.com");
   * getAuthority(3).equals("www.en.shopobot.com");
   * getAuthority(4).equals("www.en.shopobot.com");
   */
  public String getAuthority(int i) {
    if (i <= 0)
      return "";
    String s[] = host.split("\\.");
    String r = "";
    int k = 1;
    for (int j = s.length - 1; j >= 0; j--) {
      r = "." + s[j] + r;
      if (k++ >= i) {
        break;
      }
    }
    if (r.startsWith(".")) {
      r = r.substring(1);
    }
    return r;
  }
}
