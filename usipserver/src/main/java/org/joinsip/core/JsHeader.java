package org.joinsip.core;

import java.lang.reflect.Constructor;
import org.joinsip.common.JsUtil;

public final class JsHeader {
    public static final JsHeader ACCEPT = new JsHeader("Accept", JsHeaderData.class);
    public static final JsHeader ACCEPT_ENCODING = new JsHeader("Accept-Encoding", JsHeaderData.class);
    public static final JsHeader ACCEPT_LANGUAGE = new JsHeader("Accept-Language", JsHeaderData.class);
    public static final JsHeader ALERT_INFO = new JsHeader("Alert-Info", JsHeaderData.class);
    public static final JsHeader ALLOW = new JsHeader("Allow", JsHeaderData.class);
    public static final JsHeader ALLOW_EVENTS = new JsHeader("Allow-Events", JsHeaderData.class);
    public static final JsHeader AUTHENTICATION_INFO = new JsHeader("Authentication-Info", JsHeaderData.class);
    public static final JsHeader AUTHORIZATION = new JsHeader("Authorization", JsHeaderData.class);
    public static final JsHeader CALL_ID = new JsHeader("Call-ID", JsHeaderData.class);
    public static final JsHeader CALL_INFO = new JsHeader("Call-Info", JsHeaderData.class);
    public static final JsHeader CONTACT = new JsHeader("Contact", JsHeaderAddressData.class);
    public static final JsHeader CONTENT_DISPOSITION = new JsHeader("Content-Disposition", JsHeaderData.class);
    public static final JsHeader CONTENT_ENCODING = new JsHeader("Content-Encoding", JsHeaderData.class);
    public static final JsHeader CONTENT_LANGUAGE = new JsHeader("Content-Language", JsHeaderData.class);
    public static final JsHeader CONTENT_LENGTH = new JsHeader("Content-Length", JsHeaderIntData.class);
    public static final JsHeader CONTENT_TYPE = new JsHeader("Content-Type", JsHeaderData.class);
    public static final JsHeader CSEQ = new JsHeader("CSeq", JsHeaderIntData.class);
    public static final JsHeader DATE = new JsHeader("Date", JsHeaderData.class);
    public static final JsHeader ERROR_INFO = new JsHeader("Error-Info", JsHeaderData.class);
    public static final JsHeader EVENT = new JsHeader("Event", JsHeaderData.class);
    public static final JsHeader EXPIRES = new JsHeader("Expires", JsHeaderIntData.class);
    public static final JsHeader FROM = new JsHeader("From", JsHeaderAddressData.class);
    public static final JsHeader IN_REPLY_TO = new JsHeader("In-Reply-To", JsHeaderData.class);
    public static final JsHeader MAX_FORWARDS = new JsHeader("Max-Forwards", JsHeaderIntData.class);
    public static final JsHeader MIME_VERSION = new JsHeader("Mime-Version", JsHeaderData.class);
    public static final JsHeader MIN_EXPIRES = new JsHeader("Min-Expires", JsHeaderIntData.class);
    public static final JsHeader MIN_SE = new JsHeader("Min-SE", JsHeaderIntData.class);
    public static final JsHeader ORGANIZATION = new JsHeader("Organization", JsHeaderData.class);
    public static final JsHeader PRIORITY = new JsHeader("Priority", JsHeaderData.class);
    public static final String PROTCOL = "SIP/2.0";
    public static final JsHeader PROXY_AUTHENTICATE = new JsHeader("Proxy-Authenticate", JsHeaderData.class);
    public static final JsHeader PROXY_AUTHORIZATION = new JsHeader("Proxy-Authorization", JsHeaderData.class);
    public static final JsHeader PROXY_REQUIRE = new JsHeader("Proxy-Require", JsHeaderData.class);
    public static final JsHeader RACK = new JsHeader("RAck", JsHeaderData.class);
    public static final JsHeader REASON = new JsHeader("Reason", JsHeaderData.class);
    public static final JsHeader RECORD_ROUTE = new JsHeader("Record-Route", JsHeaderAddressData.class);
    public static final JsHeader REFERRED_BY = new JsHeader("Referred-By", JsHeaderData.class);
    public static final JsHeader REPLACES = new JsHeader("Replaces", JsHeaderData.class);
    public static final JsHeader REPLY_TO = new JsHeader("Reply-To", JsHeaderData.class);
    public static final JsHeader REQUIRE = new JsHeader("Require", JsHeaderData.class);
    public static final JsHeader RETRY_AFTER = new JsHeader("Retry-After", JsHeaderIntData.class);
    public static final JsHeader ROUTE = new JsHeader("Route", JsHeaderAddressData.class);
    public static final JsHeader RSEQ = new JsHeader("RSeq", JsHeaderData.class);
    public static final JsHeader SERVER = new JsHeader("Server", JsHeaderData.class);
    public static final JsHeader SESSION_EXPIRES = new JsHeader("Session-Expires", JsHeaderIntData.class);
    public static final JsHeader SIP_ETAG = new JsHeader("SIP-ETag", JsHeaderData.class);
    public static final JsHeader SIP_IF_MATCH = new JsHeader("SIP-If-Match", JsHeaderData.class);
    public static final JsHeader SUBJECT = new JsHeader("Subject", JsHeaderData.class);
    public static final JsHeader SUBSCRIPTION_STATE = new JsHeader("Subscription-State", JsHeaderData.class);
    public static final JsHeader SUPPORTED = new JsHeader("Supported", JsHeaderData.class);
    public static final String TCP = "TCP";
    public static final JsHeader TIMESTAMP = new JsHeader("Timestamp", JsHeaderIntData.class);
    public static final String TLS = "TLS";
    public static final JsHeader TO = new JsHeader("To", JsHeaderAddressData.class);
    public static final String UDP = "UDP";
    public static final JsHeader UNSUPPORTED = new JsHeader("Unsupported", JsHeaderData.class);
    public static final JsHeader USER_AGENT = new JsHeader("User-Agent", JsHeaderData.class);
    public static final JsHeader VIA = new JsHeader("Via", JsHeaderViaData.class);
    public static final JsHeader WARNING = new JsHeader("Warning", JsHeaderData.class);
    public static final JsHeader WWW_AUTHENTICATE = new JsHeader("WWW-Authenticate", JsHeaderData.class);
    private static final JsHeader[] headers = new JsHeader[]{VIA, RECORD_ROUTE, ROUTE, TO, FROM, CALL_ID, CONTACT, CSEQ, MAX_FORWARDS, CONTENT_LENGTH, CONTENT_TYPE, ALLOW, EXPIRES, USER_AGENT, REASON, CALL_INFO, ACCEPT, ACCEPT_ENCODING, ACCEPT_LANGUAGE, ALERT_INFO, ALLOW_EVENTS, AUTHENTICATION_INFO, AUTHORIZATION, CONTENT_DISPOSITION, CONTENT_ENCODING, CONTENT_LANGUAGE, DATE, ERROR_INFO, EVENT, IN_REPLY_TO, MIME_VERSION, MIN_EXPIRES, MIN_SE, ORGANIZATION, PRIORITY, PROXY_AUTHENTICATE, PROXY_AUTHORIZATION, PROXY_REQUIRE, RACK, REFERRED_BY, REPLACES, REPLY_TO, REQUIRE, RETRY_AFTER, RSEQ, SERVER, SESSION_EXPIRES, SIP_ETAG, SIP_IF_MATCH, SUBJECT, SUBSCRIPTION_STATE, SUPPORTED, TIMESTAMP, UNSUPPORTED, WARNING, WWW_AUTHENTICATE};
    private final Constructor<?> constructor;
    private final Object[] constructorParam = new Object[]{this};
    private final String name;

