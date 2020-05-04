package com.example.ericw.fastconnect;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WifiConfigFilter {

    public static List<ScanResult> filterScanResult(List<ScanResult> results
            , WifiInfo current_wifi, int difference) {
        List<ScanResult> tmp = new LinkedList<>();
        final int currentSignalStrength = current_wifi.getRssi();
        final String currentBSSID = current_wifi.getBSSID();
        final String currentSSID = current_wifi.getSSID();
        for (ScanResult result : results) {
            if (WifiManager.compareSignalLevel(result.level, currentSignalStrength) >= difference) {
                if (currentSSID.equals("\"" + result.SSID + "\"") && currentBSSID.equals(result.BSSID)) {
                    continue;
                }
                tmp.add(result);
            }
        }
        return tmp;
    }

    public static List<WifiConfiguration> filterConfiguredList(List<ScanResult> results
            , List<WifiConfiguration> configurations) {
        List<WifiConfiguration> filter_list = new ArrayList<>();
        if (results != null && configurations != null) {
            results = sortScanResult(results);
            while (!results.isEmpty() && !configurations.isEmpty()) {
                ScanResult i = results.remove(0);
                for (WifiConfiguration config : configurations) {
                    String network_id;
                    if ((network_id = config.SSID) != null && network_id.equals("\"" + i.SSID + "\"")) {
                        filter_list.add(config);
                        configurations.remove(config);
                        break;
                    }
                }
            }
        }
        return filter_list;
    }

    private static List<ScanResult> sortScanResult(List<ScanResult> list) {
        List<ScanResult> sorted_list = new LinkedList<>();
        while (!list.isEmpty()) {
            int index = 0;
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).level > list.get(index).level) {
                    index = i;
                }
            }
            sorted_list.add(list.remove(index));
        }
        return sorted_list;
    }
}
