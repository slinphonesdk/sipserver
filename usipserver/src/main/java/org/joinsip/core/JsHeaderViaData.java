package org.joinsip.core;

import org.joinsip.common.JsUtil;

public class JsHeaderViaData extends JsHeaderData {
    public static final String BRANCH = "branch";
    public static final String MADDR = "maddr";
    public static final String RECEIVED = "received";
    public static final String RPORT = "rport";
    public static final String TTL = "ttl";
    private JsAddress sentBy = null;
    private String transport = JsHeader.UDP;

    public JsHeaderViaData(JsHeader headerType) {
        super(headerType);
    }

    public JsHeaderViaData() {
        super(JsHeader.VIA);
    }

    public void copy(JsHeaderData src) {
        super.copy(src);
        if (src instanceof JsHeaderViaData) {
            JsHeaderViaData srcVia = (JsHeaderViaData) src;
            this.sentBy = srcVia.getSentBy();
            this.transport = srcVia.getTransport();
        }
    }

    public Object clone() {
        JsHeaderViaData newObj = new JsHeaderViaData();
        newObj.copy(this);
        return newObj;
    }

    public void setValue(String value) throws JsCoreException {
        super.setValue(value);
        String[] tokens = JsUtil.split(this.value.toString(), "/");
        tokens[2] = tokens[2].trim();
        if (tokens[2].startsWith(JsHeader.TCP)) {
            this.transport = JsHeader.TCP;
        } else if (tokens[2].startsWith(JsHeader.UDP)) {
            this.transport = JsHeader.UDP;
        } else if (tokens[2].startsWith(JsHeader.TLS)) {
            this.transport = JsHeader.TLS;
        }
        int sentByOffset = this.value.indexOf(this.transport) + this.transport.length();
        if (this.sentBy == null) {
            this.sentBy = new JsAddress();
        }
        this.sentBy.parse(this.value.substring(sentByOffset));
        this.value.setLength(0);
    }

    public JsAddress getSentBy() {
        return this.sentBy == null ? null : (JsAddress) this.sentBy.clone();
    }

    public void setSentBy(JsAddress sentBy) {
        this.sentBy = (JsAddress) sentBy.clone();
    }

    public String getTransport() {
        return this.transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setBranch(String branch) {
        setParameter(BRANCH, branch);
    }

    public String getBranch() {
        return getParameter(BRANCH);
    }

    public void setMAddr(String maddr) {
        setParameter(MADDR, maddr);
    }

    public String getMAddr() {
        return getParameter(MADDR);
    }

    public void setReceived(String received) {
        setParameter(RECEIVED, received);
    }

    public String getReceived() {
        return getParameter(RECEIVED);
    }

    public void setRPort(String rport) {
        setParameter(RPORT, rport);
    }

    public String getRPort() {
        return getParameter(RPORT);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(this.headerType.toString());
        buf.append(": ");
        buf.append("SIP/2.0");
        buf.append('/').append(this.transport).append(' ');
        buf.append(this.sentBy.getHost()).append(':');
        buf.append(this.sentBy.getPort());
        appendParameters(buf);
        return buf.toString();
    }
}
