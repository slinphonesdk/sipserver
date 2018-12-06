package org.joinsip.core.body;

import java.util.Vector;
import org.joinsip.common.JsUtil;

/* compiled from: JsSdpData */
class MLine extends JsSdpMediaInfo {
    private Vector<Object> alines = new Vector();
    private String[] cols1;

    public Object clone() {
        MLine mline = new MLine(getMedia(), getPort(), getNumberOfPort(), getTransport());
        mline.copy(this);
        return mline;
    }

    public JsRTPInfo[] getRTPMediaInfoList() {
        JsRTPInfo[] ret = new JsRTPInfo[this.alines.size()];
        for (int index = 0; index < this.alines.size(); index++) {
            ret[index] = (JsRTPInfo) this.alines.get(index);
        }
        return ret;
    }

    public void copy(MLine src) {
        super.copy(src);
        this.alines.removeAllElements();
        src.getALine(this.alines);
    }

    public void getALine(Vector<Object> dst) {
        dst.addAll(this.alines);
    }

    public static MLine parseMLine(String mline) {
        try {
            if (mline.startsWith("m=")) {
                int media = -1;
                String[] cols = JsUtil.split(mline.substring(2).trim(), " ");
                if (cols[0].equals(JsRTPInfo.MEDIA_AUDIO_STRING)) {
                    media = 0;
                } else if (cols[0].equals(JsRTPInfo.MEDIA_VIDEO_STRING)) {
                    media = 1;
                } else if (cols[0].equals(JsRTPInfo.MEDIA_APPLICATION_STRING)) {
                    media = 2;
                }
                MLine mLine = new MLine(media, Integer.parseInt(cols[1]), 1, cols[2]);
                for (int index = 3; index < cols.length; index++) {
                    int payloadType = Integer.parseInt(cols[index]);
                    JsRTPInfo rtpParameter = JsRTPInfo.getStaticData(payloadType);
                    if (rtpParameter == null) {
                        rtpParameter = new JsRTPInfo(media, payloadType);
                    }
                    mLine.addALine(new JsSdpMediaInfo(rtpParameter));
                }
                return mLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseALine(String aline) {
        JsSdpMediaInfo nowInfo;
        boolean updateSendRecv = false;
        if (aline.startsWith("a=recvonly")) {
            setSendRecv(1);
            updateSendRecv = true;
        }
        if (aline.startsWith("a=sendrecv")) {
            setSendRecv(0);
            updateSendRecv = true;
        }
        if (aline.startsWith("a=sendonly")) {
            setSendRecv(2);
            updateSendRecv = true;
        }
        if (updateSendRecv) {
            for (int index = 0; index < this.alines.size(); index++) {
                ((JsSdpMediaInfo) this.alines.get(index)).setSendRecv(getSendRecv());
            }
        }
        if (aline.startsWith("a=fmtp")) {
            String[] cols1 = JsUtil.split(aline, " ");
            int cIndex = cols1[0].indexOf(58);
            nowInfo = getALine(Integer.parseInt(cols1[0].substring(cIndex + 1)));
            if (nowInfo != null) {
                nowInfo.setFormatParameter(aline.substring(aline.indexOf(32, cIndex) + 1));
            }
        }
        if (aline.startsWith("a=rtpmap")) {
            String encodingParameter = null;
            cols1 = JsUtil.split(aline.substring(aline.indexOf(58) + 1).trim(), " ");
            int payloadType = Integer.parseInt(cols1[0]);
            nowInfo = getALine(payloadType);
            if (nowInfo != null) {
                String[] cols2 = JsUtil.split(cols1[1], "/");
                String encodingName = cols2[0];
                int clockRate = Integer.parseInt(cols2[1]);
                if (cols2.length > 2) {
                    encodingParameter = cols2[2];
                }
                nowInfo.setRTPParameter(new JsRTPInfo(getMedia(), payloadType, encodingName, encodingParameter, clockRate, 1));
            }
        }
    }

    MLine(int media, int port, int numberOfPort, String transport) {
        super(port, numberOfPort, transport);
        setRTPParameter(new JsRTPInfo(media, -1));
    }

    public boolean hasALine() {
        return !this.alines.isEmpty();
    }

    public int getMaxPayloadType() {
        int maxPayloadValue = 0;
        for (int index = 0; index < this.alines.size(); index++) {
            JsSdpMediaInfo line = (JsSdpMediaInfo) this.alines.get(index);
            if (line.getPayloadType() >= maxPayloadValue) {
                maxPayloadValue = line.getPayloadType();
            }
        }
        return maxPayloadValue;
    }

    private int getALineIndex(int payloadType) {
        for (int index = 0; index < this.alines.size(); index++) {
            if (((JsSdpMediaInfo) this.alines.get(index)).getPayloadType() == payloadType) {
                return index;
            }
        }
        return -1;
    }

    public void addALine(JsSdpMediaInfo line) {
        int index = getALineIndex(line.getPayloadType());
        if (index >= 0) {
            this.alines.remove(index);
        }
        this.alines.add(line);
    }

    public void addALine(JsRTPInfo newLine) {
        addALine(newLine, null);
    }

    public void addALine(JsRTPInfo newLine, String formatParameter) {
        JsSdpMediaInfo newInfo = new JsSdpMediaInfo(newLine, getPort(), getNumberOfPort(), getTransport());
        newInfo.setFormatParameter(formatParameter);
        addALine(newInfo);
    }

    public JsSdpMediaInfo selectALine(int payloadType) {
        JsSdpMediaInfo aline = getALine(payloadType);
        if (aline == null) {
            return null;
        }
        this.alines.removeAllElements();
        this.alines.add(aline);
        return aline;
    }

    public JsSdpMediaInfo findALine(String encodeType, int clockRate) {
        for (int index = 0; index < this.alines.size(); index++) {
            JsSdpMediaInfo line = (JsSdpMediaInfo) this.alines.get(index);
            if (line.getEncodingName().equalsIgnoreCase(encodeType) && line.getClockRate() == clockRate) {
                return line;
            }
        }
        return null;
    }

    public JsSdpMediaInfo getALine(int payloadType) {
        try {
            int lineIndex = getALineIndex(payloadType);
            return lineIndex < 0 ? null : (JsSdpMediaInfo) this.alines.get(lineIndex);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        StringBuffer bufM = new StringBuffer();
        StringBuffer bufARtpmap = new StringBuffer();
        StringBuffer bufAFmtpmap = new StringBuffer();
        bufM.append("m=").append(getMediaString());
        bufM.append(' ').append(getPort());
        if (getNumberOfPort() > 1) {
            bufM.append('/').append(getNumberOfPort());
        }
        bufM.append(' ').append(getTransport());
        for (int index = 0; index < this.alines.size(); index++) {
            JsSdpMediaInfo aline = (JsSdpMediaInfo) this.alines.get(index);
            if (!(aline instanceof MLine)) {
                bufARtpmap.append(aline.toRTPMAPString()).append("\r\n");
                if (aline.getFormatParameter() != null) {
                    bufAFmtpmap.append(aline.toFMTPString().trim()).append("\r\n");
                }
            }
            if (index == 0 && aline.getPayloadType() != 0) {
                bufM.append(" 0");
            }
            bufM.append(' ').append(aline.getPayloadType());
        }
        bufM.append("\r\n").append(bufARtpmap.toString());
        if (bufAFmtpmap.length() > 0) {
            bufM.append(bufAFmtpmap.toString());
        }
        bufM.append("a=" + getSendRecvString()).append("\r\n");
        return bufM.toString();
    }
}
