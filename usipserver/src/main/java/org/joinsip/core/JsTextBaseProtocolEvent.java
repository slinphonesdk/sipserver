package org.joinsip.core;

import java.util.StringTokenizer;
import java.util.Vector;

public abstract class JsTextBaseProtocolEvent extends JsNetEvent {
    public static final byte[] RETCODE = new byte[]{(byte) 13, (byte) 10};
    public static final int STARTLINE_INVALID = 0;
    public static final int STARTLINE_UNKNOWN = -1;
    private StringBuffer body;
    private Vector<JsHeaderData> headers;
    private StringBuffer startLine;
    protected int startLineIndex;

    protected abstract int getMethodIndex(String str);

    protected abstract String getProtocolVer();

    public abstract String getResponseMessage(int i);

    protected abstract int getStartLineIndex(String str);

    protected abstract void parse(String str) throws JsCoreException;

    public JsTextBaseProtocolEvent(JsNetEvent netEvent) {
        this.startLineIndex = 0;
        this.startLine = null;
        this.headers = null;
        this.body = null;
        this.startLine = new StringBuffer(64);
        this.headers = new Vector(16);
        this.body = new StringBuffer(256);
        copy(netEvent);
    }

    public int getStatusCode() {
        if (isResponse()) {
            return this.startLineIndex;
        }
        return -1;
    }

    public void setStatusCode(int statusCode) {
        setStartLine(getProtocolVer() + " " + statusCode + " " + getResponseMessage(statusCode));
    }

    public void setStartLine(String startLine) {
        this.startLine.setLength(0);
        this.startLine.append(startLine);
        this.startLineIndex = getStartLineIndex(startLine);
    }

    public String getStartLine() {
        return this.startLine.toString();
    }

