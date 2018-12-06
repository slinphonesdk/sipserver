package org.joinsip.common;

import android.support.v4.view.MotionEventCompat;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public final class JsUtil {
  private static MessageDigest md;

  static {
    md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static final String getTimeString() {
    return DateFormat.getDateTimeInstance(3, 2).format(new Date());
  }

  public static final String getMD5(String key) {
    String ret;
    synchronized (md) {
      byte[] dat = key.getBytes();
      md.update(dat, 0, dat.length);
      ret = toHexString(md.digest());
    }
    return ret;
  }

  private static final String toHexString(byte[] buf) {
    StringBuilder s = new StringBuilder();
    for (byte b : buf) {
      int n = b & MotionEventCompat.ACTION_MASK;
      if (n < 16) {
        s.append('0');
      }
      s.append(Integer.toHexString(n).toLowerCase());
    }
    return s.toString();
  }

  public static final String makeCharLine(char c, int len) {
    StringBuilder buf = new StringBuilder(128);
    while (buf.length() < len) {
      buf.append(c);
    }
    return buf.toString();
  }

  public static final String[] split(String str, String splt) {
    StringTokenizer st = new StringTokenizer(str, splt);
    String[] ret = new String[st.countTokens()];
    for (int index = 0; index < ret.length; index++) {
      ret[index] = st.nextToken();
    }
    return ret;
  }

  public static String toAryString(String[] ary) {
    StringBuilder buf = new StringBuilder(128);
    for (int index = 0; index < ary.length; index++) {
      if (index != 0) {
        buf.append(" , ");
      }
      buf.append(ary[index]);
    }
    return buf.toString();
  }

  public static String toAryString(byte[] ary) {
    StringBuilder buf = new StringBuilder(128);
    for (int index = 0; index < ary.length; index++) {
      if (index != 0) {
        buf.append(",");
      }
      buf.append(ary[index]);
    }
    return buf.toString();
  }

  public static String toHtmlString(String text) {
    if (text == null) {
      return null;
    }
    return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
}
