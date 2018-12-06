package org.joinsip.core;

public class JsHeaderIntData extends JsHeaderData {
    private int intValue = 0;

    public JsHeaderIntData(JsHeader headerType) {
        super(headerType);
    }

    public void copy(JsHeaderData src) {
        super.copy(src);
        if (src instanceof JsHeaderIntData) {
            this.intValue = ((JsHeaderIntData) src).getIntValue();
        }
    }

    public Object clone() {
        JsHeaderIntData newObj = new JsHeaderIntData(this.headerType);
        newObj.copy(this);
        return newObj;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public void setIntValue(int value) {
        this.intValue = value;
    }

    public void setValue(String value) throws JsCoreException {
        super.setValue(value);
        int spltIndex = this.value.indexOf(" ");
        if (spltIndex < 0) {
            this.intValue = Integer.parseInt(this.value.toString().trim());
            this.value.setLength(0);
            return;
        }
        this.intValue = Integer.parseInt(this.value.substring(0, spltIndex).trim());
        String newValue = this.value.substring(spltIndex + 1).trim();
        this.value.setLength(0);
        this.value.append(newValue);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(this.headerType.toString());
        buf.append(": ");
        buf.append(getIntValue());
        if (getValue().length() > 0) {
            buf.append(' ');
            buf.append(getValue());
        }
        appendParameters(buf);
        return buf.toString();
    }
}
