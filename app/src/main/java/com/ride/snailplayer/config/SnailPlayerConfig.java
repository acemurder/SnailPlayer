package com.ride.snailplayer.config;

/**
 * @author Stormouble
 * @since 2017/5/19.
 */

public class SnailPlayerConfig {

    /**
     * Host地址
     */
    public static final String HOST = "http://iface.qiyi.com/";
    public static final String API_REALTIME_HOST = HOST + "openapi/realtime/";

    /**
     * OkHttp超时配置
     */
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT    = 30;
    public static final int WRITE_TIMEOUT   = 30;


    /**
     * 渠道Key，Open API必填参数
     */
    public static final String APP_KEY = "69842642483add0a63503306d63f0443";

    /**
     * Umeng的AppKey
     */
    public static final String UMENG_KEY = "591e8b1499f0c7250c000d95";


    /**
     * Bmob云后台Application ID
     */
    public static final String BMOB_APPLICATION_ID = "97d1aa449df415134bf56e4f8e522a17";

    /**
     * Bmob云后台REST API Key
     */
    public static final String BMOB_RESTAPI_KEY = "47375fdfd13f09f586a3926b58131a3a";

    /**
     * Bmob SMS的短信模版名称
     */
    public static final String BMOB_SMS_TEMPLATE = "骑着蜗牛短信模版";

    /**
     * 客户端版本
     */
    public static final String APP_V = "8.2";

    /**
     * 客户端类型,固定为'0'
     */
    public static final String APP_T = "0";

    /**
     * 平台码，固定为'10'
     */
    public static final String PLATFORM_ID = "10";

    /**
     * 数据获取成功
     */
    public static final int API_CODE_SUCCESS = 100000;

    public static final int API_CODE_NO_DATA = 100002;


}
