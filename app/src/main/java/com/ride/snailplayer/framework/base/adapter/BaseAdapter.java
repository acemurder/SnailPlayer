package com.ride.snailplayer.framework.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stormouble
 * @since 2016/10/24.
 */

public abstract class BaseAdapter<T>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;

    protected LayoutInflater mInflater;

    protected List<T> mData;

    @Nullable
    protected OnItemClickListener mItemListener;

    protected RecyclerView mRecyclerView;

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, View view, int position);
    }

    public BaseAdapter(@NonNull Context context, List<T> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(mContext);

        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
        if (mItemListener != null && mRecyclerView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onItemClick(mRecyclerView, v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemListener = listener;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> newDataList) {
        if (newDataList == null) {
            newDataList = new ArrayList<>();
        }
        mData = newDataList;
        notifyDataSetChanged();
    }

    public void addItem(T t) {
       if (t == null) {
           return;
       }

        mData.add(t);
        notifyItemInserted(mData.size() - 1);
    }

    public void addItem(T t, int position) {
        if (t == null) {
            return;
        }

        mData.add(position, t);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position + 1, getItemCount() - position - 1);
    }

}
