package com.ride.snailplayer.framework;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.qiyi.video.playcore.QiyiVideoView;
import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.AppUtils;
import com.ride.util.common.util.Utils;

import cn.bmob.v3.Bmob;


/**
 * @author Stormouble
 * @since 2017/5/18.
 */

public class SnailPlayerApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        QiyiVideoView.init(this);
        Utils.init(this);
        initLog();
        initBomb();
    }

    private void initLog() {
        if (AppUtils.isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initBomb() {
        Bmob.initialize(this, SnailPlayerConfig.BMOB_APPLICATION_ID);
    }



}
