package com.ride.snailplayer.framework;

import android.app.Application;

import com.qiyi.video.playcore.QiyiVideoView;
import com.ride.snailplayer.common.config.SnailPlayerConfig;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.AppUtils;
import com.ride.util.common.util.Utils;


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
        initBomb();
    }

    private void initLog() {
        if (AppUtils.isDebug()) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initBomb() {
        //Bmob.initialize(this, SnailPlayerConfig.BMOB_APPLICATION_ID);
    }

}
