package com.example.ericw.fastconnect;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Do everything inside start service, end everything inside end service
 */
public class SwitchWifiService extends Service {

    private static final int NOTIFICATION_ID = 543;

    public static final String START_SERVICE
            = "com.example.ericw.fastconnect.SwitchWifiService.start_service";

    public static final String END_SERVICE
            = "com.example.ericw.fastconnect.SwitchWifiService.end_service";

    public static final String UPDATE_SERVICE_STATUS
            = "com.example.ericw.fastconnect.SwitchWifiService.update_service_status";

    public static final String NEW_LOG_MESSAGE_AVAILABLE
            = "com.example.ericw.fastconnect.SwitchWifiService.new_log_message_available";

    public static final String WIFI_STATE_CHANGED_ACTION
            = WifiManager.WIFI_STATE_CHANGED_ACTION;

    private static final int MAX_LOG_QUEUE = 30;

    private final static int SCAN_INTERVAL = 10;

    public static Toast toast = null;

    public static boolean pendingNewConnection = false;

    private static boolean isConnecting = false;

    private final IBinder mBinder = new LocalBinder();

    private boolean isServiceRunning = false;

    private static int last_scan_time = SCAN_INTERVAL;

    private String networkID = "<unknown ssid>";

    private String connection_status = "DISCONNECTED";

    private String wifi_state = "DISABLED";

    private Intent broadcast_intent;

    private LocalBroadcastManager broadcast;

    private WifiStateMonitor monitor;

    private LocationProviderMonitor lp_monitor;

    private SharedPreferences preferences;

    private WifiConnectionManager connectionManager = WifiConnectionManager.getInstance();

    private int pref_scan_trigger_level = 0;

    private int pref_wifi_switch_trigger_diff = 0;

    private int pref_active_scan_trigger_level = 0;

    private boolean pref_active_scan_enable = true;

    private WifiConfiguration default_config;

    private static Queue<String> log_queue = new LinkedList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action;
        if (intent != null && (action = intent.getAction()) != null) {
            if (action.equals(START_SERVICE)) {
                startService();
            } else {
                endService();
            }
        }
        return START_STICKY;
    }

    private void startService() {
        if (isServiceRunning) return;
        isServiceRunning = true;
        broadcast_intent = new Intent();
        broadcast = LocalBroadcastManager.getInstance(this);
        sendBroadcast(UPDATE_SERVICE_STATUS);

        startForeground(
                NOTIFICATION_ID,
                NotificationFactory.createForegroundNotification(this, MainActivity.class)
        );
        loadPreference();
        setMonitor();
        setLp_monitor();
        monitor.enable();
        lp_monitor.enable();
    }

    private void endService() {
        if (!isServiceRunning) return;
        isServiceRunning = false;
        sendBroadcast(UPDATE_SERVICE_STATUS);

        stopForeground(true);
        stopSelf();
        monitor.disable();
        lp_monitor.disable();
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }

    public String getWifiState() {
        return wifi_state;
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_SERVICE_STATUS);
        filter.addAction(WIFI_STATE_CHANGED_ACTION);
        filter.addAction(NEW_LOG_MESSAGE_AVAILABLE);
        return filter;
    }

    public String getNewLogMessageAvailable() {
        if (!log_queue.isEmpty()) {
            return log_queue.poll();
        }
        return null;
    }

    public boolean isLogQueueEmpty() {
        return log_queue.isEmpty();
    }

    private void loadPreference() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (key.equals(getResources().getString(R.string.trigger_guard_key))) {
                            pref_scan_trigger_level
                                    = SignalParser.parseSignal(preferences.getInt(key, 0));
//                            Log.d("TESTING", "PREF_SCAN_TRIGGER_LEVEL = " + pref_scan_trigger_level);
                        } else if (key.equals(getResources().getString(R.string.trigger_diff_key))) {
                            pref_wifi_switch_trigger_diff
                                    = preferences.getInt(key, 0);
//                            Log.d("TESTING", "PREF_WIFI_SWITCH_TRIGGER_DIFF = " + pref_wifi_switch_trigger_diff);
                        } else if (key.equals(getResources().getString(R.string.trigger_diff_key))) {
                            pref_active_scan_enable
                                    = preferences.getBoolean(key, false);
//                            Log.d("TESTING", "PREF_ACTIVE_SCAN_ENABLE = " + pref_active_scan_enable);
                        } else if (key.equals(getResources().getString(R.string.active_scan_point_key))) {
                            pref_active_scan_trigger_level
                                    = SignalParser.parseSignal(preferences.getInt(key, 0));
//                            Log.d("TESTING", "PREF_ACTIVE_SCAN_TRIGGER_LEVEL = " + pref_active_scan_trigger_level);
                        }
                    }
                };
        pref_scan_trigger_level
                = SignalParser.parseSignal(preferences.getInt(
                getResources().getString(R.string.trigger_guard_key), 0));
        pref_wifi_switch_trigger_diff
                = preferences.getInt(
                getResources().getString(R.string.trigger_diff_key), 0);
        pref_active_scan_enable
                = preferences.getBoolean(
                getResources().getString(R.string.active_scan_key), false);
        pref_active_scan_trigger_level
                = SignalParser.parseSignal(preferences.getInt(
                getResources().getString(R.string.active_scan_point_key), 0));
