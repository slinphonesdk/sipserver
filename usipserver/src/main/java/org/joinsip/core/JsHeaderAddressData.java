package org.joinsip.core;

public class JsHeaderAddressData extends JsHeaderData {
    private JsAddress address;

    public JsHeaderAddressData(JsHeader headerType) {
        super(headerType);
        this.address = null;
        this.address = new JsAddress();
    }

    public void copy(JsHeaderData src, boolean headerTypeFlag) {
        super.copy(src, headerTypeFlag);
        if (src instanceof JsHeaderAddressData) {
            this.address = ((JsHeaderAddressData) src).getAddress();
        }
    }

    public void copy(JsHeaderData src) {
        copy(src, true);
    }

    public Object clone() {
        JsHeaderAddressData newObj = new JsHeaderAddressData(this.headerType);
        newObj.copy(this);
        return newObj;
    }

    public JsAddress getAddress() {
        return (JsAddress) this.address.clone();
    }

    public void setAddress(JsAddress address) {
        this.address.copy(address);
    }

    public void setValue(String value) throws JsCoreException {
        super.setValue(value);
        this.address.parse(this.value.toString());
        this.value.setLength(0);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(this.headerType.toString());
        buf.append(": ");
        buf.append(this.address.toString());
        appendParameters(buf);
        return buf.toString();
    }
}
