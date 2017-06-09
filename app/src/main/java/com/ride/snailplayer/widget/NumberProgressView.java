package com.ride.snailplayer.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;


/**
 * @author Stormouble
 * @since 2017/3/16.
 */

public class NumberProgressView extends View {

    private int mProgress = 0;
    private int mMaxProgress = 100;

    private Paint mTextPaint;
    private Paint mReachedAreaPaint;
    private Paint mUnreachedAreaPaint;
    private float mTextSize;
    private int mTextColor;
    private float mTextPadding;
    private float mReachedAreaHeight;
    private float mUnreachedAreaHeight;
    private int mReachedAreaColor;
    private int mUnreachedAreaColor;

    private RectF mReachedAreaRectF;
    private RectF mUnreachedAreaRectF;

    private boolean mWithCircleBar;

    public NumberProgressView(Context context) {
        this(context, null);
    }

    public NumberProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources resources = context.getResources();
        mTextSize = resources.getDimension(R.dimen.default_num_progress_text_size);
        mTextColor = ContextCompat.getColor(context, R.color.default_num_progress_text_color);
        mTextPadding = resources.getDimension(R.dimen.default_num_progress_text_padding);
        mReachedAreaColor = ContextCompat.getColor(context, R.color.default_num_progress_reached_area_color);
        mUnreachedAreaColor = ContextCompat.getColor(context, R.color.default_num_progress_unreached_area_color);
        mReachedAreaHeight = resources.getDimension(R.dimen.default_num_progress_reached_area_height);
        mUnreachedAreaHeight = resources.getDimension(R.dimen.default_num_progress_unreached_area_height);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberProgressView);
        mTextSize = a.getDimension(R.styleable.NumberProgressView_num_progress_text_size, mTextSize);
        mTextColor = a.getColor(R.styleable.NumberProgressView_num_progress_text_color, mTextColor);
        mTextPadding = a.getDimension(R.styleable.NumberProgressView_num_progress_text_padding, mTextPadding);
        mReachedAreaColor = a.getColor(R.styleable.NumberProgressView_num_progress_reached_area_color, mReachedAreaColor);
        mUnreachedAreaColor = a.getColor(R.styleable.NumberProgressView_num_progress_unreached_area_color, mUnreachedAreaColor);
        mReachedAreaHeight = a.getDimension(R.styleable.NumberProgressView_num_progress_reached_area_height, mReachedAreaHeight);
        mUnreachedAreaHeight = a.getDimension(R.styleable.NumberProgressView_num_progress_unreached_area_height, mUnreachedAreaHeight);
        mWithCircleBar = a.getBoolean(R.styleable.NumberProgressView_num_progress_with_circle_bar, mWithCircleBar);
        a.recycle();

        init();
    }

    private void init() {
        //init paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mReachedAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedAreaPaint.setColor(mReachedAreaColor);

        mUnreachedAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedAreaPaint.setColor(mUnreachedAreaColor);

        //init rect
        mReachedAreaRectF = new RectF(0, 0, 0, 0);
        mUnreachedAreaRectF = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumWidth = getMinimumSize(900, getSuggestedMinimumWidth());
        int minimumHeight = getMinimumSize(100, getSuggestedMinimumHeight());
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

    @Override
    protected void onDraw(Canvas canvas) {
        //calculate reached area and unreached area RectF
        calculateProgressRectF();

        //draw progress reached area
        boolean isReachEnd = false;
        if (mReachedAreaRectF.right >= getWidth() - getTextAreaWidth()) {
            isReachEnd = true;
            //adjust right border to avoid overlaying text area
            mReachedAreaRectF.right = getWidth() - getTextAreaWidth();
        }
        canvas.drawRect(mReachedAreaRectF, mReachedAreaPaint);

        //draw progress text
        if (mWithCircleBar) {
        } else {
            float x;
            if (isReachEnd) {
                //when reach end, adjust x to make text fix at the end
                x = getWidth() - getPaddingRight() - getMaxProgressTextWidth() - mTextPadding;
            } else {
                x = mReachedAreaRectF.right + mTextPadding;
            }
            float y = (getHeight() + getPaddingTop() - getPaddingBottom() - (mTextPaint.ascent() + mTextPaint.descent())) / 2;
            canvas.drawText(getCurProgressText(), x, y, mTextPaint);
        }

        //draw progress unreached area
        canvas.drawRect(mUnreachedAreaRectF, mUnreachedAreaPaint);

        nextProgress();
    }

    private void calculateProgressRectF() {
        //calculate reached area RectF
        mReachedAreaRectF.left = getPaddingLeft();
        mReachedAreaRectF.top = (getHeight() + getPaddingTop() - getPaddingBottom() - mReachedAreaHeight) / 2;
        mReachedAreaRectF.right = getCurProgress() * getWidth() - getPaddingRight();
        mReachedAreaRectF .bottom = mReachedAreaRectF.top + mReachedAreaHeight;

        //calculate unreached area RectF
        mUnreachedAreaRectF.left = mReachedAreaRectF.right + getTextAreaWidth();
        mUnreachedAreaRectF.top = (getHeight() + getPaddingTop() - getPaddingBottom() - mUnreachedAreaHeight) / 2;
        mUnreachedAreaRectF.right = getWidth() - getPaddingRight();
        mUnreachedAreaRectF.bottom = mUnreachedAreaRectF.top + mUnreachedAreaHeight;
    }

    private void nextProgress() {
        if (mProgress != mMaxProgress) {
            mProgress++;
            invalidate();
        }
    }

    private float getTextAreaWidth() {
        return getMaxProgressTextWidth() + 2 * mTextPadding;
    }

    private float getMaxProgressTextWidth() {
        return mTextPaint.measureText(getMaxLengthProgressText());
    }

    private String getMaxLengthProgressText() {
        return String.valueOf(mMaxProgress) + "%";
    }

    private String getCurProgressText() {
        return String.valueOf(mProgress) + "%";
    }

    private float getCurProgress() {
        return mProgress / (float) mMaxProgress;
    }

    public void setCurProgress(int progress) {
        if (progress < 0 || progress > mMaxProgress) {
            throw new IllegalArgumentException("progress value can't less than zero or large than the maximum value" + ", maximum value=" + mMaxProgress);
        }
        mProgress = progress;
        invalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        if (maxProgress <= 0) {
            throw new IllegalArgumentException("maximum value can't less than zero");
        }
        mMaxProgress = maxProgress;
        mProgress = 0;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.progress = mProgress;
        savedState.maxProgress = mMaxProgress;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mProgress = savedState.progress;
        mMaxProgress = savedState.maxProgress;
        requestLayout();
    }

    static class SavedState extends BaseSavedState {
        int progress;
        int maxProgress;

        SavedState(Parcelable source) {
            super(source);
        }

        private SavedState(Parcel source) {
            super(source);
            progress = source.readInt();
            maxProgress = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeInt(maxProgress);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
