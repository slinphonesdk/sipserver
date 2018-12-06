package org.joinsip.core;

import org.joinsip.common.JsUtil;

public abstract class JsSipEventServiceBase extends JsNetEventServiceBase {
    protected boolean consoleFlag = false;

    protected abstract void receiveSipEvent(JsSipEvent jsSipEvent) throws JsCoreException;

    public void setLogEnable(boolean flag) {
        this.consoleFlag = flag;
    }

    // 发送状态
    protected void sendSipEvent(JsSipEvent event) {
        event.update();
        if (this.consoleFlag) {
            String destAddr = event.getDestAddr();
            out("# send > " + destAddr + JsUtil.makeCharLine(' ', 21 - destAddr.length()) + "\n  " + event.getSummary());
            out("event ---" + event.getSummary() + event.getBody());
        }
        sendEvent(event);
    }

    // 接收状态
    public void receiveEvent(JsNetEvent event) {

        JsSipEvent sipEvent = null;
        try {
            sipEvent = new JsSipEvent(event);
        } catch (Exception e) {
            System.err.println("----------");
            System.err.println(event.toString() + " (" + event.toString().length());
            System.err.println("----------");
            err(e.toString());
        }
        if (sipEvent != null && !sipEvent.isInvalid()) {
            try {
                if (this.consoleFlag) {
                    String srcAddr = event.getSrcAddr();
                    String log = "# recv < " + srcAddr + JsUtil.makeCharLine(' ', 21 - srcAddr.length());
                    if (sipEvent.isValid()) {
                        log = log + "\n  " + sipEvent.getSummary();
                    }
                    out(log);
                }
                receiveSipEvent(sipEvent);
            } catch (Exception e2) {
                err("# recv < " + event.getSrcAddr() + " exception! [" + e2.getClass().getSimpleName() + "]");
                err("<<< BGN(" + event.getLength() + " bytes) >>>");
                err(event.toString());
                err("<<< END >>>");
                try {
                    if (sipEvent.isRequest()) {
                        sendSipEvent(sipEvent.createResponse(JsSipEvent.RES_500));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (this.consoleFlag) {
            out("# recv < " + event.getSrcAddr() + " (invalid msg, " + event.getLength() + " bytes)");
            err("<<< BGN(" + event.getLength() + " bytes) >>>");
            err(event.toString());
            err("<<< END >>>");
        }
    }
}
