package com.momo.mousehome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Vendor Autostart launches MainActivity on this box.
// MainActivity reads the saved START/STOP state and acts accordingly.
// This receiver intentionally does not bypass a saved STOP state.
public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        // No direct daemon start here.
    }
}
