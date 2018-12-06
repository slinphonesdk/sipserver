package org.joinsip.core.body;

import java.util.StringTokenizer;
import java.util.Vector;
import org.joinsip.common.JsUtil;
import org.joinsip.core.JsCoreException;

public class JsSdpData {
  public static final int PORT_NONE = -1;
  private String host;
  private MLine[] mlines;
  private long sessionId;
  private String sessionInfo;
  private String sessionName;
  private long sessionVersion;
  private long timestamp;
  private String user;
  private int version;

  public JsSdpData() {
    this.version = 0;
    this.user = null;
    this.sessionId = 0;
    this.sessionVersion = 0;
    this.host = "0.0.0.0";
    this.sessionName = "-";
    this.sessionInfo = "";
    this.timestamp = -1;
    this.mlines = null;
    this.timestamp = System.currentTimeMillis();
    this.sessionId = this.timestamp;
    this.sessionVersion = this.timestamp;
    this.mlines = new MLine[3];
  }

  public Object clone() {
    JsSdpData obj = new JsSdpData();
    obj.copy(this);
    return obj;
  }

  public void copy(JsSdpData src) {
    this.version = src.getVersion();
    this.user = src.getUser();
    this.sessionId = src.getSessionId();
    this.sessionVersion = src.getSessionVersion();
    this.host = src.getHost();
    this.sessionName = src.getSessionName();
    this.sessionInfo = src.getSessionInfo();
    for (int index = 0; index < this.mlines.length; index++) {
      JsSdpMediaInfo mline = src.getMLine(index);
      if (mline == null) {
        this.mlines[index] = null;
      } else {
        this.mlines[index] = new MLine(index, mline.getPort(), 1, JsSdpMediaInfo.TRANSPORT_RTP_AVP);
        this.mlines[index].copy((MLine) mline);
      }
    }
  }

  public JsSdpMediaInfo getMLine(int media) {
    return this.mlines[media];
  }

  public JsRTPInfo[] getSupportMediaInfoList(int media) {
    MLine mline = this.mlines[media];
    if (mline == null) {
      return null;
    }
    return mline.getRTPMediaInfoList();
  }

  public JsSdpMediaInfo getMatchMediaInfo(int media, JsRTPInfo[] mediaList) {
    MLine mline = this.mlines[media];
    if (mline == null) {
      return null;
    }
    for (JsRTPInfo nowInfo : mediaList) {
      JsSdpMediaInfo retInfo = mline.findALine(nowInfo.getEncodingName(), nowInfo.getClockRate());
      if (retInfo != null) {
        return retInfo;
      }
    }
    return null;
  }

  public JsSdpMediaInfo selectMediaInfo(int media, int payloadType) {
    return this.mlines[media].selectALine(payloadType);
  }

  public void addSdpMediaInfo(JsRTPInfo newInfo) throws JsCoreException {
    addSdpMediaInfo(newInfo, null);
  }

  public void addSdpMediaInfo(JsRTPInfo newInfo, String formatParameter) throws JsCoreException {
    if (this.mlines[newInfo.getMedia()] == null) {
      JsCoreException ex = new JsCoreException();
      ex.addTraceMessage(0, "not initialized yet.(media=" + newInfo.getMedia() + ")");
      throw ex;
    }
    this.mlines[newInfo.getMedia()].addALine(newInfo, formatParameter);
  }

