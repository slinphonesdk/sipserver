package org.joinsip.core;

public class JsNetEvent {
    public static final int BUFFER_PAGESIZE = 2048;
    private byte[] buf;
    private int bufLen = 0;
    private String destHost;
    private int destPort;
    protected final Object mutex = new Object();
    private String srcHost;
    private int srcPort;

    public JsNetEvent() {
        remakeBuf(2048);
    }

    public JsNetEvent(int bufSize) {
        remakeBuf(bufSize);
    }

    public void copy(JsNetEvent src) {
        this.srcHost = src.getSrcHost();
        this.srcPort = src.getSrcPort();
        this.destHost = src.getDestHost();
        this.destPort = src.getDestPort();
        copyData(src.getData());
    }

    protected void remakeBuf(int size) {
        int newSize = (((size - 1) / 2048) + 1) * 2048;
        synchronized (this.mutex) {
            byte[] newBuf = new byte[newSize];
            this.bufLen = Math.min(this.bufLen, size);
            if (this.buf != null) {
                System.arraycopy(this.buf, 0, newBuf, 0, this.bufLen);
            }
            this.buf = newBuf;
        }
    }

    public void initData() {
        this.bufLen = 0;
    }

    public void copyData(byte[] src) {
        copyData(src, 0, src.length);
    }

    public void copyData(byte[] src, int srcPos, int length) {
        synchronized (this.mutex) {
            this.bufLen = 0;
            appendData(src, srcPos, length);
        }
    }

    public int getBufferSize() {
        byte[] tmpBuf = this.buf;
        if (tmpBuf != null) {
            return tmpBuf.length;
        }
        return 0;
    }

    public void appendData(byte[] src, int srcPos, int length) {
        synchronized (this.mutex) {
            if (this.buf.length < this.bufLen + length) {
                remakeBuf(this.bufLen + length);
            }
            System.arraycopy(src, srcPos, this.buf, this.bufLen, length);
            this.bufLen += length;
        }
    }

    public byte[] getData() {
        byte[] ret;
        synchronized (this.mutex) {
            ret = new byte[this.bufLen];
            System.arraycopy(this.buf, 0, ret, 0, this.bufLen);
        }
        return ret;
    }

    public int getLength() {
        return this.bufLen;
    }

    private static final String makeAddrString(String host, int port) {
        if (host != null) {
            return host + ":" + port;
        }
        return "[----]";
    }

    public String getDestAddr() {
        return makeAddrString(this.destHost, this.destPort);
    }

    public String getDestHost() {
        return this.destHost;
    }

    public void setDestHost(String destAddr) {
        this.destHost = destAddr;
    }

    public int getDestPort() {
        return this.destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public String getSrcAddr() {
        return makeAddrString(this.srcHost, this.srcPort);
    }

    public String getSrcHost() {
        return this.srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public int getSrcPort() {
        return this.srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public boolean isEventTo(String host, int port) {
        return host.equals(getDestHost()) && port == getDestPort();
    }

    public void route(String nextAddr, int nextPort) {
        this.srcHost = this.destHost;
        this.srcPort = this.destPort;
        this.destHost = nextAddr;
        this.destPort = nextPort;
    }

    public void switchSrcDest() {
        String tmpSrcHost = this.destHost;
        int tmpSrcPort = this.destPort;
        this.destHost = this.srcHost;
        this.destPort = this.srcPort;
        this.srcHost = tmpSrcHost;
        this.srcPort = tmpSrcPort;
    }

    public void replyToSrc() {
        this.destHost = this.srcHost;
        this.destPort = this.srcPort;
        this.srcHost = null;
        this.srcPort = -1;
    }

    public String getSummary() {
        return "udp : " + getSrcAddr() + " > " + getDestAddr();
    }

    public String toString() {
        return new String(this.buf, 0, this.bufLen);
    }
}
