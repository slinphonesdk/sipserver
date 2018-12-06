package org.joinsip.core;

import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class JsNetEventSource {
    private JsConsole consoleOut = null;
    private String myHost = null;
    private int myPort = -1;

    protected abstract boolean _closePort();

    protected abstract boolean _openPort(String str, int i) throws SocketException, UnknownHostException;

    public abstract boolean receive(JsNetEvent jsNetEvent);

    public abstract boolean send(JsNetEvent jsNetEvent);

    public void setConsoleOut(JsConsole consoleOut) {
        this.consoleOut = consoleOut;
    }

    public void out(String text) {
        JsConsole conOut = this.consoleOut;
        if (conOut != null) {
            conOut.out(text);
        } else {
            System.out.println(text);
        }
    }

    public void err(String text) {
        JsConsole conOut = this.consoleOut;
        if (conOut != null) {
            conOut.err(text);
        } else {
            System.err.println(text);
        }
    }

    public boolean openPort(String host, int port) throws SocketException, UnknownHostException {
        boolean ret = _openPort(host, port);
        if (ret) {
            this.myHost = host;
            this.myPort = port;
        } else {
            this.myHost = null;
            this.myPort = -1;
        }
        return ret;
    }

    public boolean closePort() {
        this.myHost = null;
        this.myPort = -1;
        return _closePort();
    }

    public String getMyHost() {
        return this.myHost;
    }

    public int getMyPort() {
        return this.myPort;
    }

    public String getLocalAddrString() {
        return (this.myHost == null || this.myPort <= 0) ? null : this.myHost + ":" + this.myPort;
    }
}
