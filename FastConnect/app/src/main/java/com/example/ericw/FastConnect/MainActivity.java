package com.example.ericw.fastconnect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;

    private SwitchWifiService mService;

    private boolean mBound = false;

    private Context context = this;

    private TextView service_status_text_view;
    private TextView wifi_state_text_view;
    private ToggleButton service_toggle_button;
    private LinearLayout log_container;
    private LinearLayout level_changed_log;

    private static final int max_log_line = 30;
    private int current_log_line_count = 0;
    private int current_level_log_line_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                SwitchWifiService.pendingNewConnection = true;
                if (wifiManager != null)
                    wifiManager.startScan();
                addLogMessage("SCANNING...");
            }
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_container.removeAllViews();
                level_changed_log.removeAllViews();
                current_log_line_count = 0;
                current_level_log_line_count = 0;
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////

        service_status_text_view = findViewById(R.id.service_status_text_view);
        wifi_state_text_view = findViewById(R.id.wifi_state_text_view);
        service_toggle_button = findViewById(R.id.service_toggle_buttons);

        log_container = findViewById(R.id.action_log);
        level_changed_log = findViewById(R.id.level_log);

        service_toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService();
                } else {
                    endService();
                }
            }
        });

        // Check auto start service on launch
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getResources().getString(R.string.auto_launch_key), false)) {
            service_toggle_button.setChecked(true);
        }

        // Listens for service's UI update request
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action;
                if (intent != null && (action = intent.getAction()) != null && mBound) {
                    switch (action) {
                        case SwitchWifiService.UPDATE_SERVICE_STATUS:
                            String status;
                            if (mService.isServiceRunning()) {
                                status = "RUNNING";
                                service_toggle_button.setChecked(true);
                            } else {
                                status = "STOPPED";
                                service_toggle_button.setChecked(false);
                            }
                            wifi_state_text_view.setText(mService.getWifiState());
                            service_status_text_view.setText(status);
                            break;
                        case SwitchWifiService.WIFI_STATE_CHANGED_ACTION:
                            wifi_state_text_view.setText(mService.getWifiState());
                            break;
                        case SwitchWifiService.NEW_LOG_MESSAGE_AVAILABLE:
                            addLogMessage(mService.getNewLogMessageAvailable());
                            break;
                    }
                }
            }
        };


        //DEBUGGING, NEED TO CHANGE IT
        requestPermission(this);
    }

    private void addLogMessage(String message) {
        if (message != null) {
            TextView valueTV = new TextView(context);
            valueTV.setTextSize(15);
            valueTV.setText(message);
            valueTV.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (message.contains("LEVEL")) {
                if (current_level_log_line_count >= max_log_line) {
                    level_changed_log.removeAllViews();
                    current_level_log_line_count = 0;
                }
                current_level_log_line_count++;
                level_changed_log.addView(valueTV);
            } else {
                if (current_log_line_count >= max_log_line) {
                    log_container.removeAllViews();
                    current_log_line_count = 0;
                }
                current_log_line_count++;
                log_container.addView(valueTV);
            }
        }
    }

    private void startService() {
        Intent startIntent = new Intent(context, SwitchWifiService.class);
        startIntent.setAction(SwitchWifiService.START_SERVICE);
        startService(startIntent);
    }

    private void endService() {
        Intent startIntent = new Intent(context, SwitchWifiService.class);
        startIntent.setAction(SwitchWifiService.END_SERVICE);
        startService(startIntent);
    }

    //DEBUGGING, NEED TO CHANGE IT
    private void requestPermission(MainActivity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SwitchWifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, SwitchWifiService.getIntentFilter());
        checkIfLocationServiceOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
        mBound = false;
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkIfLocationServiceOn() {
        if (!isLocationServiceEnable())
            buildAlertMessageNoGps();
    }

    private boolean isLocationServiceEnable() {
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (lm != null && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("FastConnect requires location service, do you want to turn on now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SwitchWifiService.LocalBinder binder = (SwitchWifiService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            //Service status
            String service_status = mService.isServiceRunning() ? "RUNNING" : "STOPPED";
            service_status_text_view.setText(service_status);

            //Wifi state
            wifi_state_text_view.setText(mService.getWifiState());

            //Toggle button
            service_toggle_button.setChecked(mService.isServiceRunning());

            while (!mService.isLogQueueEmpty()) {
                addLogMessage(mService.getNewLogMessageAvailable());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
