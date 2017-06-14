package com.ride.snailplayer.net.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.AppUtils;
import com.ride.util.common.util.DeviceUtils;
import com.ride.util.common.util.EncodeUtils;
import com.ride.util.common.util.EncryptUtils;
import com.ride.util.common.util.NetworkUtils;
import com.ride.util.common.util.SPUtils;
import com.ride.util.common.util.ScreenUtils;
import com.ride.util.common.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public class IQiYiApiParamsUtils {

    private static final String SP_DEVICE_ID = "device_id";

    private static String mDeviceID;//设备唯一标识
    private static String mHardwareInfo;//部分硬件序列
    private static String mResolution;//分辨率

    private IQiYiApiParamsUtils() {
        throw new UnsupportedOperationException("IQiYiApiParamsUtils can't be initialized");
    }

    /**
     * 构建Open API通用参数
     * @return
     */
    public static Map<String, String> genCommonParams() {
        Map<String, String> params = new HashMap<>();
        params.put("app_k", SnailPlayerConfig.APP_KEY);
        params.put("version", SnailPlayerConfig.APP_V);
        params.put("app_v", SnailPlayerConfig.APP_V);
        params.put("app_t", SnailPlayerConfig.APP_T);
        params.put("platform_id", SnailPlayerConfig.PLATFORM_ID);
        params.put("dev_os", DeviceUtils.getAndroidOSVersion());
        params.put("dev_ua", DeviceUtils.getDeviceUA());
        params.put("dev_hw", getHardwareInfo());
        params.put("net_sts", getNetWorkType());
        params.put("scrn_sts", "0"); //屏幕状态，0：默认， 1：竖屏， 2：横屏，3：mini
        params.put("scrn_res", getResolution()); //屏幕分辨率
        params.put("scrn_dpi", String.valueOf(ScreenUtils.getScreenDensityDpi())); //屏幕密度
        params.put("qyid", getDeviceID());
        params.put("secure_v", "1"); //客户端安全版本，固定写死为1
        params.put("secure_p", "GPhone");//客户端安全标识
        params.put("core", ""); //播放内核类型
        params.put("req_sn", String.valueOf(System.currentTimeMillis())); //客户端请求接口的时间戳,长整数
        params.put("req_times", "1"); //请求次数,客户端请求接口的次数，第一次请求req_times=1,如失败后重试一次，则req_times=2，以此类推
        return params;
    }

    /**
     * 构建获取频道列表的参数
     * @return
     */
    public static Map<String, String> genChannelParams() {
        Map<String, String> params = genCommonParams();
        params.put("type", "list"); //请求频道列表
        return params;
    }

    /**
     * 获取频道详情信息参数
     * @param channelId 频道ID
     * @param channelName 频道名称
     * @param pageIndex 分页码
     * @param pageSize 每页数据条数
     * @return
     */
    public static Map<String, String> genChannelDetailParams(String channelId, String channelName,
                                                             int pageIndex, int pageSize) {
        Map<String, String> params = genCommonParams();
        params.put("type", "detail"); //频道详情
        params.put("channel_name", channelName);

        /**
         * 排序方式（排序方式，若无此参数，则默认按照相关性排序,使用此参数对应基线中片库:热播榜,好评榜,新上线 等功能）:
         * 1 按照相关性排序；
         * 2 视频创建时间；
         * 4 最新更新时间排序(新上线)；
         * 5 vv；
         * 8 评分 (好评榜)；
         * 9 历史点击量排序；
         * 10 周点击排序；
         * 11 昨日点击排序 (热播榜)排序方式。
         */
        params.put("mode", "11");

        /**
         * 付费方式（付费方式，若无此参数，则返回所有付费和非付费的）：
         * 0：免费
         * 1：付费未划价(是付费片子，但是还没有定价，这样状态的片子还不允许上线)
         * 2：付费已划价(判断是否会员视频，直接判断is_purchase=2即可)
         */
        // params.put("is_purchase", "2");

        /**
         * 1： 仅要付费点播的结果
         * 0：不要付费点播的结果
         * 不传：要付费点播和非付费点播的结果
         */
        params.put("on_demand", "0");

        params.put("page_num", String.valueOf(pageIndex));
        params.put("page_size", String.valueOf(pageSize));

        //是否需要返回频道标签列表
        params.put("require_tag_list", "1");

        return params;
    }

    /**
     * 构建推荐页参数
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static Map<String, String> genRecommendDetailParams(int pageIndex, int pageSize) {
        Map<String, String> params = genCommonParams();
        params.put("page_num", String.valueOf(pageIndex));
        params.put("page_size", String.valueOf(pageSize));
        return params;
    }

    /**
     * 获取设备的唯一标识符:IMEI+ANDROID_ID+MAC+SERIAL
     */
    private static String getDeviceID() {
        if (!TextUtils.isEmpty(mDeviceID)) {
            return mDeviceID;
        }

        String cacheValue = SPUtils.getInstance().getString(SP_DEVICE_ID);
        if (!TextUtils.isEmpty(cacheValue)) {
            mDeviceID = cacheValue;
            return mDeviceID;
        }

        String IMEI = DeviceUtils.getIMEI();
        String ANDROID_ID = DeviceUtils.getAndroidID();
        String MAC = DeviceUtils.getMacAddress();
        String SERIAL = DeviceUtils.getHardwareSerial();
        if (IMEI == null) {
            IMEI = "";
        }
        if (MAC == null) {
            MAC = "";
        }
        if (ANDROID_ID == null) {
            ANDROID_ID = "";
        }
        if (SERIAL == null) {
            SERIAL = "";
        }
        mDeviceID = IMEI + MAC + ANDROID_ID + SERIAL;
        mDeviceID = EncryptUtils.encryptWithSHA1(mDeviceID);
        Timber.i(mDeviceID);
        if (!TextUtils.isEmpty(mDeviceID)) {
            SPUtils.getInstance().put(SP_DEVICE_ID, mDeviceID);
        }
        return mDeviceID;
    }

    /**
     * 获取屏幕分辨率
     */
    private static String getResolution() {
        if (!TextUtils.isEmpty(mResolution)) {
            return mResolution;
        }
        mResolution = ScreenUtils.getScreenWidth() + "*" + ScreenUtils.getScreenHeight();
        return mResolution;
    }

    /**
     * 获取部分硬件信息
     */
    private static String getHardwareInfo() {
        if (!TextUtils.isEmpty(mHardwareInfo)) {
            return mHardwareInfo;
        }

        try {
            JSONObject o = new JSONObject();
            o.put("cpu", 0);
            o.put("gpu", "");
            o.put("mem", getAvailableMemoryString());
            mHardwareInfo = EncodeUtils.urlDecode(o.toString());
            return mHardwareInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取可用内存的字符串表示，保留小数点后一位
     */
    private static String getAvailableMemoryString() {
        String result = String.valueOf((1f * AppUtils.getAvailableMemorySize()) / (1L << 20));
        int index = result.indexOf(".");

        //整数部分+小数点
        String wholePart = result.substring(0, index + 1);

        //小数部分
        String decimalPart = result.substring(index + 1);
        if (decimalPart.length() > 2) {
            decimalPart = decimalPart.substring(0, 2);
        }
        return wholePart + decimalPart + "MB";
    }

    /**
     * network is HSPA+
     * 因该参数为android api13 新增的参数，固此处使用常量表示
     */
    public static final int NETWORK_TYPE_HSPAP = 15;

    /**
     * 获取网络类型
     */
    public static String getNetWorkType() {
        String networkType = "";
        NetworkInfo netWorkInfo = NetworkUtils.getActiveNetWorkInfo();
        if (netWorkInfo != null) {
            if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "1";
            } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager telephonyManager =
                        (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        networkType = "2";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        networkType = "3";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        networkType = "4";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        networkType = "5";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        networkType = "6";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        networkType = "7";
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        networkType = "8";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        networkType = "9";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        networkType = "10";
                        break;
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        networkType = "11";
                        break;
                    case NETWORK_TYPE_HSPAP:
                        networkType = "12";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        networkType = "14";
                        break;
                    default:
                        networkType = "-1";
                }
            }
        }
        return networkType;
    }

}
