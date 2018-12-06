package org.joinsip.usipserver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.joinsip.core.JsConsole;

public class USipServerActivity extends Activity implements JsConsole {
    public static final String INTENT_ACTION = "SSA_BROADCAST";
    public static final String INTENT_ACTION_SERVICE_STARTED = "SSA_SERVICE_STARTED";
    public static final String INTENT_ACTION_SERVICE_STOPPED = "SSA_SERVICE_STOPPED";
    public static final String INTENT_KEY = "SSA_CONSOLE";
    public static final String INTENT_KEY_SERVERINFO = "SSA_SERVERINFO";
    public CheckBox checkboxRegisterLog;
    public CheckBox checkboxSipLog;
    public TextView console = null;
    public StringBuilder consoleText;
    public Handler handler;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(USipServerActivity.INTENT_ACTION)) {
                USipServerActivity.this.out(intent.getStringExtra(USipServerActivity.INTENT_KEY));
            }
        }
    };

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    public void startAction(View v) {
        USipServerActivity.this.startUSipServerService();
        checkboxSipLog.setChecked(false);
        checkboxRegisterLog.setChecked(false);
    }

    public void configer(View v) {
        USipServerActivity.this.startActivity(new Intent(USipServerActivity.this, ConfigActivity.class));
    }

    public void stop(View v) {
        USipServerActivity.this.stopService(new Intent(USipServerActivity.this.getBaseContext(), USipServerService.class));
    }

    public void status(View v) {
        Intent intent = new Intent(USipServerService.INTENT_ACTION);
        intent.putExtra(USipServerService.INTENT_KEY_STATUS, "");
        USipServerActivity.this.sendBroadcast(intent);
    }

    public void register(View v) {
        Intent intent = new Intent(USipServerService.INTENT_ACTION);
        intent.putExtra(USipServerService.INTENT_KEY_REGISTER, "");
        USipServerActivity.this.sendBroadcast(intent);
    }

    public void clear(View v) {
        USipServerActivity.this.clearConsole();
    }

    public void copy(View v) {
        ((ClipboardManager) USipServerActivity.this.getSystemService("clipboard")).setText(USipServerActivity.this.consoleText);
    }

    public void sipLog(View v) {
        Intent intent = new Intent(USipServerService.INTENT_ACTION);
        intent.putExtra(USipServerService.INTENT_KEY_SIPLOG, checkboxSipLog.isChecked());
        USipServerActivity.this.sendBroadcast(intent);
    }

    public void registerLog(View v) {
        Intent intent = new Intent(USipServerService.INTENT_ACTION);
        intent.putExtra(USipServerService.INTENT_KEY_REGLOG, checkboxRegisterLog.isChecked());
        USipServerActivity.this.sendBroadcast(intent);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.handler = new Handler();
        this.consoleText = new StringBuilder(512);
        this.console = findViewById(R.id.text_console);

        final CheckBox checkboxSipLog =  findViewById(R.id.checkbox_siplog);
        this.checkboxSipLog = checkboxSipLog;
        final CheckBox checkboxRegisterLog = findViewById(R.id.checkbox_registerlog);
        this.checkboxRegisterLog = checkboxRegisterLog;

        registerReceiver(this.receiver, new IntentFilter(INTENT_ACTION));
        registerReceiver(this.receiver, new IntentFilter(INTENT_ACTION_SERVICE_STARTED));
        registerReceiver(this.receiver, new IntentFilter(INTENT_ACTION_SERVICE_STOPPED));
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_auto_start", true)) {
            startUSipServerService();
        }
    }

    private void startUSipServerService() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String domain = sharedPreferences.getString("pref_domain", "");
        String localIp = sharedPreferences.getString("pref_localip", "0.0.0.0");
        int localPort = 5060;
        boolean regDataPersistence = sharedPreferences.getBoolean("pref_register_data_persistence", false);
        try {
            localPort = Integer.parseInt(sharedPreferences.getString("pref_localport", "5060"));
        } catch (NumberFormatException e) {
        }
        Intent intent = new Intent(getBaseContext(), USipServerService.class);
        intent.putExtra("pref_domain", domain);
        intent.putExtra("pref_localip", localIp);
        intent.putExtra("pref_localport", localPort);
        intent.putExtra("pref_register_data_persistence", regDataPersistence);
        startService(intent);
    }

    public void printIpAddrList() {
        try {
            out("ip_addr list:");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = ((NetworkInterface) interfaces.nextElement()).getInetAddresses();
                while (addresses.hasMoreElements()) {
                    out("  " + ((InetAddress) addresses.nextElement()).getHostAddress());
                }
            }
        } catch (SocketException e) {
            err(e.getMessage());
        }
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    public void clearConsole() {
        this.consoleText.setLength(0);
        this.handler.post(new Runnable() {
            public void run() {
                USipServerActivity.this.console.setText(USipServerActivity.this.consoleText);
            }
        });
    }

    public void out(String text) {
        if (text != null) {
            Log.i("OUT", text);
            printConsole(text);
        }
    }

    public void err(String text) {
        if (text != null) {
            Log.i("ERR", text);
            printConsole(text);
        }
    }

    public void printConsole(String text) {
        Log.e("USIP",text+"");
//        this.consoleText.append(text).append('\n');
//        this.handler.post(new Runnable() {
//            public void run() {
//                USipServerActivity.this.console.setText(USipServerActivity.this.consoleText);
//                Layout layout = USipServerActivity.this.console.getLayout();
//                if (layout != null) {
//                    int max = USipServerActivity.this.console.getHeight() / USipServerActivity.this.console.getLineHeight();
//                    boolean updateFlag = false;
//                    for (int lines = layout.getLineCount(); lines > max; lines--) {
//                        USipServerActivity.this.consoleText.replace(0, USipServerActivity.this.consoleText.indexOf("\n") + 1, "");
//                        updateFlag = true;
//                    }
//                    if (updateFlag) {
//                        USipServerActivity.this.console.setText(USipServerActivity.this.consoleText);
//                    }
//                }
//            }
//        });
    }

    public static final int countLine(StringBuilder text) {
        int lines = 1;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == '\n') {
                lines++;
            }
        }
        return lines;
    }
}
