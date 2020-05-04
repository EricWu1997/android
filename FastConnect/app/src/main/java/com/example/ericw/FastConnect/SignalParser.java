package com.example.ericw.fastconnect;

public class SignalParser {
    // RSSI Levels as used by notification icon
    // Level 4  -55 <= RSSI
    // Level 3  -66 <= RSSI < -55
    // Level 2  -77 <= RSSI < -67
    // Level 1  -88 <= RSSI < -78
    // Level 0         RSSI < -88
    public static int parseSignal(int strength_level) {
        switch (strength_level) {
            case 0:
                return 0;
            case 1:
                return -55;
            case 2:
                return -66;
            case 3:
                return -77;
            case 4:
                return -88;
        }
        return -127;
    }

    public static String signalToString(int sig_strength) {
        if (sig_strength >= -55) {
            return "Excellent";
        } else if (sig_strength >= -66) {
            return "Good";
        } else if (sig_strength >= -77) {
            return "Fair";
        } else if (sig_strength >= -88) {
            return "Weak";
        } else if (sig_strength >= -126) {
            return "Poor";
        }
        return "";
    }
}
