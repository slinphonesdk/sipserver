package org.joinsip.core;

import java.lang.reflect.Array;

public final class JsAddress implements Cloneable {
    private String host = null;
    private String name = null;
    private int port = 5060;
    private String schema = "sip";
    private String[][] tag = ((String[][]) null);
    private String user = null;
    private boolean validFlag = false;

    public void initParam() {
        this.validFlag = false;
        this.name = null;
        this.schema = "sip";
        this.user = null;
        this.host = null;
        this.port = 5060;
        this.tag = null;
    }

    public void copy(JsAddress src) {
        this.validFlag = src.isValid();
        this.name = src.getName();
        this.schema = src.getSchema();
        this.user = src.getUser();
        this.host = src.getHost();
        this.port = src.getPort();
        this.tag = src.getTag();
    }

    public Object clone() {
        JsAddress newObj = new JsAddress();
        newObj.copy(this);
        return newObj;
    }

    private String[][] cloneTag(String[][] srcTag) {
        if (srcTag == null) {
            return null;
        }
        String[][] newTag = (String[][]) Array.newInstance(String.class, new int[]{srcTag.length, 2});
        for (int index = 0; index < newTag.length; index++) {
            if (srcTag[index].length != 2) {
                return null;
            }
            newTag[index][0] = srcTag[index][0];
            newTag[index][1] = srcTag[index][1];
        }
        return newTag;
    }

    public boolean isValid() {
        return this.validFlag;
    }

    private void setValidFlag() {
        if (this.host != null && this.port > 0) {
            this.validFlag = true;
        }
    }

    public boolean isHostUri() {
        return this.host != null && this.user == null;
    }

