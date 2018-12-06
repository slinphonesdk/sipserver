package org.joinsip.core;

import java.util.StringTokenizer;
import org.joinsip.common.JsUtil;
import org.joinsip.core.body.JsSdpData;

public class JsSipEvent extends JsTextBaseProtocolEvent {
    public static final int ACK = -2;
    public static final int REQ_BYE = -9;
    public static final int REQ_CANCEL = -10;
    public static final int REQ_INFO = -11;
    public static final int REQ_INVITE = -5;
    public static final int REQ_MESSAGE = -12;
    public static final int REQ_NOTIFY = -7;
    public static final int REQ_OPTIONS = -13;
    public static final int REQ_REGISTER = -4;
    public static final int REQ_SUBSCRIBE = -8;
    public static final int REQ_UPDATE = -6;
    public static final int REQ_XXX = -3;
    public static final int RES_100 = 100;
    public static final int RES_180 = 180;
    public static final int RES_200 = 200;
    public static final int RES_400 = 400;
    public static final int RES_404 = 404;
    public static final int RES_405 = 405;
    public static final int RES_406 = 406;
    public static final int RES_480 = 480;
    public static final int RES_481 = 481;
    public static final int RES_486 = 486;
    public static final int RES_487 = 487;
    public static final int RES_488 = 488;
    public static final int RES_500 = 500;
    public static final int RES_OFFSET = 1000;
    public static final int RES_XXX = 999;
    public static final String sipVer = "SIP/2.0";
    private boolean myEvent = false;

    public JsSipEvent(JsNetEvent netEvent) {
        super(netEvent);
    }

    public void setMyEvent(boolean flag) {
        this.myEvent = flag;
    }

    public boolean isMyEvent() {
        return this.myEvent;
    }

    public boolean isEventToMe(JsAddress myUri) {
        String myHost;
        String evHost;
        if (isRequest()) {
            JsAddress requestUri = getRequestUri();
            myHost = myUri.getHost();
            String myUser = myUri.getUser();
            evHost = requestUri.getHost();
            String evUser = requestUri.getUser();
            if (myHost.equals(evHost) && myUri.isHostUri() && requestUri.isHostUri()) {
                return true;
            }
            if (!(evUser == null || myUser == null || !myUser.equals(evUser))) {
                return true;
            }
        } else if (isResponse() && countHeader(JsHeader.VIA) == 1) {
            JsAddress viaUri = ((JsHeaderViaData) getHeader(JsHeader.VIA, 0)).getSentBy();
            myHost = myUri.getHost();
            evHost = viaUri.getHost();
            if (!(evHost == null || myHost == null || !myHost.equals(evHost))) {
                return true;
            }
        }
        return false;
    }

    public void setBody(JsSdpData sdp) {
        super.setBody(sdp.toString(), "application/sdp");
    }

    public boolean isRegister() {
        return this.startLineIndex == -4;
    }

    public boolean isInvite() {
        return this.startLineIndex == -5;
    }

    public boolean isUpdate() {
        return this.startLineIndex == -6;
    }

    public boolean isSubscribe() {
        return this.startLineIndex == -8;
    }

    public boolean isNotify() {
        return this.startLineIndex == -7;
    }

    public boolean isBye() {
        return this.startLineIndex == -9;
    }

    public boolean isAck() {
        return this.startLineIndex == -2;
    }

    public boolean isCancel() {
        return this.startLineIndex == -10;
    }

    public boolean isInfo() {
        return this.startLineIndex == -11;
    }

    public boolean isMessage() {
        return this.startLineIndex == -12;
    }

