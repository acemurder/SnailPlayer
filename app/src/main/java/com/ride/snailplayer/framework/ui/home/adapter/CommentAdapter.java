package com.ride.snailplayer.framework.ui.home.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.ride.snailplayer.databinding.ItemCommentBinding;
import com.ride.snailplayer.framework.base.adapter.databinding.DataBoundAdapter;
import com.ride.snailplayer.framework.base.adapter.databinding.DataBoundViewHolder;
import com.ride.snailplayer.framework.base.model.Comment;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2017/6/18
 * Created for : snailplayer.
 * Enjoy it !!!
 */

public class CommentAdapter extends DataBoundAdapter<ItemCommentBinding> {
    private Context context;
    private List<Comment> commentList;
    /**
     * Creates a DataBoundAdapter with the given item layout
     *
     * @param layoutId The layout to be used for items. It must use data binding.
     */
    public CommentAdapter(@LayoutRes int layoutId,List<Comment> commentList, Context context) {
        super(layoutId);
        this.commentList = commentList;
        this.context = context;
    }


    @Override
    protected void bindItem(DataBoundViewHolder<ItemCommentBinding> holder, int position, List<Object> payloads) {
        holder.binding.commentContent.setText(commentList.get(position).getContent());
        holder.binding.commentTime.setText(commentList.get(position).getUpdatedAt());
        holder.binding.userName.setText(commentList.get(position).getUserName());
        if (!TextUtils.isEmpty(commentList.get(position).getUserAvatar())){
            Glide.with(context).load(commentList.get(position).getUserAvatar()).dontAnimate()
                    .into(holder.binding.userAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
