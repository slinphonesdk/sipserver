package org.joinsip.core.transport;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.joinsip.core.JsNetEvent;
import org.joinsip.core.JsNetEventSource;

public class JsUdpEventSource extends JsNetEventSource {
    private DatagramSocket recvSocket = null;
    protected Object sendMutex = new Object();
    private DatagramSocket sendSocket = null;
    private DatagramPacket tPacket;

    public JsUdpEventSource() {
        byte[] data = new byte[1500];
        this.tPacket = new DatagramPacket(data, data.length);
    }

    public boolean send(JsNetEvent event) {
        boolean z = false;
        synchronized (this.sendMutex) {
            byte[] data = new byte[event.getLength()];
            DatagramPacket newPacket = new DatagramPacket(data, data.length);
            System.arraycopy(event.getData(), 0, newPacket.getData(), 0, event.getLength());
            newPacket.setLength(event.getLength());
            try {
                newPacket.setAddress(InetAddress.getByName(event.getDestHost()));
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            newPacket.setPort(event.getDestPort());
            try {
                this.sendSocket.send(newPacket);
                z = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return z;
    }

    public boolean receive(JsNetEvent event) {
        DatagramSocket tSocket = this.recvSocket;
        if (tSocket == null) {
            return false;
        }
        try {
            byte[] data = new byte[1500];
            this.tPacket = new DatagramPacket(data, data.length);
            tSocket.receive(this.tPacket);
            event.setSrcHost(this.tPacket.getAddress().getHostAddress().toString());
            event.setSrcPort(this.tPacket.getPort());
            event.setDestHost(tSocket.getLocalAddress().getHostAddress());
            event.setDestPort(tSocket.getLocalPort());
            event.copyData(this.tPacket.getData(), 0, this.tPacket.getLength());

            if (event.toString().trim().length() == 0) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected boolean _openPort(String host, int port) throws SocketException, UnknownHostException {
        if (host == null) {
            host = "0.0.0.0";
        }
        this.recvSocket = new DatagramSocket(port, InetAddress.getByName(host));
        this.sendSocket = this.recvSocket;
        return true;
    }

    protected boolean _closePort() {
        this.recvSocket.close();
        this.recvSocket = null;
        return false;
    }

    public String getLocalAddrString() {
        return this.recvSocket == null ? null : this.recvSocket.getLocalAddress().getHostName() + ":" + this.recvSocket.getLocalPort();
    }
}
