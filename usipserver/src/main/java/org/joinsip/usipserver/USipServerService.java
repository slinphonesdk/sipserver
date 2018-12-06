package org.joinsip.usipserver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Hashtable;
import org.joinsip.core.JsConsole;
import org.joinsip.core.transport.JsUdpEventSource;
import org.joinsip.impl.proxy.JsSimpleRegisterInfo;
import org.joinsip.impl.proxy.JsSimpleRegisterPersistenceLogic;
import org.joinsip.impl.proxy.JsSipProxyService;

public class USipServerService extends Service implements JsConsole, JsSimpleRegisterPersistenceLogic {
    public static final String INTENT_ACTION = "SSS_BROADCAST";
    public static final String INTENT_KEY_REGISTER = "SSS_REGISTER";
    public static final String INTENT_KEY_REGLOG = "SSS_REGLOG";
    public static final String INTENT_KEY_SIPLOG = "SSS_SIPLOG";
    public static final String INTENT_KEY_STATUS = "SSS_STATUS";
    static final String REG_DATA_FILENAME = "reg_data.txt";
    static final String TAG = "SSS";
    public final JsSipProxyService disp = new JsSipProxyService();
    private String domain = null;
    private String localIp = null;
    private int localPort = 0;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(USipServerService.INTENT_ACTION)) {
                boolean flag;
                if (intent.hasExtra(USipServerService.INTENT_KEY_SIPLOG)) {
                    flag = intent.getBooleanExtra(USipServerService.INTENT_KEY_SIPLOG, false);
                    if (flag) {
                        USipServerService.this.disp.out("[Set SipLog: On]");
                    } else {
                        USipServerService.this.disp.out("[Set SipLog: Off]");
                    }
                    USipServerService.this.disp.setLogEnable(flag);
                }
                if (intent.hasExtra(USipServerService.INTENT_KEY_REGLOG)) {
                    flag = intent.getBooleanExtra(USipServerService.INTENT_KEY_REGLOG, false);
                    if (flag) {
                        USipServerService.this.disp.out("[Set RegisterLog: On]");
                    } else {
                        USipServerService.this.disp.out("[Set RegisterLog: Off]");
                    }
                    USipServerService.this.disp.registrar.setLogEnable(flag);
                }
                if (intent.hasExtra(USipServerService.INTENT_KEY_STATUS)) {
                    USipServerService.this.disp.out(USipServerService.this.disp.showStatus());
                    if (USipServerService.this.regDataPersistence) {
                        USipServerService.this.disp.out("  Register Data Persistence: On");
                    }
                }
                if (intent.hasExtra(USipServerService.INTENT_KEY_REGISTER)) {
                    USipServerService.this.disp.out(USipServerService.this.disp.showRegister());
                }
            }
        }
    };
    private boolean regDataPersistence = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.disp.getRegistrar().setPersistenceLogic(this);
        registerReceiver(this.receiver, new IntentFilter(INTENT_ACTION));
        Log.d(TAG, "onCreate");
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String domain = intent.getStringExtra("pref_domain");
            String localIp = intent.getStringExtra("pref_localip");
            int localPort = intent.getIntExtra("pref_localport", 5060);
            boolean regDataPersistence = intent.getBooleanExtra("pref_register_data_persistence", false);
            if (startServer(domain, localIp, localPort, regDataPersistence)) {

                Notification.Builder builder = new Notification.Builder(this);
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setTicker(getString(R.string.app_name));
                builder.setContentTitle("通知");
                builder.setContentText("每天进步一点点");
                builder.setWhen(System.currentTimeMillis()); //发送时间
                builder.setDefaults(Notification.DEFAULT_ALL);
                Notification notification = builder.build();
                startForeground(1, notification);
            }
            if (!regDataPersistence) {
                File file = getFileStreamPath(REG_DATA_FILENAME);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (this.regDataPersistence) {
            this.disp.getRegistrar().setPersistenceLogic(this);
        }
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
        stopServer();
        stopForeground(true);
        Log.d(TAG, "onDestroy()");
    }

    public boolean startServer(String domain, String localIp, int localPort, boolean regDataPersistence) {
        this.domain = null;
        this.localIp = null;
        this.localPort = 0;
        this.regDataPersistence = false;
        if (this.disp.isRunning()) {
            return false;
        }
        this.disp.setLogEnable(false);
        this.disp.registrar.setLogEnable(false);
        if (domain == null || domain.trim().length() == 0) {
            domain = "127.0.0.1";
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    Enumeration<InetAddress> addresses = ((NetworkInterface) interfaces.nextElement()).getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        String address = ((InetAddress) addresses.nextElement()).getHostAddress();
                        if (!address.equals("127.0.0.1") && !address.contains(":")) {
                            domain = address;
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        out("[Start SipServer]");
        out("  Domain: " + domain);
        this.disp.setConsoleOut(this);
        JsUdpEventSource udpEvent = new JsUdpEventSource();
        udpEvent.setConsoleOut(this);
        this.disp.registEventSource(udpEvent);
        this.disp.setSipDomain(domain, localPort);
        final String tmpLocalIp = localIp;
        final int tmpLocalPort = localPort;
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    USipServerService.this.disp.start(tmpLocalIp, tmpLocalPort);
                    USipServerService.this.out("  LocalIp: " + USipServerService.this.disp.getEventSource().getMyHost() + "\n  LocalPort: " + USipServerService.this.disp.getEventSource().getMyPort());
                    Thread.currentThread().setName("OK");
                } catch (Exception e) {
                    e.printStackTrace();
                    USipServerService.this.err("! " + e.getClass().getSimpleName());
                    String emsg = e.getMessage();
                    if (emsg != null && emsg.length() > 0) {
                        USipServerService.this.err(emsg);
                    }
                    USipServerService.this.disp.stop();
                    Thread.currentThread().setName("NG");
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
        }
        if (!th.getName().equals("OK")) {
            return false;
        }
        this.domain = domain;
        this.localIp = localIp;
        this.localPort = localPort;
        this.regDataPersistence = regDataPersistence;
        return true;
    }

    public void stopServer() {
        if (this.disp.isRunning()) {
            out("[Stop SipServer]");
            Thread th = new Thread(new Runnable() {
                public void run() {
                    USipServerService.this.disp.stop();
                }
            });
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
            }
            if (!this.regDataPersistence) {
                File file = getFileStreamPath(REG_DATA_FILENAME);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void setSipLogEnable(boolean flag) {
        if (this.disp != null) {
            this.disp.setLogEnable(flag);
        }
    }

    public void setRegisterLogEnable(boolean flag) {
        if (this.disp != null) {
            this.disp.registrar.setLogEnable(flag);
        }
    }

    public String getServerInfo() {
        return "sip:****@" + this.domain + " [" + this.localIp + ":" + this.localPort + "]";
    }

    public void out(String text) {
        Intent intent = new Intent(USipServerActivity.INTENT_ACTION);
        intent.putExtra(USipServerActivity.INTENT_KEY, text);
        sendBroadcast(intent);
    }

    public void err(String text) {
        Intent intent = new Intent(USipServerActivity.INTENT_ACTION);
        intent.putExtra(USipServerActivity.INTENT_KEY, text);
        sendBroadcast(intent);
    }

    public boolean writeRegisterTable(Hashtable<String, JsSimpleRegisterInfo> regTable) {
        if (this.regDataPersistence) {
            try {
                FileOutputStream fos = openFileOutput(REG_DATA_FILENAME, 0);
                for (String key : regTable.keySet()) {
                    JsSimpleRegisterInfo info = (JsSimpleRegisterInfo) regTable.get(key);
                    if (!info.isExpired()) {
                        fos.write(info.toString().getBytes());
                        fos.write(10);
                    }
                }
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean readRegisterTable(Hashtable<String, JsSimpleRegisterInfo> regTable) {
        if (this.regDataPersistence) {
            try {
                this.disp.out("[Register Data Persistence]");
                int registerDataCount = 0;
                if (getFileStreamPath(REG_DATA_FILENAME).exists()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(REG_DATA_FILENAME)));
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        JsSimpleRegisterInfo info = JsSimpleRegisterInfo.parseLine(line);
                        if (!(info == null || info.isExpired())) {
                            regTable.put(info.getToUri().getUser(), info);
                            registerDataCount++;
                        }
                    }
                    br.close();
                }
                if (registerDataCount > 0) {
                    this.disp.out("  Recovered " + registerDataCount + " Register Data.");
                } else {
                    this.disp.out("  No Persistence Data.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
