package com.ride.snailplayer.framework.ui.play;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityPlayBinding;
import com.ride.snailplayer.net.model.VideoInfo;

public class PlayActivity extends AppCompatActivity {

    private ActivityPlayBinding mBinding;
    private VideoInfo videoInfo;



    public static void launchActivity(Activity activity, VideoInfo videoInfo) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("video",videoInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        mBinding.videoPlayView.setPlayData(videoInfo.tId);
        mBinding.videoPlayView.pause();
    }

    private void initView() {
        videoInfo = (VideoInfo) getIntent().getSerializableExtra("video");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.videoPlayView.onScreenSizeChanged(screenWidth, screenHeight, true);
        }else{
            mBinding.videoPlayView.onScreenSizeChanged(screenWidth, (int) (screenWidth * 9.0 / 16));
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mBinding.videoPlayView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.videoPlayView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.videoPlayView.release();
    }
}
