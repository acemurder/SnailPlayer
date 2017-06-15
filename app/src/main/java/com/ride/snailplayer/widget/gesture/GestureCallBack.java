package com.ride.snailplayer.widget.gesture;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/13
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public interface GestureCallBack {
    void onSingleTap();

    void onDoubleTap();

    void onStartDragProgress();

    void onDragProgress(int startDragPos, float distanceX);

    void onEndDragProgress(int dragPosition, float totalDistanceX);

    boolean onCanHandleGesture();

    int getCurrentPosition();
}
