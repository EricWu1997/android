package com.example.ericw.fastconnect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiStateMonitor {

    private StatusChangedListener listener;

    private IntentFilter intentFilter;

    private boolean authenticating = false;

    private WifiManager manager;

    private BroadcastReceiver receiver;

    private Context context;

    WifiStateMonitor(Context context) {
        this.context = context;
        manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        createReceiver();
    }

    public void enable() {
        context.registerReceiver(receiver, intentFilter);
    }

    public void disable() {
        context.unregisterReceiver(receiver);
    }

    public WifiManager getWifiManager() {
        return manager;
    }

    public void setStatusChangedListener(StatusChangedListener listener) {
        this.listener = listener;
    }

    /**
     * An wifi status listener receives intent from an WifiManager.
     * intent indicate wifi status related events, such as the on connect,
     * disconnect or authenticating.
     */
    public interface StatusChangedListener {

        void onScanResultAvailable(List<ScanResult> list);

        void onDisconnected();

        void onConnected();

        void onConnecting();

        void onConnectionFailed();

        void onWifiStateChanged(String state);

        void onSignalLevelChanged(int level);
    }

    /**
     * This adapter class provides empty implementations of the methods
     * from StatusChangedListener. Any custom listener that cares only
     * about a subset of the methods of this listener can simply subclass
     * this adapter class instead of implementing the interface directly.
     *
     * @see StatusChangedListener
     */
    public static abstract class StatusChangedListenerAdaptor implements StatusChangedListener {
        @Override
        public void onScanResultAvailable(List<ScanResult> list) {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onConnected() {
        }

        @Override
        public void onConnecting() {
        }

        @Override
        public void onConnectionFailed() {
        }

        @Override
        public void onWifiStateChanged(String state) {
        }

        @Override
        public void onSignalLevelChanged(int level) {
        }
    }

    private void createReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action;
                if (intent != null && (action = intent.getAction()) != null && listener != null) {
                    switch (action) {
                        case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                            List<ScanResult> results = manager.getScanResults();
                            if (!results.isEmpty())
                                listener.onScanResultAvailable(results);
                            break;
                        case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                            NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                            if (nwInfo != null)
                                switch (nwInfo.getDetailedState()) {
                                    case CONNECTED:
                                        listener.onConnected();
                                        authenticating = false;
                                        break;
                                    case DISCONNECTED:
                                        if (authenticating) {
                                            listener.onConnectionFailed();
                                        }
                                        authenticating = false;
                                        listener.onDisconnected();
                                        break;
                                    case CONNECTING:
                                        listener.onConnecting();
                                        authenticating = true;
                                        break;
                                    case AUTHENTICATING:
                                        authenticating = true;
                                        break;
                                }
                            break;
                        case WifiManager.WIFI_STATE_CHANGED_ACTION:
                            String result;
                            switch (manager.getWifiState()) {
                                case WifiManager.WIFI_STATE_DISABLED:
                                    result = "DISABLED";
                                    break;
                                case WifiManager.WIFI_STATE_DISABLING:
                                    result = "DISABLING";
                                    break;
                                case WifiManager.WIFI_STATE_ENABLED:
                                    result = "ENABLED";
                                    break;
                                case WifiManager.WIFI_STATE_ENABLING:
                                    result = "ENABLING";
                                    break;
                                case WifiManager.WIFI_STATE_UNKNOWN:
                                default:
                                    result = "UNKNOWN";
                                    break;
                            }
                            listener.onWifiStateChanged(result);
                            break;
                        case WifiManager.RSSI_CHANGED_ACTION:
                            listener.onSignalLevelChanged(manager.getConnectionInfo().getRssi());
                            break;
                    }
                }
            }
        };
    }
}
