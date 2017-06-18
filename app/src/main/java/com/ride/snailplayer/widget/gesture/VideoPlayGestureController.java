package com.ride.snailplayer.widget.gesture;

import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ride.snailplayer.util.VideoUtil;
import com.ride.snailplayer.widget.VideoPlayView;

import static com.ride.snailplayer.widget.gesture.GestureType.FastBackwardOrForward;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/13
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class VideoPlayGestureController {

    private static final int INVALID_DRAG_PROGRESS = -1;


    private GestureDetector mGestureDetector;
    private Context mContext;
    private GestureType mGestureType;
    private AdjustPanel mAdjustPanel;
    private ProgressAdjustPanel mProgressAdjustPanel;
    private VideoPlayView mPlayView;

    private float mCurrentBrightness = 0;
    private int mCurrentVolume = 0;
    private boolean mIsBrightnessUsed = false;
    private float mStartDragX = 0;
    private int mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
    private int mDragProgressPosition = 0;
    private FrameLayout mAdjustPanelContainer;
    private FrameLayout mProgressAdjustPanelContainer;

    private boolean isVisible = true;


    public VideoPlayGestureController(Context context, FrameLayout viewContainer, FrameLayout viewProgressContainer,
                                      VideoPlayView mPlayView) {
        mContext = context;
        this.mPlayView = mPlayView;
        setAdjustPanelContainer(viewContainer);
        setProgressAdjustPanelContainer(viewProgressContainer);
        initGestureDetector();
        mCurrentBrightness = VideoUtil.getSystemBrightnessPercent(context);
        mPlayView.setVisibilityGone();

    }

    public void handleTouchEvent(MotionEvent event) {


        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mStartDragX = event.getRawX();
            updateCurrentInfo();
        }
        mGestureDetector.onTouchEvent(event);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mPlayView.postDelayed(mDoubleTapRunnable,1500);
            if (mGestureType == FastBackwardOrForward) {
                //   mGestureCallBack.onEndDragProgress(mDragProgressPosition, event.getRawX() - mStartDragX);
                mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
                mDragProgressPosition = 0;
                mStartDragX = 0;
            }
            reset();

        }

    }

    private void reset() {
        mGestureType = GestureType.None;
        mProgressAdjustPanel.hidePanel();
        mAdjustPanel.hidePanel();
    }


    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        mPlayView.postDelayed(mShowRunable, 200);
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        mPlayView.removeCallbacks(mDoubleTapRunnable);
                        // mGestureCallBack.onDoubleTap();
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {

                        if (e1 == null || e2 == null) {
                            return true;
                        }

                        if (mGestureType == GestureType.None) {
                            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                mGestureType = GestureType.FastBackwardOrForward;
                            } else {
                                if (e1.getX() < mPlayView.getMeasuredWidth() / 2) {
                                    mGestureType = GestureType.Brightness;
                                } else {
                                    mGestureType = GestureType.Volume;
                                }
                            }
                        }

                        distanceX = e2.getX() - e1.getX();
                        distanceY = e2.getY() - e1.getY();
                        return adjustInternal(e1, e2, distanceX, distanceY);
                    }
                });
    }


    public void setAdjustPanelContainer(FrameLayout layout) {
        mAdjustPanelContainer = layout;

        mAdjustPanel = new AdjustPanel(mContext);
        mAdjustPanelContainer.setVisibility(View.GONE);
        mAdjustPanelContainer.removeAllViews();
        mAdjustPanelContainer.addView(mAdjustPanel, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setProgressAdjustPanelContainer(FrameLayout layout) {
        mProgressAdjustPanelContainer = layout;
        mProgressAdjustPanel = new ProgressAdjustPanel(mContext);
        mProgressAdjustPanel.setVisibility(View.GONE);
        mProgressAdjustPanel.removeAllViews();
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressAdjustPanelContainer.addView(mProgressAdjustPanel, params);
    }


//    public void prepareForPlay() {
//        mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
//        mDragProgressPosition = 0;
//        mStartDragX = 0;
//    }

    private Runnable mDoubleTapRunnable = new Runnable() {
        @Override
        public void run() {
            //  mGestureCallBack.onSingleTap();
            mPlayView.setVisibilityGone();

        }
    };

    private Runnable mShowRunable = new Runnable() {
        @Override
        public void run() {
            mPlayView.setVisibilityVisible();
        }
    };


    private void updateCurrentInfo() {
        AudioManager manager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        mCurrentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mIsBrightnessUsed) {
            mCurrentBrightness = VideoUtil.getBrightnessPercent(mContext);
        } else {
            mCurrentBrightness = VideoUtil.getSystemBrightnessPercent(mContext);
        }
    }


    private boolean adjustInternal(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mGestureType == FastBackwardOrForward) {
            // Adjust Progress.
            return adjustProgress(distanceX);
        } else if (mGestureType == GestureType.Brightness) {
            // Adjust Brightness.
            return adjustBrightness(distanceY);
        } else if (mGestureType == GestureType.Volume) {
            // Adjust Volume.
            return adjustVolume(distanceY);
        }

        return true;
    }

    private boolean adjustVolume(float distanceY) {
        if (mAdjustPanel == null) {
            return true;
        }

        distanceY *= -1;
        float percent = distanceY / (float) mPlayView.getMeasuredHeight();

        AudioManager manager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumeOffsetAccurate = maxVolume * percent * 1.2f;
        int volumeOffset = (int) volumeOffsetAccurate;

        if (volumeOffset == 0 && Math.abs(volumeOffsetAccurate) > 0.2f) {
            if (distanceY > 0) {
                volumeOffset = 1;
            } else if (distanceY < 0) {
                volumeOffset = -1;
            }
        }

        int currVolume = mCurrentVolume + volumeOffset;
        if (currVolume < 0) {
            currVolume = 0;
        } else if (currVolume >= maxVolume) {
            currVolume = maxVolume;
        }

        manager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume, 0);

        float volumePercent = (float) currVolume / (float) maxVolume;
        mAdjustPanel.adjustVolume(volumePercent);

        return true;
    }

    private boolean adjustBrightness(float distanceY) {
        if (mAdjustPanel == null) {
            return true;
        }

        distanceY *= -1;
        float percent = distanceY / (float) mPlayView.getMeasuredHeight();
        float brightnessOffset = percent * 1.2f;
        float brightness = mCurrentBrightness + brightnessOffset;

        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 1) {
            brightness = 1;
        }

        VideoUtil.setBrightness(mContext, brightness);
        mAdjustPanel.adjustBrightness(brightness);
        mIsBrightnessUsed = true;

        return true;
    }

    private boolean adjustProgress(float distanceX) {
        // mGestureCallBack.onStartDragProgress();
        if (mStartDragProgressPosition == INVALID_DRAG_PROGRESS) {
            mStartDragProgressPosition = mPlayView.getCurrentPostion();
        }

//        mGestureCallBack.onDragProgress(mStartDragProgressPosition, distanceX);
        handleDragProgress(mStartDragProgressPosition, distanceX);
        return true;
    }

    protected void handleDragProgress(int startDragPos, float distanceX) {

        float percent = distanceX / (float) mPlayView.getMeasuredWidth();
        int total = mPlayView.getPostion();
        int currPosition = startDragPos;
        int seekOffset = (int) (total * percent);

        currPosition += seekOffset;
        if (currPosition < 0) {
            currPosition = 0;
        } else if (currPosition > total) {
            currPosition = total;
        }

        showAdjustProgress(seekOffset > 0, currPosition, total);
    }


    public void showAdjustProgress(boolean forward, int currPosition, int total) {
        mDragProgressPosition = currPosition;

        if (forward) {
            mProgressAdjustPanel.adjustForward(currPosition, total);
        } else {
            mProgressAdjustPanel.adjustBackward(currPosition, total);
        }
    }

}
