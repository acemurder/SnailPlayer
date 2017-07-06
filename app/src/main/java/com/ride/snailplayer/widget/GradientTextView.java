package com.ride.snailplayer.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ride.snailplayer.R;


/**
 * @author Stormouble
 * @since 2017/6/9.
 */

public class GradientTextView extends View {

    private Paint mSourceTextPaint;
    private Paint mTargetTextPaint;

    private String mText;
    private int mTextSize;
    private int mSourceTextColor;
    private int mTargetTextColor;
    private float mAlphaRatio;

    private Rect mTextRect;

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources resources = getResources();
        mTextSize = resources.getDimensionPixelSize(R.dimen.default_giv_text_size);
        mSourceTextColor = ContextCompat.getColor(context, R.color.default_giv_source_text_color);
        mTargetTextColor = ContextCompat.getColor(context, R.color.default_giv_target_text_color);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView);
        mText = (String) a.getText(R.styleable.GradientTextView_gtv_text);
        mTextSize = a.getDimensionPixelOffset(R.styleable.GradientTextView_gtv_text_size, mTextSize);
        mSourceTextColor = a.getColor(R.styleable.GradientTextView_gtv_source_text_color, mSourceTextColor);
        mTargetTextColor = a.getColor(R.styleable.GradientTextView_gtv_target_text_color, mTargetTextColor);
        mAlphaRatio = a.getFloat(R.styleable.GradientTextView_gtv_alpha_ratio, 0f);
        if (mAlphaRatio < 0 || mAlphaRatio > 1) {
            mAlphaRatio = 1f;
        }
        a.recycle();

        init();
    }

    private void init() {
        mSourceTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSourceTextPaint.setTextSize(mTextSize);
        mSourceTextPaint.setColor(mSourceTextColor);

        mTargetTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetTextPaint.setTextSize(mTextSize);
        mTargetTextPaint.setColor(mTargetTextColor);

        mTextRect = new Rect();
        if (!TextUtils.isEmpty(mText)) {
            mSourceTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumWidth = getMinimumSize(mTextRect.width(), getSuggestedMinimumWidth());
        int minimumHeight = getMinimumSize(mTextRect.height(), getSuggestedMinimumHeight());
        setMeasuredDimension(resolveDimension(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), minimumWidth),
                resolveDimension(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), minimumHeight));
    }

    private int resolveDimension(int measureSpec, int padding, int minimumSize) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = minimumSize + padding;
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(specSize, result);
            }
        }
        return result;
    }

    private int getMinimumSize(int defaultSize, int suggestedMinimumSize) {
        return Math.max(defaultSize, suggestedMinimumSize);
    }

    public void setText(@NonNull String text) {
        mText = text;
        mSourceTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        invalidate();
    }

    public void setTextSize(@IntRange(from = 1, to = Integer.MAX_VALUE) int textSize) {
        mTextSize = textSize;
        mSourceTextPaint.setTextSize(mTextSize);
        mTargetTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setSourceTextColor(int sourceTextColor) {
        mSourceTextColor = sourceTextColor;
        mSourceTextPaint.setColor(mSourceTextColor);
        invalidate();
    }

    public void setTargetTextColor(int targetTextColor) {
        mTargetTextColor = targetTextColor;
        mTargetTextPaint.setColor(mTargetTextColor);
        invalidate();
    }

    public void setAlphaRatio(@FloatRange(from = 0.0, to = 1.0) float alphaRatio) {
        mAlphaRatio = alphaRatio;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int x = getPaddingLeft() + (width / 2 - mTextRect.width() / 2);
        int y = getPaddingTop() + (int) (height / 2 - (mSourceTextPaint.ascent() + mSourceTextPaint.descent()) / 2);

        mSourceTextPaint.setAlpha((int) (255 - 255 * mAlphaRatio));
        canvas.drawText(mText, x, y, mSourceTextPaint);

        mTargetTextPaint.setAlpha((int) (255 * mAlphaRatio));
        canvas.drawText(mText, x, y, mTargetTextPaint);
    }

}
