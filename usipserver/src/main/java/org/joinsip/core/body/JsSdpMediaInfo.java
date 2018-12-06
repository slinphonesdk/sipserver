package org.joinsip.core.body;

public class JsSdpMediaInfo
{
  public static final int PORT_NONE = -1;
  public static final int SENDRECV_RECVONLY = 1;
  public static final String SENDRECV_RECVONLY_STRING = "recvonly";
  public static final int SENDRECV_SENDONLY = 2;
  public static final String SENDRECV_SENDONLY_STRING = "sendonly";
  public static final int SENDRECV_SENDRECV = 0;
  public static final String SENDRECV_SENDRECV_STRING = "sendrecv";
  public static final String TRANSPORT_RTP_AVP = "RTP/AVP";
  public static final String TRANSPORT_UDP = "udp";
  private String formatParameter = null;
  private int numberOfPort;
  private int port;
  private JsRTPInfo rtpParameter = null;
  private int sendRecv = 0;
  private String transport;

  public JsSdpMediaInfo(int paramInt1, int paramInt2, int paramInt3, String paramString1, int paramInt4, String paramString2, int paramInt5, String paramString3, int paramInt6)
  {
    this.rtpParameter = new JsRTPInfo(paramInt1, paramInt4, paramString2, paramString3, paramInt5, paramInt6);
    this.port = paramInt2;
    this.numberOfPort = paramInt3;
    this.transport = paramString1;
    this.formatParameter = null;
  }

  public JsSdpMediaInfo(int paramInt1, int paramInt2, String paramString)
  {
    this.port = paramInt1;
    this.numberOfPort = paramInt2;
    this.transport = paramString;
  }

  public JsSdpMediaInfo(JsRTPInfo paramJsRTPInfo)
  {
    this.rtpParameter = paramJsRTPInfo;
  }

  public JsSdpMediaInfo(JsRTPInfo paramJsRTPInfo, int paramInt1, int paramInt2, String paramString)
  {
    this.rtpParameter = paramJsRTPInfo;
    this.port = paramInt1;
    this.numberOfPort = paramInt2;
    this.transport = paramString;
  }

  public static JsSdpMediaInfo parse(String paramString)
  {
    return null;
  }

  public void copy(JsSdpMediaInfo paramJsSdpMediaInfo)
  {
    this.rtpParameter = paramJsSdpMediaInfo.getRTPParameter();
    this.port = paramJsSdpMediaInfo.getPort();
    this.numberOfPort = paramJsSdpMediaInfo.getNumberOfPort();
    this.transport = paramJsSdpMediaInfo.getTransport();
    this.sendRecv = paramJsSdpMediaInfo.getSendRecv();
    this.formatParameter = paramJsSdpMediaInfo.getFormatParameter();
  }

  public int getClockRate()
  {
    return this.rtpParameter.getClockRate();
  }

  public String getEncodingName()
  {
    return this.rtpParameter.getEncodingName();
  }

  public String getFormatParameter()
  {
    return this.formatParameter;
  }

  public int getMedia()
  {
    return this.rtpParameter.getMedia();
  }

  public String getMediaString()
  {
    return this.rtpParameter.getMediaString();
  }

  public int getNumberOfPort()
  {
    return this.numberOfPort;
  }

  public int getPayloadType()
  {
    return this.rtpParameter.getPayloadType();
  }

  public int getPort()
  {
    return this.port;
  }

  public JsRTPInfo getRTPParameter()
  {
    return this.rtpParameter;
  }

  public int getSendRecv()
  {
    return this.sendRecv;
  }

  public String getSendRecvString()
  {
    switch (getSendRecv())
    {
    default:
      return null;
    case 0:
      return "sendrecv";
    case 1:
      return "recvonly";
    case 2:
    }
    return "sendonly";
  }

  public String getTransport()
  {
    return this.transport;
  }

  public void setFormatParameter(String paramString)
  {
    this.formatParameter = paramString;
  }

  public void setNumberOfPort(int paramInt)
  {
    this.numberOfPort = paramInt;
  }

  public void setPort(int paramInt)
  {
    this.port = paramInt;
  }

  public void setRTPParameter(JsRTPInfo paramJsRTPInfo)
  {
    this.rtpParameter = paramJsRTPInfo;
  }

  public void setSendRecv(int paramInt)
  {
    this.sendRecv = paramInt;
  }

  public void setTransport(String paramString)
  {
    this.transport = paramString;
  }

  public String toFMTPString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("a=fmtp:").append(this.rtpParameter.getPayloadType()).append(' ').append(this.formatParameter);
    return localStringBuffer.toString();
  }

  public String toRTPMAPString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("a=rtpmap:").append(this.rtpParameter.getPayloadType()).append(' ');
    localStringBuffer.append(this.rtpParameter.getEncodingName()).append('/').append(this.rtpParameter.getClockRate());
    if (this.rtpParameter.getEncodingParameter() != null)
      localStringBuffer.append('/').append(this.rtpParameter.getEncodingParameter());
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Administrator\Documents\Tencent Files\1946940079\FileRecv\classes-dex2jar\
 * Qualified Name:     org.joinsip.core.body.JsSdpMediaInfo
 * JD-Core Version:    0.6.0
 */