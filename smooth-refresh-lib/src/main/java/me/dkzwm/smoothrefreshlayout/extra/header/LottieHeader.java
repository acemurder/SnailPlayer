package me.dkzwm.smoothrefreshlayout.extra.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;

import me.dkzwm.smoothrefreshlayout.R;
import me.dkzwm.smoothrefreshlayout.SmoothRefreshLayout;
import me.dkzwm.smoothrefreshlayout.extra.IRefreshView;
import me.dkzwm.smoothrefreshlayout.indicator.IIndicator;

/**
 * @author Stormouble
 * @since 2017/6/12.
 */

public class LottieHeader extends FrameLayout implements IRefreshView {

    private LottieAnimationView mLottieAnimationView;

    public LottieHeader(Context context) {
        this(context, null);
    }

    public LottieHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LottieHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.lottie_header, this);
        mLottieAnimationView =
                (LottieAnimationView) headerView.findViewById(R.id.smooth_refresh_animation_view);
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onFingerUp(SmoothRefreshLayout layout, IIndicator indicator) {
    }

    @Override
    public void onReset(SmoothRefreshLayout layout) {
    }

    @Override
    public void onRefreshPrepare(SmoothRefreshLayout layout) {
    }

    @Override
    public void onRefreshBegin(SmoothRefreshLayout layout, IIndicator indicator) {
    }

    @Override
    public void onRefreshComplete(SmoothRefreshLayout layout) {
    }

    @Override
    public void onRefreshPositionChanged(SmoothRefreshLayout layout, byte status, IIndicator indicator) {
    }
}
