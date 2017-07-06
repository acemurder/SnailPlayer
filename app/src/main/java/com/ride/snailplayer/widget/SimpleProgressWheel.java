package com.ride.snailplayer.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.ride.snailplayer.R;

import me.dkzwm.smoothrefreshlayout.view.ProgressWheel;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public class SimpleProgressWheel extends ProgressWheel {

    private int mBarWidth;
    private int mBarColor;
    private int mRadius;

    public SimpleProgressWheel(Context context) {
        this(context, null);
    }

    public SimpleProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources resources = getResources();
        mBarWidth = resources.getDimensionPixelSize(R.dimen.default_pw_bar_width);
        mBarColor = ContextCompat.getColor(context, R.color.default_pw_bar_color);
        mRadius = resources.getDimensionPixelSize(R.dimen.default_pw_radius);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleProgressWheel);
        mBarWidth = a.getDimensionPixelSize(R.styleable.SimpleProgressWheel_pwBarWidth, mBarWidth);
        mBarColor = a.getColor(R.styleable.SimpleProgressWheel_pwBarColor, mBarColor);
        mRadius = a.getDimensionPixelSize(R.styleable.SimpleProgressWheel_pwRadius, mRadius);
        a.recycle();

        setBarWidth(mBarWidth);
        setBarColor(mBarColor);
        setCircleRadius(mRadius);

        spin();
    }

    @Override
    public void spin() {
        setVisibility(VISIBLE);
        super.spin();
    }

    @Override
    public void stopSpinning() {
        setVisibility(INVISIBLE);
        super.stopSpinning();
    }
}
