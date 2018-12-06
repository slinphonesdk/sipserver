package org.joinsip.impl.proxy;

import java.util.Hashtable;

public interface JsSimpleRegisterPersistenceLogic {
    boolean readRegisterTable(Hashtable<String, JsSimpleRegisterInfo> hashtable);

    boolean writeRegisterTable(Hashtable<String, JsSimpleRegisterInfo> hashtable);
}