//        Log.d("TESTING", "PREF_SCAN_TRIGGER_LEVEL = " + pref_scan_trigger_level);
//        Log.d("TESTING", "PREF_WIFI_SWITCH_TRIGGER_DIFF = " + pref_wifi_switch_trigger_diff);
//        Log.d("TESTING", "PREF_ACTIVE_SCAN_ENABLE = " + pref_active_scan_enable);
//        Log.d("TESTING", "PREF_ACTIVE_SCAN_TRIGGER_LEVEL = " + pref_active_scan_trigger_level);
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void sendBroadcast(String action) {
        broadcast.sendBroadcast(broadcast_intent.setAction(action));
    }

    private void setMonitor() {
        monitor = new WifiStateMonitor(getApplicationContext());
        monitor.setStatusChangedListener(new WifiStateMonitor.StatusChangedListenerAdaptor() {
            @Override
            public void onConnected() {
                if (!connection_status.equals("CONNECTED")) {
                    isConnecting = false;
                    connection_status = "CONNECTED";
                    WifiInfo info = monitor.getWifiManager().getConnectionInfo();
                    networkID = info.getSSID();

                    log("CONNECTED TO " + networkID);
                    log("BSSID : " + info.getBSSID());

                    //Remove pending configuration stack
                    connectionManager.clear();
                    setDefaultWifiConfig();
                }
            }

            @Override
            public void onDisconnected() {
                if (!connection_status.equals("DISCONNECTED")) {
                    isConnecting = false;
                    connection_status = "DISCONNECTED";
                    networkID = "UNKNOWN";

                    log("DISCONNECTED");
                }
            }

            @Override
            public void onConnecting() {
                if (!connection_status.equals("CONNECTING")) {
                    isConnecting = true;
                    connection_status = "CONNECTING";
                    networkID = monitor.getWifiManager().getConnectionInfo().getSSID();

                    log("CONNECTING");
                }
            }

            @Override
            public void onConnectionFailed() {
                if (connectionManager.isEmpty()) {
                    log("NEW CONNECTION FAILED, RETURN TO DEFAULT");
                    returnToDefaultNetwork();
                } else {
                    log("NEW CONNECTION FAILED, TRY NEXT");
                    establishNewConnection();
                }
            }

            @Override
            public void onScanResultAvailable(List<ScanResult> list) {
                if (!list.isEmpty() && pendingNewConnection) {
                    log("SCANNING COMPLETED");
                    pendingNewConnection = false;

                    if (!isConnecting) {
                        // Filter configured wifi from scan result
                        WifiManager wifiManager = monitor.getWifiManager();
                        WifiInfo current_wifi = wifiManager.getConnectionInfo();

                        //Filter out signal weaker than current or difference less than defined
                        List<ScanResult> results = WifiConfigFilter.filterScanResult(
                                list, current_wifi, pref_wifi_switch_trigger_diff);

                        //Filter out configured network
                        List<WifiConfiguration> filtered_results = WifiConfigFilter.filterConfiguredList(
                                results, wifiManager.getConfiguredNetworks());

                        if (filtered_results.isEmpty()) {
                            log("NO BETTER NETWORK FOUND");
                            return;
                        }

                        connectionManager.addAll(filtered_results);
                        establishNewConnection();
                    }
                }
            }

            @Override
            public void onWifiStateChanged(String state) {
                wifi_state = state;
                sendBroadcast(WIFI_STATE_CHANGED_ACTION);
            }

            @Override
            public void onSignalLevelChanged(int level) {
                final String strength = SignalParser.signalToString(level);
                if (!strength.equals("")) {
                    log("LEVEL CHANGED: " + SignalParser.signalToString(level) + " (" + level + ")");
                }
                if (pref_scan_trigger_level >= level && last_scan_time >= SCAN_INTERVAL) {
                    log("SCANNING...");
                    pendingNewConnection = true;
                    monitor.getWifiManager().startScan();
                    startTrackingLastScanTime();
                }
            }
        });
    }

    private void startTrackingLastScanTime() {
        last_scan_time = 0;
        new CountDownTimer(SCAN_INTERVAL * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                last_scan_time = SCAN_INTERVAL;
            }
        }.start();
    }

    private void setDefaultWifiConfig() {
        WifiManager wifiManager = monitor.getWifiManager();
        String currentSSID = wifiManager.getConnectionInfo().getSSID();
        for (WifiConfiguration configuration : wifiManager.getConfiguredNetworks()) {
            if (configuration.SSID.equals(currentSSID)) {
                default_config = configuration;

                log("SET DEFAULT NETWORK TO " + default_config.SSID);
                break;
            }
        }
    }

    private void establishNewConnection() {
        WifiConfiguration configuration = connectionManager.nextAvailableConfig();
        if (configuration != null) {
            WifiManager wifiManager = monitor.getWifiManager();
            wifiManager.disconnect();
            log("NEXT ATTEMPT: " + configuration.SSID);
            wifiManager.enableNetwork(configuration.networkId, true);
            wifiManager.reconnect();
        }
    }

    private void returnToDefaultNetwork() {
        if (default_config != null) {
            WifiManager wifiManager = monitor.getWifiManager();
            wifiManager.disconnect();
            log("CONNECTING DEFAULT NETWORK: " + default_config.SSID);
            wifiManager.enableNetwork(default_config.networkId, true);
            wifiManager.reconnect();
        }
    }

    public void log(String message) {
        if (log_queue.size() >= MAX_LOG_QUEUE) {
            log_queue.clear();
        }
        Log.d("TESTING", message);
        log_queue.add(message);
        sendBroadcast(NEW_LOG_MESSAGE_AVAILABLE);
    }

    private void setLp_monitor() {
        lp_monitor = new LocationProviderMonitor(getApplicationContext());
        lp_monitor.setProviderChangedListener(new LocationProviderMonitor.ProviderChangedListener() {
            @Override
            public void onProviderChanged(boolean isProviderEnable) {
                if (!(isProviderEnable) && isServiceRunning) {
                    endService();
                    showToast("Please turn on location service");
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void showToast(final String message) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public class LocalBinder extends Binder {
        SwitchWifiService getService() {
            return SwitchWifiService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