    public JsAddress getRequestUri() {
        if (isRequest()) {
            String[] col = split(getStartLine(), " ");
            JsAddress uri = new JsAddress();
            try {
                uri.parse(col[1]);
                return uri;
            } catch (Exception e) {
                System.err.println("StartLine: " + getStartLine());
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setRequestUri(JsAddress uri) {
        if (isRequest()) {
            String[] col = split(getStartLine(), " ");
            setStartLine(col[0] + " " + uri.toString(false) + " " + col[2]);
        }
    }

    public void initParam() {
        this.startLineIndex = 0;
        this.startLine = new StringBuffer(64);
        this.headers.clear();
        this.body.setLength(0);
    }

    public int getMethod() {
        return getMethodIndex(getMethodName());
    }

    public String getBody() {
        return this.body == null ? null : this.body.toString();
    }

    public void setBody(String body, String contentType) {
        try {
            JsHeaderData newContentType = new JsHeaderData(JsHeader.CONTENT_TYPE);
            newContentType.setValue(contentType);
            JsHeaderIntData newContentLength = new JsHeaderIntData(JsHeader.CONTENT_LENGTH);
            newContentLength.setIntValue(contentType.length());
            setHeader(newContentType);
            setHeader(newContentLength);
            this.body.setLength(0);
            this.body.append(body);
        } catch (JsCoreException e) {
        }
    }

    public String getMethodName() {
        if (!isRequest()) {
            return null;
        }
        String[] col = split(getStartLine(), " ");
        if (col.length > 0) {
            return col[0];
        }
        return null;
    }

    public boolean isRequest() {
        return this.startLineIndex < 0;
    }

    public boolean isResponse() {
        return this.startLineIndex > 0;
    }

    public boolean isSuccessResponse() {
        return this.startLineIndex >= JsSipEvent.RES_200 && this.startLineIndex < 300;
    }

    public boolean isErrorResponse() {
        return this.startLineIndex >= JsSipEvent.RES_400 && this.startLineIndex < 700;
    }

    public boolean isResponse(int statusCode) {
        return this.startLineIndex == statusCode;
    }

    public boolean isValid() {
        return this.startLineIndex != 0;
    }

    public boolean isInvalid() {
        return this.startLineIndex == 0;
    }

    public void copy(JsNetEvent src) {
        try {
            parse(src.toString());
            super.copy(src);
        } catch (Exception e) {
            this.startLineIndex = 0;
        }
    }

    private JsHeaderData getRawHeader(JsHeader key, int index) {
        int hndex = getHeaderIndex(key, index);
        if (hndex >= 0) {
            return (JsHeaderData) this.headers.get(hndex);
        }
        return null;
    }

    public JsHeaderData getHeader(int index) {
        return (JsHeaderData) ((JsHeaderData) this.headers.get(index)).clone();
    }

    public JsHeaderData[] getHeaderSet(JsHeader key) {
        JsHeaderData[] ret = new JsHeaderData[countHeader(key)];
        int headerCount = 0;
        for (int jndex = 0; jndex < this.headers.size(); jndex++) {
            JsHeaderData nowHeader = (JsHeaderData) this.headers.get(jndex);
            if (nowHeader.getHeaderName() == key) {
                int headerCount2 = headerCount + 1;
                ret[headerCount] = nowHeader;
                headerCount = headerCount2;
            }
        }
        return ret;
    }

    public int getHeaderIndex(JsHeader key, int index) {
        for (int jndex = 0; jndex < this.headers.size(); jndex++) {
            if (((JsHeaderData) this.headers.get(jndex)).getHeaderName() == key) {
                if (index <= 0) {
                    return jndex;
                }
                index--;
            }
        }
        return -1;
    }

    public int getHeaderLastIndex(JsHeader key) {
        int lastIndex = -1;
        for (int jndex = 0; jndex < this.headers.size(); jndex++) {
            if (((JsHeaderData) this.headers.get(jndex)).getHeaderName() == key) {
                lastIndex = jndex;
            }
        }
        return lastIndex;
    }

    public JsHeaderData getLastHeader(JsHeader key) {
        int hIndex = getHeaderLastIndex(key);
        return hIndex < 0 ? null : getHeader(hIndex);
    }

    public void setHeader(JsHeaderData headerData) throws JsCoreException {
        int index = getHeaderIndex(headerData.getHeaderName(), 0);
        if (index < 0) {
            appendHeader(headerData);
        } else {
            updateHeader(headerData, index);
        }
    }

    public void updateHeader(JsHeaderData headerValue, JsHeader targetHeader, int targetHeaderIndex) throws JsCoreException {
        updateHeader(headerValue, getHeaderIndex(targetHeader, targetHeaderIndex));
    }

    public void updateHeader(JsHeaderData headerValue, int index) throws JsCoreException {
        try {
            JsHeaderData targetHeader = (JsHeaderData) this.headers.get(index);
            if (targetHeader.getClass() == headerValue.getClass()) {
                targetHeader.copy(headerValue);
                return;
            }
            this.headers.remove(index);
            this.headers.insertElementAt((JsHeaderData) headerValue.clone(), index);
        } catch (Exception e) {
            JsCoreException ex = new JsCoreException();
            ex.addTraceMessage(0, "invalid header index.");
            throw ex;
        }
    }

    public boolean hasHeader(JsHeader key) {
        return getRawHeader(key, 0) != null;
    }

    public JsHeaderData getHeader(JsHeader key, int index) {
        JsHeaderData rawData = getRawHeader(key, index);
        return rawData == null ? null : (JsHeaderData) rawData.clone();
    }

    public int countHeader(JsHeader key) {
        int count = 0;
        for (int jndex = 0; jndex < this.headers.size(); jndex++) {
            if (((JsHeaderData) this.headers.get(jndex)).getHeaderName() == key) {
                count++;
            }
        }
        return count;
    }

    public int getHeaderCount() {
        return this.headers.size();
    }

    public void insertHeader(JsHeaderData header) {
        int index = getHeaderIndex(header.getHeaderName(), 0);
        if (index < 0) {
            appendHeader(header);
        } else {
            this.headers.insertElementAt((JsHeaderData) header.clone(), index);
        }
    }

    public void insertHeaderBefore(JsHeaderData header, JsHeader key) {
        int index = getHeaderIndex(key, 0);
        if (index >= 0) {
            insertHeaderLine(header, index);
        } else {
            appendHeader(header);
        }
    }

    public void insertHeaderAfter(JsHeaderData header, JsHeader key) {
        int index = getHeaderLastIndex(key);
        if (index >= 0) {
            insertHeaderLine(header, index + 1);
        } else {
            appendHeader(header);
        }
    }

    public void insertHeaderLine(JsHeaderData header, int index) {
        this.headers.insertElementAt((JsHeaderData) header.clone(), index);
    }

    public void appendHeader(JsHeaderData header) {
        this.headers.add((JsHeaderData) header.clone());
    }

    public void removeHeader(int index) {
        this.headers.remove(index);
    }

    public JsHeaderData removeHeader(JsHeader key, int index) {
        for (int jndex = 0; jndex < this.headers.size(); jndex++) {
            if (((JsHeaderData) this.headers.get(jndex)).getHeaderName() == key) {
                if (index <= 0) {
                    return (JsHeaderData) this.headers.remove(jndex);
                }
                index--;
            }
        }
        return null;
    }

    public void removeAllHeader(JsHeader key) {
        for (int jndex = this.headers.size() - 1; jndex >= 0; jndex--) {
            if (((JsHeaderData) this.headers.get(jndex)).getHeaderName() == key) {
                this.headers.remove(jndex);
            }
        }
    }

    public void appendBody(String body) {
        this.body.append(body);
    }

    public void removeBody() {
        JsHeaderIntData contentLength = (JsHeaderIntData) getRawHeader(JsHeader.CONTENT_LENGTH, 0);
        if (contentLength != null) {
            contentLength.setIntValue(0);
        }
        removeHeader(JsHeader.CONTENT_TYPE, 0);
        this.body.setLength(0);
    }

    protected int getBodySize() {
        return this.body.length();
    }

    public void update() {
        synchronized (this.mutex) {
            initData();
            if (hasHeader(JsHeader.CONTENT_LENGTH)) {
                ((JsHeaderIntData) getRawHeader(JsHeader.CONTENT_LENGTH, 0)).setIntValue(getBodySize());
            }
            byte[] line = this.startLine.toString().getBytes();
            appendData(line, 0, line.length);
            appendData(RETCODE, 0, RETCODE.length);
            for (int index = 0; index < this.headers.size(); index++) {
                try {
                    line = ((JsHeaderData) this.headers.get(index)).toString().getBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                appendData(line, 0, line.length);
                appendData(RETCODE, 0, RETCODE.length);
            }
            appendData(RETCODE, 0, RETCODE.length);
            String bodyStr = getBody();
            appendData(bodyStr.getBytes(), 0, bodyStr.length());
        }
    }

    public static final String[] split(String str, String splt) {
        StringTokenizer st = new StringTokenizer(str, splt);
        String[] ret = new String[st.countTokens()];
        for (int index = 0; index < ret.length; index++) {
            ret[index] = st.nextToken();
        }
        return ret;
    }

    public static void replaceWord(StringBuffer target, String newWord, int index) {
        synchronized (target) {
            String[] words = split(target.toString(), " ");
            target.setLength(0);
            for (int jndex = 0; jndex < words.length; jndex++) {
                if (jndex != 0) {
                    target.append(' ');
                }
                if (jndex == index) {
                    target.append(newWord);
                } else {
                    target.append(words[jndex]);
                }
            }
        }
    }

    public String toString() {
        update();
        return super.toString();
    }
}