    private JsHeader(String name, Class<?> dataClass) {
        this.name = name;
        Constructor<?>[] constructors = dataClass.getConstructors();
        Constructor<?> tmpConstructor = null;
        for (Constructor<?> tmpConstructor2 : constructors) {
            if (tmpConstructor2.getParameterTypes().length == 1 && tmpConstructor2.getParameterTypes()[0].equals(JsHeader.class)) {
               tmpConstructor = tmpConstructor2;
                break;
            }
        }
        this.constructor = tmpConstructor;
    }

    public String toString() {
        return this.name;
    }

    public JsHeaderData newInstance() {
        try {
            return (JsHeaderData) this.constructor.newInstance(this.constructorParam);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsHeader getHeader(String headerName) {
        int count = headers.length;
        for (int index = 0; index < count; index++) {
            if (headerName.equalsIgnoreCase(headers[index].toString())) {
                return headers[index];
            }
        }
        return new JsHeader(headerName, JsHeaderData.class);
    }

    public static JsHeaderData[] parse(String line) throws JsCoreException {
        int cIndex = line.indexOf(58);
        if (cIndex > 0) {
            JsHeader header = getHeader(line.substring(0, cIndex).trim());
            String tmpValue = line.substring(cIndex + 1).trim();
            if (header != null) {
                JsHeaderData[] ret;
                if ((header == VIA || header == RECORD_ROUTE || header == ROUTE) && tmpValue.indexOf(44) >= 0) {
                    String[] tmpValues = JsUtil.split(tmpValue, ",");
                    ret = new JsHeaderData[tmpValues.length];
                    for (int index = 0; index < ret.length; index++) {
                        ret[index] = header.newInstance();
                        ret[index].setValue(tmpValues[index]);
                    }
                    return ret;
                }
                ret = new JsHeaderData[]{header.newInstance()};
                ret[0].setValue(tmpValue);
                return ret;
            }
        }
        return null;
    }
}