    protected int getStartLineIndex(String startLine) {
        try {
            String[] col = JsTextBaseProtocolEvent.split(startLine, " ");
            if (col.length < 2) {
                return 0;
            }
            if (startLine.startsWith("SIP/") || startLine.startsWith("SIPS/")) {
                return Integer.valueOf(col[1]).intValue();
            }
            return getMethodIndex(col[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getMethodName() {
        if (isRequest()) {
            return super.getMethodName();
        }
        if (isResponse()) {
            return ((JsHeaderIntData) getHeader(JsHeader.CSEQ, 0)).getValue();
        }
        return null;
    }

    public JsSipEvent createResponse(int statusCode) throws JsCoreException {
        if (isRequest()) {
            JsSipEvent response = new JsSipEvent(this);
            response.setStatusCode(statusCode);
            response.setMyEvent(true);
            response.replyToSrc();
            response.removeHeader(JsHeader.MAX_FORWARDS, 0);
            if (response.hasHeader(JsHeader.VIA)) {
                JsHeaderViaData tmpVia = (JsHeaderViaData) response.getHeader(JsHeader.VIA, 0);
                String rport = tmpVia.getRPort();
                if (rport != null && rport.length() == 0) {
                    tmpVia.setRPort("" + tmpVia.getSentBy().getPort());
                    updateHeader(tmpVia, JsHeader.VIA, 0);
                }
            }
            response.removeHeader(JsHeader.ACCEPT, 0);
            response.removeHeader(JsHeader.USER_AGENT, 0);
            response.removeHeader(JsHeader.CONTACT, 0);
            response.removeBody();
            return response;
        }
        JsCoreException sce = new JsCoreException();
        sce.addTraceMessage(0, "cant create response event");
        throw sce;
    }

    public String getResponseMessage(int statusCode) {
        switch (statusCode) {
            case RES_100 /*100*/:
                return "Trying";
            case RES_180 /*180*/:
                return "Ringing";
            case RES_200 /*200*/:
                return "OK";
            case RES_400 /*400*/:
                return "Bad Request";
            case RES_404 /*404*/:
                return "Not Found";
            case RES_405 /*405*/:
                return "Method Not Allowed";
            case RES_406 /*406*/:
                return "Not Acceptable";
            case RES_480 /*480*/:
                return "Temporarily Unavailable";
            case RES_481 /*481*/:
                return "Call/Transaction Does Not Exist";
            case RES_486 /*486*/:
                return "Busy Here";
            case RES_487 /*487*/:
                return "Request Terminated";
            case RES_488 /*488*/:
                return "Not Acceptable Here";
            case RES_500 /*500*/:
                return "Server Internal Error";
            default:
                return "";
        }
    }

    public void addViaHeader(String host, int port) {
        JsHeaderIntData cseqHeader = (JsHeaderIntData) getHeader(JsHeader.CSEQ, 0);
        JsHeaderData callIdHeader = getHeader(JsHeader.CALL_ID, 0);
        JsHeaderViaData viaHeader = new JsHeaderViaData();
        String branchId = "z9hG4bK-" + JsUtil.getMD5(callIdHeader.getValue() + cseqHeader.getIntValue());
        viaHeader.setTransport(JsHeader.UDP);
        JsAddress sentBy = new JsAddress();
        sentBy.setHost(host);
        sentBy.setPort(port);
        viaHeader.setSentBy(sentBy);
        viaHeader.setBranch(branchId);
        insertHeaderBefore(viaHeader, JsHeader.VIA);
    }

    public JsSipEvent createProxyRequestEvent(JsAddress proxyUri) throws JsCoreException {
        JsAddress newRequestUri;
        JsSipEvent newEvent = new JsSipEvent(this);
        newEvent.addViaHeader(proxyUri.getHost(), proxyUri.getPort());
        int recordRouteIndex = newEvent.getHeaderIndex(JsHeader.RECORD_ROUTE, 0);
        String ftag = ((JsHeaderAddressData) newEvent.getHeader(JsHeader.FROM, 0)).getParameter("tag");
        JsAddress newRecRouteAddr = new JsAddress();
        newRecRouteAddr.setHost(proxyUri.getHost());
        newRecRouteAddr.setPort(proxyUri.getPort());
        newRecRouteAddr.setTagValue("ftag", ftag);
        newRecRouteAddr.setTagValue("lr", "on");
        JsHeaderAddressData newRecRoute = new JsHeaderAddressData(JsHeader.RECORD_ROUTE);
        newRecRoute.setAddress(newRecRouteAddr);
        if (recordRouteIndex < 0) {
            newEvent.insertHeaderBefore(newRecRoute, JsHeader.VIA);
        } else {
            newEvent.insertHeaderBefore(newRecRoute, JsHeader.RECORD_ROUTE);
        }
        JsAddress nowRequestUri = newEvent.getRequestUri();
        newEvent.popRouteHeader(proxyUri);
        if (newEvent.hasHeader(JsHeader.ROUTE)) {
            newRequestUri = ((JsHeaderAddressData) newEvent.getHeader(JsHeader.ROUTE, 0)).getAddress();
            newRequestUri.setUser(nowRequestUri.getUser());
        } else {
            newRequestUri = newEvent.getRequestUri();
        }
        newEvent.setRequestUri(newRequestUri);
        newEvent.route(newRequestUri.getHost(), newRequestUri.getPort());
        if (newEvent.hasHeader(JsHeader.MAX_FORWARDS)) {
            JsHeaderIntData maxForwareds = (JsHeaderIntData) newEvent.getHeader(JsHeader.MAX_FORWARDS, 0);
            maxForwareds.setIntValue(maxForwareds.getIntValue() - 1);
            newEvent.updateHeader(maxForwareds, JsHeader.MAX_FORWARDS, 0);
        }
        return newEvent;
    }

    public JsHeaderAddressData popRouteHeader(JsAddress targetUri) {
        if (!hasHeader(JsHeader.ROUTE)) {
            return null;
        }
        JsAddress routeUri = ((JsHeaderAddressData) getHeader(JsHeader.ROUTE, 0)).getAddress();
        if (routeUri.getHost().equals(targetUri.getHost()) && routeUri.getPort() == targetUri.getPort()) {
            return (JsHeaderAddressData) removeHeader(JsHeader.ROUTE, 0);
        }
        return null;
    }

    public JsSipEvent createProxyResponseEvent() throws JsCoreException {
        JsSipEvent newEvent = new JsSipEvent(this);
        if (countHeader(JsHeader.VIA) < 2) {
            return null;
        }
        newEvent.removeHeader(JsHeader.VIA, 0);
        if (!newEvent.hasHeader(JsHeader.VIA)) {
            return newEvent;
        }
        JsHeaderViaData viaLine = null;
        try {
            viaLine = (JsHeaderViaData) newEvent.getHeader(JsHeader.VIA, 0);
            JsAddress sentBy = viaLine.getSentBy();
            newEvent.route(sentBy.getHost(), sentBy.getPort());
            return newEvent;
        } catch (Exception e) {
            JsCoreException sce = new JsCoreException();
            sce.addTraceMessage(0, "invalid via: uri : " + viaLine);
            throw sce;
        }
    }

    protected void parse(String signal) throws JsCoreException {
        initParam();
        StringTokenizer st = new StringTokenizer(signal.toString(), "\n");
        if (st.hasMoreTokens()) {
            String line;
            setStartLine(st.nextToken().trim());
            while (st.hasMoreTokens()) {
                line = st.nextToken().trim();
                if (line.length() == 0) {
                    break;
                }
                try {
                    JsHeaderData[] newHeaders = JsHeader.parse(line);
                    for (JsHeaderData appendHeader : newHeaders) {
                        appendHeader(appendHeader);
                    }
                } catch (Exception e) {
                    JsCoreException se = new JsCoreException();
                    se.setReasonObject(e);
                    se.addMessage("ShSipEvent.parse() : invalid header. [" + line + "]");
                    se.setStackTrace(e.getStackTrace());
                    throw se;
                }
            }
            while (st.hasMoreTokens()) {
                line = st.nextToken().trim();
                if (line.length() != 0 || st.hasMoreTokens()) {
                    appendBody(line + "\r\n");
                } else {
                    return;
                }
            }
            return;
        }
        this.startLineIndex = 0;
    }

    protected int getMethodIndex(String methodName) {
        if (methodName == null) {
            return 0;
        }
        if (methodName.equals("REGISTER")) {
            return -4;
        }
        if (methodName.equals("INVITE")) {
            return -5;
        }
        if (methodName.equals("UPDATE")) {
            return -6;
        }
        if (methodName.equals("CANCEL")) {
            return -10;
        }
        if (methodName.equals("OPTIONS")) {
            return -13;
        }
        if (methodName.equals("MESSAGE")) {
            return -12;
        }
        if (methodName.equals("INFO")) {
            return -11;
        }
        if (methodName.equals("SUBSCRIBE")) {
            return -8;
        }
        if (methodName.equals("NOTIFY")) {
            return -7;
        }
        if (methodName.equals("BYE")) {
            return -9;
        }
        if (methodName.equals("ACK")) {
            return -2;
        }
        return 0;
    }

    public static final String getSipUri(String line) {
        int at_index = line.indexOf(64);
        int start_index = line.indexOf(60) + 1;
        int end_index = line.indexOf(62);
        int sc_end_index = line.indexOf(59);
        if (end_index == -1 && end_index == -1) {
            end_index = line.length();
        }
        if (end_index > sc_end_index && sc_end_index != -1) {
            end_index = sc_end_index;
        }
        sc_end_index = line.lastIndexOf(58);
        if (end_index > sc_end_index && at_index < sc_end_index && sc_end_index != -1) {
            end_index = sc_end_index;
        }
        if (start_index < 0 || end_index < 0) {
            return null;
        }
        return line.substring(start_index, end_index);
    }

    public static final String getLtGt(String format) {
        int start_index = format.indexOf(60) + 1;
        int end_index = format.indexOf(62);
        if (start_index == 0) {
        }
        if (end_index == -1) {
            end_index = format.length();
        }
        if (start_index < 0 || end_index < 0) {
            return null;
        }
        return format.substring(start_index, end_index);
    }

    public String getSummary() {
        String summary = "";
        if (isRequest()) {
            String requestUri = getRequestUri().toString(false);
            if (requestUri.indexOf(59) >= 0) {
                requestUri = requestUri.substring(0, requestUri.indexOf(59));
            }
            return getMethodName() + " " + requestUri;
        } else if (isResponse() && hasHeader(JsHeader.CSEQ)) {
            return getStartLine() + " [" + getHeader(JsHeader.CSEQ, 0).getValue() + "]";
        } else {
            return summary;
        }
    }

    protected String getProtocolVer() {
        return "SIP/2.0";
    }
}
