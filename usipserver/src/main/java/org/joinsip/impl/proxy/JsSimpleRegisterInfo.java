package org.joinsip.impl.proxy;

import android.util.Log;

import org.joinsip.core.JsAddress;
import org.joinsip.core.JsSipEvent;

public class JsSimpleRegisterInfo {
    public final JsAddress contactUri;
    private long expiresMillis = 0;
    public final JsAddress toUri;

    public JsSimpleRegisterInfo(JsAddress toUri, JsAddress contactUri) {
        this.toUri = toUri;
        this.contactUri = contactUri;
    }

    public JsAddress getContactUri() {
        return this.contactUri;
    }

    public JsAddress getToUri() {
        return this.toUri;
    }

    public void setExpires(int expires) {
        this.expiresMillis = System.currentTimeMillis() + ((long) (expires * JsSipEvent.RES_OFFSET));
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expiresMillis;
    }

    public long getExpiresMillis() {
        return this.expiresMillis;
    }

    public String toDispString() {
        long remainsTimeMillis = this.expiresMillis - System.currentTimeMillis();
        String ret = "[" + this.toUri.getUser() + "] -> " + this.contactUri.getHost() + ":" + this.contactUri.getPort();
        if (remainsTimeMillis > 0) {
            return ret + " Expires: " + (remainsTimeMillis / 1000) + "sec";
        }
        return ret + " Expired.";
    }

    public String toString() {
        return this.toUri.toString() + "\t" + this.contactUri.toString() + "\t" + this.expiresMillis;
    }

    public static JsSimpleRegisterInfo parseLine(String line) {
        Log.e("ppt", line);
        String[] lines = line.split("\t");
        if (lines.length == 3) {
            try {
                JsAddress tmpToUri = new JsAddress();
                tmpToUri.parse(lines[0]);
                JsAddress tmpContactUri = new JsAddress();
                tmpContactUri.parse(lines[1]);
                long tmpExpires = Long.parseLong(lines[2]);
                JsSimpleRegisterInfo newInfo = new JsSimpleRegisterInfo(tmpToUri, tmpContactUri);
                newInfo.expiresMillis = tmpExpires;
                return newInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
