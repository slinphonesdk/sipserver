package org.joinsip.impl.proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.joinsip.common.JsSystem;
import org.joinsip.core.JsAddress;
import org.joinsip.core.JsConsole;

public class JsSimpleRegistrar {
    public static final String REGEX_SPECIALCHARS = ".^$[]*+?|()";
    private JsConsole console;
    private boolean logFlag;
    private JsSimpleRegisterPersistenceLogic logic;
    private Hashtable<String, JsSimpleRegisterInfo> regTable;
    private Hashtable<Pattern, JsAddress> routeTable;
    private Exception e2;

    public JsSimpleRegistrar() {
        this.regTable = null;
        this.routeTable = null;
        this.logic = null;
        this.logFlag = false;
        this.regTable = new Hashtable();
        this.routeTable = new Hashtable();
    }

    public void setConsoleOut(JsConsole console) {
        this.console = console;
    }

    public void setLogEnable(boolean flag) {
        this.logFlag = flag;
    }

    public void setPersistenceLogic(JsSimpleRegisterPersistenceLogic logic) {
        this.logic = logic;
        if (logic != null) {
            logic.readRegisterTable(this.regTable);
        }
    }

    // 注册 or 取消注册
    public void register(JsAddress toUri, JsAddress contactUri, int expires) {
        JsSimpleRegisterInfo newInfo = new JsSimpleRegisterInfo(toUri, contactUri);
        newInfo.setExpires(expires);
        if (expires > 0) {
            if (this.logFlag && this.console != null) {
                this.console.out("$ REGISTER " + newInfo.toDispString());
            }
        } else if (this.logFlag && this.console != null) {
            this.console.out("$ UNREGISTER [" + toUri.getUser() + "]");
        }
        synchronized (this.regTable) {
            this.regTable.put(toUri.getUser(), newInfo);
        }
        checkExpired();
        synchronized (this.regTable) {
            JsSimpleRegisterPersistenceLogic tmpLogic = this.logic;
            if (tmpLogic != null) {
                tmpLogic.writeRegisterTable(this.regTable);
            }
        }
    }

    public void setRoute(JsAddress uri) {
        synchronized (this.routeTable) {
            Pattern pattern = Pattern.compile(uri.getUser());
            JsAddress newUri = new JsAddress();
            newUri.copy(uri);
            this.routeTable.put(pattern, newUri);
            JsSystem.err.println("$ NEWROUTE [" + uri.getUser() + "] -> " + uri.getHost() + ":" + uri.getPort());
        }
    }

    public JsAddress getInfo(String user) {
        JsSimpleRegisterInfo nowInfo = (JsSimpleRegisterInfo) this.regTable.get(user);
        if (nowInfo != null) {
            return nowInfo.contactUri;
        }
        Enumeration<Pattern> en = this.routeTable.keys();
        while (en.hasMoreElements()) {
            Pattern nowPtn = (Pattern) en.nextElement();
            if (nowPtn.matcher(user).matches()) {
                JsAddress newContact = new JsAddress();
                newContact.copy((JsAddress) this.routeTable.get(nowPtn));
                newContact.setUser(user);
                return newContact;
            }
        }
        return null;
    }

    public boolean readStaticRegisterInfoFile(String path) {
        BufferedReader bufferedReader;
        Exception e;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    JsSystem.err.println("read static register table file : " + path + " ... OK");
                    bufferedReader = reader;
                    return true;
                } else if (line.trim().length() != 0) {
                    JsAddress uri = new JsAddress();
                    try {
                        uri.parse(line);
                        boolean isRegex = false;
                        for (int index = 0; index < REGEX_SPECIALCHARS.length(); index++) {
                            if (uri.getUser().indexOf(REGEX_SPECIALCHARS.charAt(index)) >= 0) {
                                isRegex = true;
                                break;
                            }
                        }
                        if (isRegex) {
                            setRoute(uri);
                        } else {
                            register(uri, uri, 31536000);
                        }
                    } catch (Exception e2) {
                        try {
                            e2.printStackTrace();
                            JsSystem.err.println(e2.getMessage());
                        } catch (Exception e3) {
                            e2 = e3;
                            bufferedReader = reader;
                        }
                    }
                }
            }
        } catch (Exception e4) {
            e2 = e4;
            JsSystem.err.println("read static register table file : " + path + " ... " + e2.toString());
            return false;
        }
    }

    public void checkExpired() {
        synchronized (this.regTable) {
            Enumeration<JsSimpleRegisterInfo> renum = this.regTable.elements();
            while (renum.hasMoreElements()) {
                JsSimpleRegisterInfo regInfo = (JsSimpleRegisterInfo) renum.nextElement();
                if (regInfo.isExpired()) {
                    this.regTable.remove(regInfo.getToUri().getUser());
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        int count = 0;
        Enumeration<String> keys = this.regTable.keys();
        while (keys.hasMoreElements()) {
            buf.append("  " + ((JsSimpleRegisterInfo) this.regTable.get((String) keys.nextElement())).toDispString() + "\n");
            count++;
        }
        buf.append("  " + count + " Register Data.");
        Enumeration<Pattern> pkeys = this.routeTable.keys();
        if (pkeys.hasMoreElements()) {
            count = 0;
            buf.append('\n');
            while (keys.hasMoreElements()) {
                Pattern key = (Pattern) pkeys.nextElement();
                buf.append("  [" + key.pattern() + "] -> " + ((JsSimpleRegisterInfo) this.regTable.get(key)).toDispString() + "\n");
                count++;
            }
            buf.append("  " + count + " Route Data.");
        }
        return buf.toString();
    }
}
