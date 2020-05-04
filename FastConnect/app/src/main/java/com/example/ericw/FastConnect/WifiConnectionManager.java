package com.example.ericw.fastconnect;

import android.net.wifi.WifiConfiguration;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WifiConnectionManager {

    private static WifiConnectionManager instance = null;

    private Queue<WifiConfiguration> configurations_queue;

    private WifiConnectionManager() {
        configurations_queue = new LinkedList<>();
    }

    public WifiConfiguration nextAvailableConfig() {
        if (configurations_queue.isEmpty()) return null;
        return configurations_queue.poll();
    }

    public void addAll(List<WifiConfiguration> list) {
        configurations_queue.addAll(list);
    }

    public void clear() {
        configurations_queue.clear();
    }

    public static WifiConnectionManager getInstance() {
        return (instance == null ? (instance = new WifiConnectionManager()) : instance);
    }

    public boolean isEmpty() {
        return configurations_queue.isEmpty();
    }
}
