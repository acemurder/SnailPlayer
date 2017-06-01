package com.ride.snailplayer.framework;

import android.app.Application;

import com.qiyi.video.playcore.QiyiVideoView;
import com.ride.snailplayer.common.config.SnailPlayerConfig;
import com.ride.util.common.AppUtils;
import com.ride.util.common.Utils;
import com.ride.util.log.Timber;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Stormouble
 * @since 2017/5/18.
 */

public class SnailPlayerApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        QiyiVideoView.init(this);
        Utils.init(this);
        initLog();
        initUmeng();
    }

    private void initLog() {
        if (AppUtils.isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initUmeng() {
        MobclickAgent.UMAnalyticsConfig config =
                new MobclickAgent.UMAnalyticsConfig(this, SnailPlayerConfig.UMENG_KEY, "wangdoujia");
        MobclickAgent.startWithConfigure(config);
    }

}
