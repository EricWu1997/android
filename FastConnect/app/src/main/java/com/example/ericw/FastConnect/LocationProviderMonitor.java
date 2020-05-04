package com.example.ericw.fastconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;

//on enable will signal whether provider is enable or not
public class LocationProviderMonitor {

    private BroadcastReceiver receiver;

    private IntentFilter intentFilter;

    private Context context;

    private ProviderChangedListener listener;

    LocationProviderMonitor(Context context) {
        this.context = context;
        intentFilter = new IntentFilter();
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        createReceiver();
    }

    private void createReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action;
                if (intent != null && (action = intent.getAction()) != null && listener != null) {
                    if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION))
                        listener.onProviderChanged(isLocationServiceEnable());
                }
            }
        };
    }

    private boolean isLocationServiceEnable() {
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (lm != null && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));
    }

    public void setProviderChangedListener(ProviderChangedListener listener) {
        this.listener = listener;
    }

    public void enable() {
        context.registerReceiver(receiver, intentFilter);
        if (listener != null) {
            listener.onProviderChanged(isLocationServiceEnable());
        }
    }

    public void disable() {
        context.unregisterReceiver(receiver);
    }

    public interface ProviderChangedListener {
        void onProviderChanged(boolean isProviderEnable);
    }
}
