package org.joinsip.impl.proxy;

import org.joinsip.common.JsSystem;
import org.joinsip.core.JsAddress;
import org.joinsip.core.JsCoreException;
import org.joinsip.core.JsHeader;
import org.joinsip.core.JsHeaderAddressData;
import org.joinsip.core.JsHeaderIntData;
import org.joinsip.core.JsSipEvent;
import org.joinsip.core.JsSipEventServiceBase;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class JsSipProxyService extends JsSipEventServiceBase {
    public static final String CONFIG_SIPDOMAIN = "SIPDOMAIN";
    protected JsAddress myUri;
    public final JsSimpleRegistrar registrar;
    private String sipDomain;
    private JsSipEvent proxyEvent;

    public JsSipProxyService() {
        this.sipDomain = null;
        this.myUri = null;
        this.myUri = new JsAddress();
        this.registrar = new JsSimpleRegistrar();
        this.registrar.setConsoleOut(this);
    }

    public void setSipDomain(String sipDomain) {
        this.sipDomain = sipDomain;
    }

    public void setSipDomain(String sipDomain, int localPort) {
        this.sipDomain = sipDomain;
        this.myUri.setHost(sipDomain);
        this.myUri.setPort(localPort);
    }

    public JsAddress getSipDomainUri() {
        JsAddress uri = new JsAddress();
        try {
            uri.parse("sip:" + this.sipDomain);
        } catch (JsCoreException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public JsSimpleRegistrar getRegistrar() {
        return this.registrar;
    }

    protected void receiveSipEvent(JsSipEvent sipEvent) throws JsCoreException {
        boolean eventToMe = false;
        JsAddress requestUri = sipEvent.getRequestUri();
        if (requestUri != null && requestUri.getHost().equals(this.sipDomain)) {
            eventToMe = true;
        }
        JsAddress contactUri;
        if (sipEvent.isRegister() && eventToMe) {
            JsAddress toUri = ((JsHeaderAddressData) sipEvent.getHeader(JsHeader.TO, 0)).getAddress();
            if (toUri.getUser() == null) {
                sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_400));
                return;
            }
            JsHeaderAddressData contactHeader = (JsHeaderAddressData) sipEvent.getHeader(JsHeader.CONTACT, 0);
            contactUri = contactHeader.getAddress();
            int expires = 3600;
            if (sipEvent.hasHeader(JsHeader.EXPIRES)) {
                expires = ((JsHeaderIntData) sipEvent.getHeader(JsHeader.EXPIRES, 0)).getIntValue();
            } else {
                try {
                    expires = Integer.parseInt(contactHeader.getParameter("expires"));
                } catch (Exception e) { }
            }
            if (!contactUri.isAsterisk()) {
                this.registrar.register(toUri, contactUri, expires);
            }
            sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_200));
        } else if (sipEvent.isRequest()) {
            JsAddress targetUri = sipEvent.getRequestUri();
            String targetUser = targetUri.getUser();
            String targetHost = targetUri.getHost();
            if (targetHost.equals(this.sipDomain) || targetHost.equals(getEventSource().getMyHost())) {
                if (targetUser == null) {
                    targetUser = ((JsHeaderAddressData) sipEvent.getHeader(JsHeader.TO, 0)).getAddress().getUser();
                }
                if (targetUser == null) {
                    sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_400));
                    return;
                }
                contactUri = this.registrar.getInfo(targetUser);
                if (contactUri == null) {
                    if (!sipEvent.isAck()) {
                        sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_404));
                    }
                    return;
                } else if (contactUri.getParameterValue("R") != null) {
                    JsHeaderAddressData routeHeader = new JsHeaderAddressData(JsHeader.ROUTE);
                    routeHeader.setAddress(contactUri);
                    sipEvent.insertHeaderBefore(routeHeader, JsHeader.ROUTE);
                    System.err.println("ROUTE TO = " + routeHeader.getAddress().getHost());
                    proxyEvent = sipEvent.createProxyRequestEvent(this.myUri);
                    proxyEvent.removeHeader(JsHeader.ROUTE, 0);
                    proxyEvent.removeHeader(JsHeader.ROUTE, 0);
                    sendSipEvent(proxyEvent);
                    return;
                } else {
                    sipEvent.setRequestUri(contactUri);
                    if (sipEvent.isInvite()) {
                    }
                }
            } else {
                try {
                    targetHost = targetUri.getHost();
                    InetAddress.getByName(targetHost);
                } catch (UnknownHostException e2) {
                    JsSystem.err.println("UnknownHostException : " + targetHost);
                    sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_404));
                    return;
                }
            }
            sendSipEvent(sipEvent.createProxyRequestEvent(this.myUri));
        } else if (sipEvent.isResponse()) {
            if (!sipEvent.isEventToMe(this.myUri)) {
                proxyEvent = sipEvent.createProxyResponseEvent();
                if (proxyEvent != null) {
                    sendSipEvent(proxyEvent);
                }
            }
        }
    }

    public String showStatus() {
        StringBuilder buf = new StringBuilder();
        buf.append("[Status]\n");
        if (this.netEventSource == null || this.myUri == null) {
            buf.append("  sip service is not running.\n");
        } else {
            buf.append("  Domain: " + this.sipDomain + "\n");
            buf.append("  LocalIp: " + this.netEventSource.getMyHost() + "\n");
            buf.append("  LocalPort: " + this.myUri.getPort());
        }
        return buf.toString();
    }

    public String showRegister() {
        StringBuilder buf = new StringBuilder();
        buf.append("[Register]\n");
        buf.append(this.registrar.toString());
        return buf.toString();
    }
}
