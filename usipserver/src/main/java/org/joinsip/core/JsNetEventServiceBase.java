package org.joinsip.core;

import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class JsNetEventServiceBase implements Runnable, JsConsole {
    private JsConsole consoleOut = null;
    private Object mutex = new Object();
    protected JsNetEventSource netEventSource = null;
    private boolean runningFlag = false;
    private Thread thread = null;

    protected abstract void receiveEvent(JsNetEvent jsNetEvent);

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

    protected void sendEvent(JsNetEvent event) {
        this.netEventSource.send(event);
    }

    public void registEventSource(JsNetEventSource netEventSource) {
        this.netEventSource = netEventSource;
    }

    public JsNetEventSource getEventSource() {
        return this.netEventSource;
    }

    public void start(String host, int port) throws SocketException, UnknownHostException {
        synchronized (this.mutex) {
            if (this.runningFlag) {
                out(getClass().getSimpleName() + " - Already started.");
            } else {
                this.netEventSource.openPort(host, port);
                this.runningFlag = true;
                this.thread = new Thread(this);
                this.thread.start();
            }
        }
    }

    public void stop() {
        synchronized (this.mutex) {
            if (this.runningFlag) {
                this.netEventSource.closePort();
                this.runningFlag = false;
            }
        }
    }

    public boolean isRunning() {
        return this.runningFlag;
    }

    public void run() {
        JsNetEvent netEvent = new JsNetEvent(1500);
        while (this.runningFlag) {
            try {
                if (this.netEventSource.receive(netEvent) && this.netEventSource != null) {
                    receiveEvent(netEvent);
                }
            } catch (Exception e) {
                err(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
