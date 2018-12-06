package org.joinsip.core;

import java.util.Vector;
import org.joinsip.common.JsUtil;

public class JsHeaderData {
    protected JsHeader headerType = null;
    protected Vector<String[]> parameters = null;
    protected StringBuffer value = null;

    public JsHeaderData(JsHeader headerType) {
        this.headerType = headerType;
        this.value = new StringBuffer(128);
        this.parameters = null;
    }

    public void copy(JsHeaderData src) {
        copy(src, true);
    }

    public void copy(JsHeaderData src, boolean headerTypeFlag) {
        if (headerTypeFlag) {
            this.headerType = src.getHeaderName();
        }
        this.value.setLength(0);
        this.value.append(src.getValue());
        this.parameters = new Vector(5);
        src.copyParameters(this.parameters);
    }

    public Object clone() {
        JsHeaderData newObj = new JsHeaderData(this.headerType);
        newObj.copy(this);
        return newObj;
    }

    public JsHeader getHeaderName() {
        return this.headerType;
    }

    public void copyParameters(Vector<String[]> parameters) {
        if (this.parameters != null) {
            for (int index = 0; index < this.parameters.size(); index++) {
                String[] param = (String[]) this.parameters.get(index);
                parameters.add(new String[]{param[0], param[1]});
            }
        }
    }

    public void setValue(String value) throws JsCoreException {
        this.parameters = null;
        int gtIndex = value.lastIndexOf(62);
        if (gtIndex < 0) {
            gtIndex = 0;
        }
        int bgnIndex = value.indexOf(59, gtIndex);
        if (bgnIndex >= 0) {
            String[] cols = JsUtil.split(value.substring(bgnIndex + 1), ";");
            for (String nowParam : cols) {
                if (nowParam.trim().length() > 0) {
                    String key;
                    String paramValue = "";
                    String[] cols2 = JsUtil.split(nowParam, "=");
                    if (cols2.length == 2) {
                        key = cols2[0];
                        paramValue = cols2[1];
                    } else {
                        key = cols2[0];
                    }
                    setParameter(key, paramValue);
                }
            }
            value = value.substring(0, bgnIndex);
        }
        this.value.setLength(0);
        this.value.append(value);
    }

    public void setParameter(String key, String value) {
        if (this.parameters == null) {
            this.parameters = new Vector(5);
        } else {
            for (int index = 0; index < this.parameters.size(); index++) {
                String[] param = (String[]) this.parameters.get(index);
                if (param[0].equalsIgnoreCase(key)) {
                    param[1] = value;
                    return;
                }
            }
        }
        this.parameters.add(new String[]{key, value});
    }

    public String getParameter(String key) {
        if (this.parameters == null) {
            return null;
        }
        for (int index = 0; index < this.parameters.size(); index++) {
            String[] param = (String[]) this.parameters.get(index);
            if (param[0].equals(key)) {
                return param[1];
            }
        }
        return null;
    }

    public void removeAllParameter() {
        if (this.parameters != null) {
            this.parameters.clear();
        }
    }

    public boolean hasParameter(String key) {
        if (this.parameters == null) {
            return false;
        }
        for (int index = 0; index < this.parameters.size(); index++) {
            if (((String[]) this.parameters.get(index))[0].equals(key)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value.toString();
    }

    protected final void appendParameters(StringBuilder buf) {
        if (this.parameters != null) {
            for (int index = 0; index < this.parameters.size(); index++) {
                String[] param = (String[]) this.parameters.get(index);
                buf.append(';');
                buf.append(param[0]);
                if (param[1].length() > 0) {
                    buf.append('=');
                    buf.append(param[1]);
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(this.headerType.toString());
        buf.append(": ");
        buf.append(getValue());
        appendParameters(buf);
        return buf.toString();
    }
}
