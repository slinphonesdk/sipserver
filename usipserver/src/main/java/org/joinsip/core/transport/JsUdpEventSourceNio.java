package org.joinsip.core.transport;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.joinsip.common.JsSystem;
import org.joinsip.common.JsUtil;
import org.joinsip.core.JsNetEvent;
import org.joinsip.core.JsNetEventSource;

public class JsUdpEventSourceNio extends JsNetEventSource {
    public static final int MAX_PACKET_SIZE = 65507;
    private ByteBuffer buffer = null;
    private DatagramChannel datagramChannel = null;
    protected Object sendMutex = new Object();
    private DatagramSocket socket = null;
    private byte[] tmpData = new byte[1500];

    public boolean send(JsNetEvent event) {
        synchronized (this.sendMutex) {
            SocketAddress target = new InetSocketAddress(event.getDestHost(), event.getDestPort());
            this.buffer.clear();
            this.buffer.put(event.getData(), 0, event.getLength());
            this.buffer.flip();
            try {
                this.datagramChannel.send(this.buffer, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.buffer.clear();
        }
        return false;
    }

    public boolean receive(JsNetEvent event) {
        try {
            SocketAddress client = this.datagramChannel.receive(this.buffer);
            int size = this.buffer.position();
            this.buffer.flip();
            this.buffer.get(this.tmpData, 0, size);
            this.buffer.clear();
            event.copyData(this.tmpData, 0, size);
            String[] cols = JsUtil.split(client.toString().substring(1), ":");
            event.setSrcHost(cols[0]);
            event.setSrcPort(Integer.parseInt(cols[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected boolean _openPort(String host, int port) throws SocketException, UnknownHostException {
        try {
            this.datagramChannel = DatagramChannel.open();
            this.socket = this.datagramChannel.socket();
            SocketAddress address = new InetSocketAddress(host, port);
            this.socket.bind(address);
            this.buffer = ByteBuffer.allocateDirect(1500);
            JsSystem.err.println("openPort() - nio " + address);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean _closePort() {
        try {
            this.datagramChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
