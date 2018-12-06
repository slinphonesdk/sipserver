package org.joinsip.core;

public class JsCoreException extends Exception {
    private static final long serialVersionUID = 1;
    private StringBuffer message = null;
    private Object reasonObj = null;
    private String trace = null;

    public JsCoreException() {
        StackTraceElement elem = getStackTrace()[0];
        this.message = new StringBuffer();
        String className = elem.getClassName();
        this.trace = className.substring(className.lastIndexOf(46) + 1) + "." + elem.getMethodName();
    }

    public void setReasonObject(Object reasonObj) {
        this.reasonObj = reasonObj;
    }

    public Object getReasonObject() {
        return this.reasonObj;
    }

    public void addTraceMessage(int index, String message) {
        addMessage("Ex at " + this.trace + "(#" + index + ") : " + message);
    }

    public void addMessage(String message) {
        this.message.append(message).append("\n");
    }

    public String getMessage() {
        return this.message.toString();
    }
}
