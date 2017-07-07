package com.ride.snailplayer.widget.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ride.snailplayer.R;
import com.ride.snailplayer.util.VideoUtil;
import com.ride.util.common.log.Timber;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/13
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class ProgressAdjustPanel extends LinearLayout {
    private View adjustIconView;
    private TextView currProgress;
    private TextView totalDuration;


    public ProgressAdjustPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_adjust_panel,this,false);
  //        View.inflate(context, R.layout.layout_progress_adjust_panel, null);
//        View.inflate(cont, R.layout.layout_adjust_panel, this);
        addView(v);

        adjustIconView = findViewById(R.id.adjust_icon);
        currProgress = (TextView) findViewById(R.id.curr_progress);
        totalDuration = (TextView) findViewById(R.id.total_duration);

    }

    public void adjustForward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_forward, currProgressMS, totalDurationMS);
    }

    public void adjustBackward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_backward, currProgressMS, totalDurationMS);
    }

    private void adjustInternal(int iconResId, int currProgressMS, int totalDurationMS) {
        setVisibility(VISIBLE);
        ViewGroup parent = (ViewGroup) getParent();
        Timber.i(parent.getVisibility() + "");
        adjustIconView.setBackgroundResource(iconResId);
        currProgress.setText(VideoUtil.formatTime(currProgressMS));
        totalDuration.setText(VideoUtil.formatTime(totalDurationMS));
        parent.setVisibility(VISIBLE);
        Timber.e(getChildCount() + "");


    }

    public void hidePanel() {
        ViewGroup parent = (ViewGroup) getParent();
        parent.setVisibility(View.GONE);
    }
}
