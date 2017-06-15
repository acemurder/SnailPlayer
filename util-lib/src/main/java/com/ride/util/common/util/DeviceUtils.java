package com.ride.util.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.ride.util.common.log.Timber;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public final class DeviceUtils {

    public DeviceUtils() {
        throw new UnsupportedOperationException("DeviceUtils can't be initialized");
    }

    /**
     * 获取IMEI
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI() {
        try {
            TelephonyManager localTelephonyManager =
                    (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            return localTelephonyManager.getDeviceId();
        } catch (Exception e) {
            Timber.i("TelephonyManager.getDeviceId() exception : " + e);
        }
        return null;
    }

    /**
     * 获取ANDROID_ID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        try {
            return Settings.Secure.getString(Utils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Timber.i("Settings.Secure.ANDROID_ID exception : " + e);
        }
        return null;
    }

    /**
     * 获取mac地址
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress() {
        String macAddress = "";
        WifiManager wifiManager = (WifiManager) Utils.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            macAddress = wifiInfo.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    /**
     * 获取硬件序列号
     */
    @SuppressLint("HardwareIds")
    public static String getHardwareSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取系统版本号
     */
    public static String getAndroidOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机的UA信息，如：XIAOMI NOTE
     */
    public static String getDeviceUA() {
        return Build.MODEL;
    }
}
