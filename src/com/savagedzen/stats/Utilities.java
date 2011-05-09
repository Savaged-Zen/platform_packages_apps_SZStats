package com.savagedzen.stats;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.os.Build;
import android.content.ComponentName;
import android.content.Intent;

public class Utilities {

    private static final String TAG = "Utilities";

    public static String getUniqueID(Context ctx){
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        String device_id = digest(tm.getDeviceId());
        if (device_id == null) {
            String wifiInterface = SystemProperties.get("wifi.interface");
            try {
                String wifiMac = new String (NetworkInterface.getByName(wifiInterface).getHardwareAddress());
                device_id = digest(wifiMac);
            } catch (Exception e) {
                device_id = null;
            }
        }

        return device_id;
    }
    
    public static String getCarrier(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String carrier = tm.getNetworkOperatorName();
        if ("".equals(carrier)) {
            carrier = "Unknown";
        }
        return carrier;
    }

    public static String getCarrierId(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierId = tm.getNetworkOperator();
        if ("".equals(carrierId)) {
            carrierId = "0";
        }
        return carrierId;
    }
    
    public static String getCountryCode(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        if (countryCode.equals("")) {
            countryCode = "Unknown";
        }
        return countryCode;
    }

    public static String getDevice() {
        return SystemProperties.get("ro.product.device");
    }

    public static String getModVersion() {
        return SystemProperties.get("ro.modversion");
    }

    public static String getKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                int position = 0;
                procVersionStr = reader.readLine();
                procVersionStr = procVersionStr.replaceFirst("Linux", "");
                procVersionStr = procVersionStr.replaceFirst("version", "");
                procVersionStr = procVersionStr.trim();
                  for (int i=0; i < procVersionStr.length(); i++)
		       if (procVersionStr.charAt(i) == ' ')
		           position = i;

	                   char[] newstring = new char[position];

                  for (int j = 0; j < position; j++)
		      newstring[j] = procVersionStr.charAt(j);

               procVersionStr = new String(newstring);

            } finally {
                reader.close();
            }

            return procVersionStr;

        } catch (IOException e) {
            Log.e(TAG,
                "IO Exception when getting kernel version for Device Info screen",
                e);

            return "Unavailable";
        }

    }

    public static String digest(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new BigInteger(1, md.digest(input.getBytes())).toString(16).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

}
