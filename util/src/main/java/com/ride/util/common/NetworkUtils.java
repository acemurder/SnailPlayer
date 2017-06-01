package com.ride.util.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */
public final class NetworkUtils {

    public NetworkUtils() {
        throw new UnsupportedOperationException("NetworkUtils can't be initialized");
    }

    /**
     * 是否有可用网络
     */
    public static boolean isNetworkAvailable() {
        return getActiveNetWorkInfo() != null;
    }

    private static final int NETWORK_TYPE_GSM      = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN    = 18;

    /**
     * 获取当前网络状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return 网络类型
     * <ul>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_WIFI   } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_4G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_3G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_2G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_UNKNOWN} </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_NO     } </li>
     * </ul>
     */
    public static NetworkType getNetworkStatus() {
        NetworkType type = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetWorkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                type = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        type = NetworkType.NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        type = NetworkType.NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        type = NetworkType.NETWORK_4G;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            type = NetworkType.NETWORK_3G;
                        } else {
                            type = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                type = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return type;
    }

    public static NetworkInfo getActiveNetWorkInfo() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                return activeNetInfo;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }
}
