package org.joinsip.core.body;

public class JsRTPInfo {
    public static final int CLOCKRATE_DEFAULT = 8000;
    public static final JsRTPInfo IANA_00_PCMU_8000 = new JsRTPInfo(0, 0, "PCMU", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_03_GSM_8000 = new JsRTPInfo(0, 3, "GSM", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_04_G723_8000 = new JsRTPInfo(0, 4, "G723", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_05_DVI4_8000 = new JsRTPInfo(0, 5, "DVI4", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_06_DVI4_16000 = new JsRTPInfo(0, 6, "DVI4", null, 16000, 1);
    public static final JsRTPInfo IANA_07_LPC_8000 = new JsRTPInfo(0, 7, "LPC", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_08_PCMA_8000 = new JsRTPInfo(0, 8, "PCMA", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_09_G722_8000 = new JsRTPInfo(0, 9, "G722", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_10_L16_44100_2 = new JsRTPInfo(0, 10, "L16", null, 44100, 2);
    public static final JsRTPInfo IANA_11_L16_44100_1 = new JsRTPInfo(0, 11, "L16", null, 44100, 1);
    public static final JsRTPInfo IANA_12_QCELP_8000 = new JsRTPInfo(0, 12, "QCELP", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_13_CN_8000 = new JsRTPInfo(0, 13, "CN", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_14_MPA_90000 = new JsRTPInfo(0, 14, "MPA", null, 90000, 1);
    public static final JsRTPInfo IANA_15_G728_8000 = new JsRTPInfo(0, 15, "G728", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_16_DVI4_11025 = new JsRTPInfo(0, 16, "DVI4", null, 11025, 1);
    public static final JsRTPInfo IANA_17_DVI4_22050 = new JsRTPInfo(0, 17, "DVI4", null, 22050, 1);
    public static final JsRTPInfo IANA_18_G729_8000 = new JsRTPInfo(0, 18, "G729", null, CLOCKRATE_DEFAULT, 1);
    public static final JsRTPInfo IANA_25_CelB_90000 = new JsRTPInfo(1, 25, "CelB", null, 90000, 0);
    public static final JsRTPInfo IANA_26_JPEG_90000 = new JsRTPInfo(1, 26, "JPEG", null, 90000, 0);
    public static final JsRTPInfo IANA_28_nv_90000 = new JsRTPInfo(1, 28, "nv", null, 90000, 0);
    public static final JsRTPInfo IANA_31_H261_90000 = new JsRTPInfo(1, 31, "H261", null, 90000, 0);
    public static final JsRTPInfo IANA_32_MPV_90000 = new JsRTPInfo(1, 32, "MPV", null, 90000, 0);
    public static final JsRTPInfo IANA_33_MP2T_90000 = new JsRTPInfo(1, 33, "MP2T", null, 90000, 0);
    public static final JsRTPInfo IANA_34_H263_90000 = new JsRTPInfo(1, 34, "H263", null, 90000, 0);
    private static final JsRTPInfo[] IANA_RTP_PARAMETERS = new JsRTPInfo[]{IANA_00_PCMU_8000, null, null, IANA_03_GSM_8000, IANA_04_G723_8000, IANA_05_DVI4_8000, IANA_06_DVI4_16000, IANA_07_LPC_8000, IANA_08_PCMA_8000, IANA_09_G722_8000, IANA_10_L16_44100_2, IANA_11_L16_44100_1, IANA_12_QCELP_8000, IANA_13_CN_8000, IANA_14_MPA_90000, IANA_15_G728_8000, IANA_16_DVI4_11025, IANA_17_DVI4_22050, IANA_18_G729_8000, null, null, null, null, null, null, IANA_25_CelB_90000, IANA_26_JPEG_90000, null, IANA_28_nv_90000, null, null, IANA_31_H261_90000, IANA_32_MPV_90000, IANA_33_MP2T_90000, IANA_34_H263_90000};
    public static final int MEDIA_APPLICATION = 2;
    public static final String MEDIA_APPLICATION_STRING = "application";
    public static final int MEDIA_AUDIO = 0;
    public static final String MEDIA_AUDIO_STRING = "audio";
    public static final int MEDIA_VIDEO = 1;
    public static final String MEDIA_VIDEO_STRING = "video";
    public static final int PAYLOADTYPE_NONE = -1;
    private int channels;
    private int clockRate;
    private String encodingName;
    private String encodingParameter;
    private int media;
    private int payloadType;

    public JsRTPInfo(int media, int payloadType, String encodingName, String encodingParameter, int clockRate, int channels) {
        this.media = media;
        this.payloadType = payloadType;
        this.encodingName = encodingName;
        this.encodingParameter = encodingParameter;
        this.clockRate = clockRate;
        this.channels = channels;
    }

    public JsRTPInfo(int media, int payloadType) {
        this.media = media;
        this.payloadType = payloadType;
    }

    public int getMedia() {
        return this.media;
    }

    public boolean isAudio() {
        return this.media == 0;
    }

    public boolean isVideo() {
        return this.media == 1;
    }

    public boolean isApplication() {
        return this.media == 2;
    }

    public String getMediaString() {
        switch (getMedia()) {
            case 0:
                return MEDIA_AUDIO_STRING;
            case 1:
                return MEDIA_VIDEO_STRING;
            case 2:
                return MEDIA_APPLICATION_STRING;
            default:
                return null;
        }
    }

    public int getClockRate() {
        return this.clockRate;
    }

    public String getEncodingName() {
        return this.encodingName;
    }

    public final String getEncodingParameter() {
        if (this.encodingParameter == null && this.channels == 2) {
            return "2";
        }
        return this.encodingParameter;
    }

    public final int getPayloadType() {
        return this.payloadType;
    }

    public final int getChannels() {
        return this.channels;
    }

    public static JsRTPInfo getStaticData(int payloadType) {
        if (payloadType >= IANA_RTP_PARAMETERS.length) {
            return null;
        }
        return IANA_RTP_PARAMETERS[payloadType];
    }
}
