package com.ride.snailplayer.framework.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stormouble
 * @since 2016/10/24.
 */

public abstract class BasePagerAdapter<T> extends PagerAdapter {

    private static final int TAG_CHANGED = 9;

    protected Context mContext;

    protected List<T> mDataList;

    public BasePagerAdapter(@NonNull Context context,
                            @NonNull List<T> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        if (mDataList != null && mDataList.isEmpty()) {
            return POSITION_NONE;
        }
        if (object instanceof View) {
            View view = (View) object;
            if (view.getTag() instanceof Integer && ((Integer) view.getTag()).compareTo(TAG_CHANGED) == 0) {
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }
        return super.getItemPosition(object);
    }

    public List<T> getData() {
        return mDataList;
    }

}