  public String toTestString() {
    StringBuffer buf = new StringBuffer();
    buf.append("v=0").append("\r\n");
    buf.append("o=test " + this.timestamp + " " + this.timestamp + " IN IP4 192.168.1.1").append("\r\n");
    buf.append("s=test name").append("\r\n");
    buf.append("c=IN IP4 192.168.1.1").append("\r\n");
    buf.append("t=0 0").append("\r\n");
    buf.append("m=audio 5678 RTP/AVP 0 100 102").append("\r\n");
    buf.append("a=rtpmap:100 test1/8000").append("\r\n");
    buf.append("a=rtpmap:102 test2/16600").append("\r\n");
    buf.append("a=fmtp:100 CIF=2;QCIF=1;D=1").append("\r\n");
    buf.append("a=sendrecv").append("\r\n");
    buf.append("m=video 5678 RTP/AVP 0 107").append("\r\n");
    buf.append("a=rtpmap:106 H263/90000").append("\r\n");
    buf.append("a=fmtp:106 QCIF=2").append("\r\n");
    buf.append("a=recvonly").append("\r\n");
    return buf.toString();
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("v=" + getVersion()).append("\r\n");
    buf.append("o=" + getUser() + " " + getSessionId() + " " + getSessionVersion() + " IN IP4 " + getHost()).append("\r\n");
    if (getSessionName() != null) {
      buf.append("s=" + getSessionName()).append("\r\n");
    }
    buf.append("c=IN IP4 " + getHost()).append("\r\n");
    buf.append("t=0 0").append("\r\n");
    if (this.mlines[0] != null && getAudioPort() >= 0) {
      buf.append(this.mlines[0].toString());
    }
    if (this.mlines[1] != null && getVideoPort() >= 0) {
      buf.append(this.mlines[1].toString());
    }
    if (this.mlines[2] != null && getApplicationPort() >= 0) {
      buf.append(this.mlines[2].toString());
    }
    return buf.toString();
  }

  public void parse(String body) {
    Vector<String> defaultALine = new Vector(16);
    MLine nowMLine = null;
    StringTokenizer st = new StringTokenizer(body, "\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      String lineValue = line.substring(2).trim();
      String[] cols = JsUtil.split(lineValue, " ");
      if (line.length() >= 2 && line.charAt(1) == '=') {
        switch (line.charAt(0)) {
          case 'a':
            if (nowMLine != null) {
              nowMLine.parseALine(line);
              break;
            } else {
              defaultALine.add(line);
              break;
            }
          case 'c':
            setHost(cols[2]);
            break;
          case 'm':
            nowMLine = MLine.parseMLine(line);
            this.mlines[nowMLine.getMedia()] = nowMLine;
            break;
          case 'o':
            setUser(cols[0]);
            setSessionId(Long.parseLong(cols[1]));
            setSessionVersion(Long.parseLong(cols[2]));
            setHost(cols[5]);
            break;
          case 's':
            setSessionName(lineValue);
            break;
          case 'v':
            setVersion(Integer.parseInt(lineValue));
            break;
          default:
            break;
        }
      }
    }
  }

  public void init(String host) {
    setHost(host);
  }

  private MLine newMedia(int media, int port) {
    return new MLine(media, port, 1, JsSdpMediaInfo.TRANSPORT_RTP_AVP);
  }

  public int getAudioPort() {
    return this.mlines[0].getPort();
  }

  public void setAudioPort(int port) {
    this.mlines[0].setPort(port);
  }

  public void initAudio(int audioPort) {
    this.mlines[0] = newMedia(0, audioPort);
  }

  public int getVideoPort() {
    return this.mlines[1].getPort();
  }

  public void initVideo(int videoPort) {
    this.mlines[1] = newMedia(1, videoPort);
  }

  public int getApplicationPort() {
    return this.mlines[2].getPort();
  }

  public void initApplicationPort(int applicationPort) {
    this.mlines[2] = newMedia(2, applicationPort);
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public long getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(long sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionInfo() {
    return this.sessionInfo;
  }

  public void setSessionInfo(String sessionInfo) {
    this.sessionInfo = sessionInfo;
  }

  public String getSessionName() {
    return this.sessionName;
  }

  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public long getSessionVersion() {
    return this.sessionVersion;
  }

  public void setSessionVersion(long sessionVersion) {
    this.sessionVersion = sessionVersion;
  }

  public String getUser() {
    if (this.user == null) {
      return "-";
    }
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void incrementSessionVersion() {
    this.sessionVersion++;
  }
}