    public boolean isUserUri() {
        return (this.host == null || this.user == null) ? false : true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        if (host.indexOf(58) < 0 || host.startsWith("[")) {
            this.host = host;
        } else {
            this.host = "[" + host + "]";
        }
        setValidFlag();
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
        setValidFlag();
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void addTag(String key, String value) {
        this.tag = makeNewTag(this.tag, key, value);
    }

    public void setTagValue(String key, String value) {
        String[] targetTag = getTag(this.tag, key);
        if (targetTag != null) {
            targetTag[1] = value;
        } else {
            addTag(key, value);
        }
    }

    public boolean isAsterisk() {
        return this.host != null && this.host.length() == 1 && this.host.equals("*");
    }

    public void removeTag() {
        this.tag = null;
    }

    public String[][] getTag() {
        return cloneTag(this.tag);
    }

    public String getParameterValue(String key) {
        String[] param = getTag(this.tag, key);
        if (param == null || param.length != 2) {
            return null;
        }
        return param[1];
    }

    private static String[] getTag(String[][] tag, String key) {
        if (tag != null) {
            int index = 0;
            while (index < tag.length) {
                if (tag[index] != null && tag[index][0] != null && tag[index][0].equals(key)) {
                    return tag[index];
                }
                index++;
            }
        }
        return null;
    }

    private static String[][] makeNewTag(String[][] nowTag, String newKey, String newValue) {
        int newSize = nowTag == null ? 1 : nowTag.length + 1;
        String[][] newTag = (String[][]) Array.newInstance(String.class, new int[]{newSize, 2});
        if (nowTag != null) {
            System.arraycopy(nowTag, 0, newTag, 0, nowTag.length);
        }
        newTag[newSize - 1][0] = newKey;
        newTag[newSize - 1][1] = newValue;
        return newTag;
    }

    private String cutWord(String text, char c, int fromIndex, int toIndex) {
        int bgnIndex = text.indexOf(c, fromIndex);
        int endIndex = text.indexOf(c, bgnIndex + 1);
        if (bgnIndex > toIndex) {
            bgnIndex = -1;
        }
        if (endIndex > toIndex) {
            endIndex = -1;
        }
        if ((bgnIndex < 0 && endIndex >= 0) || (bgnIndex >= 0 && endIndex < 0)) {
            return null;
        }
        if (bgnIndex >= 0) {
            return text.substring(bgnIndex + 1, endIndex);
        }
        return text.substring(fromIndex, toIndex + 1);
    }

    private int countTagChar(String text, char c, int fromIndex, int toIndex) {
        int count = 0;
        for (int index = fromIndex; index <= toIndex; index++) {
            if (text.charAt(index) == c) {
                count++;
            }
        }
        return count;
    }

    private String[][] cutTag(String text, char c, int fromIndex, int toIndex) {
        String[][] ret = (String[][]) Array.newInstance(String.class, new int[]{countTagChar(text, ';', fromIndex, toIndex), 2});
        int tagIndex = 0;
        int tagBgnIndex = fromIndex;
        boolean eqFlag = false;
        int index = tagBgnIndex + 1;
        while (index <= toIndex) {
            char nowChar = text.charAt(index);
            if (nowChar == '=') {
                eqFlag = true;
                ret[tagIndex][0] = text.substring(tagBgnIndex + 1, index);
                if (ret[tagIndex][0].length() == 0) {
                    return (String[][]) null;
                }
                tagBgnIndex = index;
            }
            if (nowChar == c || index == toIndex) {
                if (index == toIndex) {
                    if (nowChar == c) {
                        return (String[][]) null;
                    }
                    index++;
                }
                if (eqFlag) {
                    ret[tagIndex][1] = text.substring(tagBgnIndex + 1, index);
                } else {
                    ret[tagIndex][0] = text.substring(tagBgnIndex + 1, index);
                    if (ret[tagIndex][0].length() == 0) {
                        return (String[][]) null;
                    }
                }
                tagBgnIndex = index;
                tagIndex++;
                eqFlag = false;
            }
            index++;
        }
        return ret;
    }

    private static String makeTagString(String[][] tags) {
        if (tags == null || tags.length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(64);
        for (int index = 0; index < tags.length; index++) {
            if (tags[index][0] != null) {
                buf.append(';').append(tags[index][0]);
                if (tags[index][1] != null) {
                    buf.append('=').append(tags[index][1]);
                }
            }
        }
        return buf.toString();
    }

    public void parse(String text) throws JsCoreException {
        initParam();
        if (text.length() > 0 && (text.startsWith(" ") || text.endsWith(" "))) {
            text = text.trim();
        }
        String tmpName = null;
        String[][] tmpExTags = null;
        int ltIndex = text.indexOf(60);
        int gtIndex = text.indexOf(62, ltIndex + 1);
        JsCoreException ex;
        if ((gtIndex >= 0 || ltIndex < 0) && (gtIndex < 0 || ltIndex >= 0)) {
            if (ltIndex < 0) {
                gtIndex = text.length();
            }
            if (ltIndex >= 0) {
                tmpName = cutWord(text, '\"', 0, ltIndex - 1);
                int countNameDq = countTagChar(text, '\"', 0, ltIndex - 1);
                if (countNameDq > 0 && (tmpName == null || text.charAt(tmpName.length() + 1) != '\"' || countNameDq != 2)) {
                    ex = new JsCoreException();
                    ex.addTraceMessage(1, "parse error - " + text);
                    throw ex;
                } else if (gtIndex < 0) {
                    ex = new JsCoreException();
                    ex.addTraceMessage(2, "parse error - " + text);
                    throw ex;
                } else {
                    int tagBgnIndex = gtIndex + 1;
                    if (text.length() > tagBgnIndex + 1 && cutTag(text, ';', tagBgnIndex, text.length() - 1) == null) {
                        ex = new JsCoreException();
                        ex.addTraceMessage(2, "parse error - " + text);
                        throw ex;
                    }
                }
            }
            String tmpSchema = null;
            int scIndex = text.indexOf(58, ltIndex);
            if (scIndex - ltIndex >= 4) {
                String sch = text.substring(ltIndex + 1, scIndex + 1);
                if (sch.startsWith("sip:") || sch.startsWith("tel:")) {
                    tmpSchema = sch.substring(0, 3);
                } else {
                    scIndex = ltIndex;
                }
            } else {
                scIndex = ltIndex;
            }
            String tmpUser = null;
            int atIndex = text.indexOf(64, scIndex);
            if (atIndex < 0) {
                atIndex = scIndex;
            } else if (atIndex <= scIndex || atIndex >= gtIndex) {
                ex = new JsCoreException();
                ex.addTraceMessage(2, "parse error - " + text);
                throw ex;
            } else {
                tmpUser = text.substring(scIndex + 1, atIndex);
            }
            String[][] tmpTags = null;
            int tagIndex = text.indexOf(59, ltIndex);
            if (tagIndex <= 0 || tagIndex >= gtIndex) {
                tagIndex = gtIndex;
            } else {
                tmpTags = cutTag(text, ';', tagIndex, gtIndex - 1);
                if (tmpTags == null) {
                    ex = new JsCoreException();
                    ex.addTraceMessage(2, "parse error - " + text);
                    throw ex;
                }
            }
            int tmpPort = 5060;
            int portIndex = text.lastIndexOf(58, tagIndex);
            int v6AddrBgnIndex = text.lastIndexOf(91, tagIndex);
            int v6AddrEndIndex = text.lastIndexOf(93, tagIndex);
            if (portIndex > v6AddrBgnIndex && portIndex < v6AddrEndIndex) {
                portIndex = -1;
            }
            if (portIndex < 0 || portIndex <= atIndex || portIndex >= tagIndex) {
                portIndex = tagIndex;
            } else {
                try {
                    tmpPort = Integer.valueOf(text.substring(portIndex + 1, tagIndex)).intValue();
                } catch (NumberFormatException e) {
                    ex = new JsCoreException();
                    ex.addTraceMessage(2, "parse error - " + text);
                    throw ex;
                }
            }
            String tmpHost = text.substring(atIndex + 1, portIndex);
            this.validFlag = true;
            if (tmpName != null) {
                this.name = tmpName.trim();
            } else {
                this.name = tmpName;
            }
            this.schema = tmpSchema;
            this.user = tmpUser;
            this.host = tmpHost;
            this.port = tmpPort;
            this.tag = tmpTags;
            return;
        }
        ex = new JsCoreException();
        ex.addTraceMessage(0, "parse error - " + text);
        throw ex;
    }

    public String getAddressInfoString() {
        StringBuilder buf = new StringBuilder(256);
        buf.append("name   : [" + this.name + "]\n");
        buf.append("schema : [" + this.schema + "]\n");
        buf.append("user   : [" + this.user + "]\n");
        buf.append("host   : [" + this.host + "]\n");
        buf.append("port   : [" + this.port + "]\n");
        buf.append("param  : [" + makeTagString(this.tag) + "]\n");
        return buf.toString();
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean style) {
        if (!isValid()) {
            return null;
        }
        StringBuilder buf = new StringBuilder(1500);
        if (style) {
            if (this.name != null && this.name.length() > 0) {
                buf.append('\"').append(this.name).append('\"');
            }
            buf.append('<');
        }
        if (this.schema != null && this.schema.length() > 0) {
            buf.append(this.schema).append(':');
        }
        if (this.user != null && this.user.length() > 0) {
            buf.append(this.user).append('@');
        }
        buf.append(this.host);
        if (this.port >= 0 && this.port != 5060) {
            buf.append(':').append(this.port);
        }
        if (this.tag != null) {
            buf.append(makeTagString(this.tag));
        }
        if (style) {
            buf.append('>');
        }
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JsAddress)) {
            return false;
        }
        return toString(false).equals(((JsAddress) obj).toString(false));
    }
}
