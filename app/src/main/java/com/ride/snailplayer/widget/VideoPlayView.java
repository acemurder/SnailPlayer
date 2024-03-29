package com.ride.snailplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;
import com.qiyi.video.playcore.QiyiVideoView;
import com.ride.snailplayer.R;
import com.ride.snailplayer.widget.gesture.VideoPlayGestureController;
import com.ride.util.common.log.Timber;

import java.util.concurrent.TimeUnit;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/3
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class VideoPlayView extends RelativeLayout {
    private QiyiVideoView mVideoView;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeText;
    private Context mContext;
    private ImageView mPlayImage;
    private ImageView mFullScreenImage;
    private LinearLayout mControlLayout;
    private static final int PERMISSION_REQUEST_CODE = 7171;
    private static final int HANDLER_MSG_UPDATE_PROGRESS = 1;
    private static final int HANDLER_DEPLAY_UPDATE_PROGRESS = 1000; // 1s
    private VideoPlayGestureController mGestureController;
    private FrameLayout viewContainer;
    private FrameLayout viewProgressContainer;
    private TextView mTitleText;
    private ImageView mBackImage;
    private LinearLayout mRemindLayout;
    private  LottieAnimationView lottieAnimationView;

    private OnBackClickListener listener;


    IQYPlayerHandlerCallBack mCallBack = new IQYPlayerHandlerCallBack() {
        /**
         * SeekTo 成功，可以通过该回调获取当前准确时间点。
         */
        @Override
        public void OnSeekSuccess(long l) {
            Timber.i("SeekTo 成功");
        }

        /**
         * 是否因数据加载而暂停播放
         */
        @Override
        public void OnWaiting(boolean b) {
//            Observable.create((ObservableOnSubscribe<Object>) e -> {
//                if (b) {
//                    lottieAnimationView.setVisibility(VISIBLE);
//                    lottieAnimationView.playAnimation();
//
//                }else {
//                    lottieAnimationView.setVisibility(GONE);
//                }
//                e.onNext(1);
//
//            }).subscribeOn(AndroidSchedulers.mainThread())
//                    .unsubscribeOn(AndroidSchedulers.mainThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
            if (b){
                ((Activity)getContext()).runOnUiThread(()->{
                    lottieAnimationView.setVisibility(VISIBLE);
                });
                Timber.i("数据加载暂停");

            }

            else{
                Timber.i("数据加载暂停false");
           //     lottieAnimationView.cancelAnimation();
                ((Activity)getContext()).runOnUiThread(()->{
                    lottieAnimationView.setVisibility(GONE);
                });
            }
        }

        /**
         * 播放内核发生错误
         */
        @Override
        public void OnError(ErrorCode errorCode) {
            mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
            Timber.i("错误");
        }

        /**
         * 播放器状态码 {@link com.iqiyi.player.nativemediaplayer.MediaPlayerState}
         * 0	空闲状态
         * 1	已经初始化
         * 2	调用PrepareMovie，但还没有进入播放
         * 4    可以获取视频信息（比如时长等）
         * 8    广告播放中
         * 16   正片播放中
         * 32	一个影片播放结束
         * 64	错误
         * 128	播放结束（没有连播）
         */
        @Override
        public void OnPlayerStateChanged(int i) {
            switch (i) {
                case 0:
                    Timber.i("空闲");
                    break;
                case 1:
                    Timber.i("已经初始化");
                    break;
                case 2:
                    Timber.i("调用PrepareMovie");
                    break;
                case 4:
                    Timber.i("可以获取视频信息");
                    break;
                case 8:
                    Timber.i("广告播放中");
                    break;
                case 16:
                    Timber.i("正片播放中");
                    break;
                case 32:
                    Timber.i("一个影片播放结束");
                    break;
                case 64:
                    Timber.i("错误");
                    break;
                case 128:
                    Timber.i("播放结束");
                    break;


            }
        }
    };


    private Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_UPDATE_PROGRESS:
                    int duration = mVideoView.getDuration();
                    int progress = mVideoView.getCurrentPosition();
                    if (duration > 0) {
                        mSeekBar.setMax(duration);
                        mSeekBar.setProgress(progress);
                        mCurrentTimeText.setText(ms2hms(progress) + "/" + ms2hms(duration));
                    }
                    mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
                    break;
                default:
                    break;
            }
        }
    };


    public VideoPlayView(Context context) {
        super(context);
        this.mContext = context;
        initView();

    }


    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();

    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        initView();

    }

    public void setVisibilityGone() {
        mRemindLayout.setVisibility(GONE);
        mControlLayout.setVisibility(GONE);
    }

    public void setVisibilityVisible() {
        mRemindLayout.setVisibility(VISIBLE);
        mControlLayout.setVisibility(VISIBLE);
    }

    private void initView() {

        LayoutInflater.from(mContext).inflate(R.layout.video_play_view, this);
        mControlLayout = (LinearLayout) findViewById(R.id.video_controller);
        mVideoView = (QiyiVideoView) findViewById(R.id.video_view);
        mVideoView.setPlayerCallBack(mCallBack);
//        mVideoView.setOnClickListener((v -> {
//            if (mControlLayout.getVisibility() == VISIBLE)
//                mControlLayout.setVisibility(INVISIBLE);
//            else
//                mControlLayout.setVisibility(VISIBLE);
//
//        }));

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie_animation_view);
        //lottieAnimationView.cancelAnimation();
        lottieAnimationView.setVisibility(GONE);
        mCurrentTimeText = (TextView) findViewById(R.id.tv_play_time);

        mPlayImage = (ImageView) findViewById(R.id.iv_play);
        mPlayImage.setOnClickListener((v) -> {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mPlayImage.setBackgroundResource(R.drawable.ic_play_arrow);
                mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
            } else {
                mVideoView.start();
                mPlayImage.setBackgroundResource(R.drawable.ic_pause);
                mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);
            }
        });
        mFullScreenImage = (ImageView) findViewById(R.id.iv_full_screen);
        mFullScreenImage.setOnClickListener((v -> setOrientation()));

        mSeekBar = (SeekBar) findViewById(R.id.sb_progress);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // if (fromUser) {
                mProgress = progress;
                removeCallbacks(mGestureController.getDoubleTapRunnable());
                //   }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBar.setProgress(mProgress);
                mVideoView.seekTo(mProgress);
                postDelayed(mGestureController.getDoubleTapRunnable(), 2000);
            }
        });
        viewContainer = (FrameLayout) findViewById(R.id.fl_view_container);
        viewProgressContainer = (FrameLayout) findViewById(R.id.fl_view_container_progress);
        mTitleText = (TextView) findViewById(R.id.tv_title);

        mBackImage = (ImageView) findViewById(R.id.iv_back);
        mBackImage.setOnClickListener((v -> {
            if (listener != null) {
                listener.onClick();
            }
        }));

        mRemindLayout = (LinearLayout) findViewById(R.id.remind_layout);
        initGestureController();


    }

    public void seekTo(int position) {
        mVideoView.seekTo(position);
        mSeekBar.setProgress(position);
    }

    public void setSeekbarPosition(int position) {
        mSeekBar.setProgress(position);
    }

    public void setTitle(String title) {
        mTitleText.setText(title);
    }

    public void setListener(OnBackClickListener listener) {
        this.listener = listener;
    }

    private void initGestureController() {
        mGestureController = new VideoPlayGestureController(getContext(), viewContainer, viewProgressContainer, this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureController.handleTouchEvent(event);
        return true;

    }

    public int getPostion() {
        return mVideoView.getDuration();
    }

    public int getCurrentPostion() {
        return mVideoView.getCurrentPosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = measureDimension(0, widthMeasureSpec);
        int h = w * 9 / 16;
        ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
        params.height = h;
        mVideoView.setLayoutParams(params);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.getMode(heightMeasureSpec)));
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private String ms2hms(int millis) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return result;
    }


    public void setPlayData(String id) {
        mVideoView.setPlayData(id);
    }

    public void start() {
        mVideoView.start();
        mMainHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_PROGRESS, HANDLER_DEPLAY_UPDATE_PROGRESS);

    }

    public void pause() {
        mVideoView.pause();
        mMainHandler.removeMessages(HANDLER_MSG_UPDATE_PROGRESS);
    }

    public void release() {
        mMainHandler.removeCallbacksAndMessages(null);
        mVideoView.release();
        this.mContext = null;

    }

    public void setOrientation() {
        Activity activity = ((Activity) getContext());
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //切换竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFullScreenImage.setBackgroundResource(R.drawable.ic_fullscreen);
        } else {
            //切换横屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mFullScreenImage.setBackgroundResource(R.drawable.ic_fullscreen_exit);

        }
    }

    public void onScreenSizeChanged(int width, int height, boolean fullScreen) {
        mVideoView.setVideoViewSize(width, height, fullScreen);
        mFullScreenImage.setBackgroundResource(R.drawable.ic_fullscreen_exit);
    }

    public void onScreenSizeChanged(int width, int height) {
        mVideoView.setVideoViewSize(width, height);
        mFullScreenImage.setBackgroundResource(R.drawable.ic_fullscreen);
    }

    public QiyiVideoView getmVideoView() {
        return mVideoView;
    }

    public interface OnBackClickListener {
        void onClick();
    }

    //private GestureDetector mGestureDetector = new GestureDetector()


}
