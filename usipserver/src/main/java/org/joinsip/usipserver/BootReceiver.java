package org.joinsip.usipserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_auto_start", true)) {
            Intent i = new Intent(context, USipServerActivity.class);
            i.addFlags(268435456);
            context.startActivity(i);
        }
    }
}
