package org.ga4gh.ctk.transport;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * This class is used to encode a string using the format required by <a
 * href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>.
 *
 * @author <a href="https://github.com/juliuss">julius schorzman</a>
 */
public class FragmentEncoder {

  static final String digits = "0123456789ABCDEF";

  /**
   * Prevents this class from being instantiated.
   */
  private FragmentEncoder() {
  }

  /**
   * Encodes the given string {@code s} in a x-www-form-urlencoded string using
   * the specified encoding scheme {@code enc}.
   * <p>
   * All characters except letters ('a'..'z', 'A'..'Z') and numbers ('0'..'9')
   * and characters '.', '-', '*', '_', '!', '$', '&', ''', '(', ')', '*', '+',
   * ',', ';', '=', '~', ':', '@', '/', '?' are converted into their hexadecimal
   * value prepended by '%'. For example: '#' -> %23. In addition, spaces are
   * substituted by '+'
   * 
   * @param s
   *          the string to be encoded.
   * @param enc
   *          the encoding scheme to be used.
   * @return the encoded string.
   * @throws UnsupportedEncodingException
   *           if the specified encoding scheme is invalid.
   */
  public static String encode(String s, String enc) throws UnsupportedEncodingException {

    if (s == null || enc == null) {
      throw new NullPointerException();
    }
    // check for UnsupportedEncodingException
    "".getBytes(enc);

    // Guess a bit bigger for encoded form
    StringBuilder buf = new StringBuilder(s.length() + 16);
    int start = -1;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || " .-*_!$&'()+,;=~:@/?".indexOf(ch) > -1) {
        if (start >= 0) {
          convert(s.substring(start, i), buf, enc);
          start = -1;
        }
        if (ch != ' ') {
          buf.append(ch);
        } else {
          buf.append('+');
        }
      } else {
        if (start < 0) {
          start = i;
        }
      }
    }
    if (start >= 0) {
      convert(s.substring(start, s.length()), buf, enc);
    }
    return buf.toString();
  }

  private static void convert(String s, StringBuilder buf, String enc) throws UnsupportedEncodingException {
    byte[] bytes = s.getBytes(enc);
    for (int j = 0; j < bytes.length; j++) {
      buf.append('%');
      buf.append(digits.charAt((bytes[j] & 0xf0) >> 4));
      buf.append(digits.charAt(bytes[j] & 0xf));
    }
  }
  
  /**
   * Decodes the argument which is assumed to be encoded in the {@code
   * x-www-form-urlencoded} MIME content type using the specified encoding
   * scheme.
   * <p>
   *'+' will be converted to space, '%' and two following hex digit
   * characters are converted to the equivalent byte value. All other
   * characters are passed through unmodified. For example "A+B+C %24%25" ->
   * "A B C $%".
   *
   * @param s
   *            the encoded string.
   * @param encoding
   *            the encoding scheme to be used.
   * @return the decoded clear-text representation of the given string.
   * @throws UnsupportedEncodingException
   *             if the specified encoding scheme is invalid.
   */
  public static String decode(String s, String encoding)
          throws UnsupportedEncodingException {
      if (encoding == null) {
          throw new NullPointerException();
      }
      if (encoding.isEmpty()) {
          throw new UnsupportedEncodingException(encoding);
      }

      if (s.indexOf('%') == -1) {
          if (s.indexOf('+') == -1)
              return s;
          char[] str = s.toCharArray();
          for (int i = 0; i < str.length; i++) {
              if (str[i] == '+')
                  str[i] = ' ';
          }
          return new String(str);
      }

      Charset charset = null;
      try {
          charset = Charset.forName(encoding);
      } catch (IllegalCharsetNameException e) {
          throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                  encoding).initCause(e));
      } catch (UnsupportedCharsetException e) {
          throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                  encoding).initCause(e));
      }

      return decode(s, charset);
  }

  private static String decode(String s, Charset charset) {

      char[] str_buf = new char[s.length()];
      byte[] buf = new byte[s.length() / 3];
      int buf_len = 0;

      for (int i = 0; i < s.length();) {
          char c = s.charAt(i);
          if (c == '+') {
              str_buf[buf_len] = ' ';
          } else if (c == '%') {

              int len = 0;
              do {
                  if (i + 2 >= s.length()) {
                      throw new IllegalArgumentException(
                              "Incomplete % sequence at: " + i);
                  }
                  int d1 = Character.digit(s.charAt(i + 1), 16);
                  int d2 = Character.digit(s.charAt(i + 2), 16);
                  if (d1 == -1 || d2 == -1) {
                      throw new IllegalArgumentException(
                              "Invalid % sequence "
                                      + s.substring(i, i + 3)
                                      + " at " + i);
                  }
                  buf[len++] = (byte) ((d1 << 4) + d2);
                  i += 3;
              } while (i < s.length() && s.charAt(i) == '%');

              CharBuffer cb = charset.decode(ByteBuffer.wrap(buf, 0, len));
              len = cb.length();
              System.arraycopy(cb.array(), 0, str_buf, buf_len, len);
              buf_len += len;
              continue;
          } else {
              str_buf[buf_len] = c;
          }
          i++;
          buf_len++;
      }
      return new String(str_buf, 0, buf_len);
  }
}