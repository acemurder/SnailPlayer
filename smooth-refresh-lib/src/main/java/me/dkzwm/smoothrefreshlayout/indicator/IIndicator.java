package me.dkzwm.smoothrefreshlayout.indicator;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dkzwm on 2017/5/22.
 *
 * @author dkzwm
 */
public interface IIndicator {
    float DEFAULT_RATIO_OF_REFRESH_VIEW_HEIGHT_TO_REFRESH = 1.1f;
    float DEFAULT_CAN_MOVE_THE_MAX_RATIO_OF_REFRESH_VIEW_HEIGHT = 0f;
    float DEFAULT_RESISTANCE = 1.65f;
    int DEFAULT_START_POS = 0;
    int MOVING_CONTENT = 0;
    int MOVING_FOOTER = 1;
    int MOVING_HEADER = 2;

    @MovingStatus
    int getMovingStatus();

    void setMovingStatus(@MovingStatus int direction);

    int getCurrentPosY();

    boolean hasTouched();

    float getResistanceOfPullDown();

    void setResistanceOfPullDown(float resistance);

    float getResistanceOfPull();

    void setResistanceOfPullUp(float resistance);

    void setResistance(float resistance);

    void onFingerUp();

    void onRefreshComplete();

    boolean crossCompletePos();

    void setRatioOfRefreshViewHeightToRefresh(float ratio);

    float getRatioOfHeaderHeightToRefresh();

    void setRatioOfHeaderHeightToRefresh(float ratio);

    float getRatioOfFooterHeightToRefresh();

    void setRatioOfFooterHeightToRefresh(float ratio);

    int getOffsetToRefresh();

    void setOffsetToRefresh(int offset);

    int getOffsetToLoadMore();

    void onFingerDown(float x, float y);

    void onFingerMove(float x, float y);

    float getOffsetY();

    int getLastPosY();

    void setCurrentPos(int current);

    int getHeaderHeight();

    void setHeaderHeight(int height);

    int getFooterHeight();

    void setFooterHeight(int height);

    void convert(IIndicator indicator);

    boolean hasLeftStartPosition();

    boolean hasJustLeftStartPosition();

    boolean hasJustBackToStartPosition();

    boolean isOverOffsetToRefresh();

    boolean hasMovedAfterPressedDown();

    boolean isInStartPosition();

    boolean crossRefreshLineFromTopToBottom();

    boolean crossRefreshLineFromBottomToTop();

    boolean hasJustReachedHeaderHeightFromTopToBottom();

    boolean hasJustReachedFooterHeightFromBottomToTop();

    boolean isOverOffsetToKeepRefreshViewWhileLoading();

    int getOffsetToKeepRefreshViewWhileLoading();

    void setOffsetToKeepHeaderWhileLoading(int offset);

    void setOffsetToKeepFooterWhileLoading(int offset);

    boolean isAlreadyHere(int to);

    boolean willOverTop(int to);

    void setCanMoveTheMaxRatioOfRefreshHeight(float ratio);

    float getCanMoveTheMaxRatioOfHeaderHeight();

    void setCanMoveTheMaxRatioOfHeaderHeight(float ratio);

    float getCanMoveTheMaxRatioOfFooterHeight();

    void setCanMoveTheMaxRatioOfFooterHeight(float ratio);

    float getCanMoveTheMaxDistanceOfHeader();

    float getCanMoveTheMaxDistanceOfFooter();

    @NonNull
    float[] getFingerDownPoint();

    @NonNull
    float[] getLastMovePoint();

    boolean isJustReachedToKeepRefreshWhileLoading();

    float getLastPercentOfHeader();

    float getCurrentPercentOfHeader();

    float getLastPercentOfFooter();

    float getCurrentPercentOfFooter();

    @IntDef({MOVING_CONTENT, MOVING_FOOTER, MOVING_HEADER})
    @Retention(RetentionPolicy.SOURCE)
    @interface MovingStatus {
    }
}
