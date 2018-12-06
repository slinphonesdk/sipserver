package org.joinsip.core;

public final class JsSipRequest {
    public static final JsSipRequest ACK = new JsSipRequest("ACK");
    public static final JsSipRequest BYE = new JsSipRequest("BYE");
    public static final JsSipRequest CANCEL = new JsSipRequest("CANCEL");
    public static final JsSipRequest INFO = new JsSipRequest("INFO");
    public static final JsSipRequest INVITE = new JsSipRequest("INVITE");
    public static final JsSipRequest MESSAGE = new JsSipRequest("MESSAGE");
    public static final JsSipRequest NOTIFY = new JsSipRequest("NOTIFY");
    public static final JsSipRequest OPTIONS = new JsSipRequest("OPTIONS");
    public static final JsSipRequest PRACK = new JsSipRequest("PRACK");
    public static final JsSipRequest PUBLISH = new JsSipRequest("PUBLISH");
    public static final JsSipRequest REFFER = new JsSipRequest("REFFER");
    public static final JsSipRequest REGISTER = new JsSipRequest("REGISTER");
    public static final JsSipRequest SUBSCRIBE = new JsSipRequest("SUBSCRIBE");
    public static final JsSipRequest UPDATE = new JsSipRequest("UPDATE");
    private final String name;

    private JsSipRequest(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
