package me.dkzwm.smoothrefreshlayout.extra.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;

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
        super(context);
    }

    public LottieHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LottieHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(0);
            if (child instanceof LottieAnimationView) {
                mLottieAnimationView = (LottieAnimationView) child;
            }
        }
        super.onFinishInflate();
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
