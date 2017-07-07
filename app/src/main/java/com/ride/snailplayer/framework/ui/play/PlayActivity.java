package com.ride.snailplayer.framework.ui.play;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityPlayBinding;
import com.ride.snailplayer.framework.base.model.Comment;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.home.adapter.CommentAdapter;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.model.VideoInfo;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.KeyboardUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class PlayActivity extends AppCompatActivity {

    private static final String TABLE_NAME = "Comment";
    private ActivityPlayBinding mBinding;
    private VideoInfo videoInfo;
    private int currentOrientation = 1;
    private CommentAdapter mCommentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private UserViewModel model;
    private User user;



    public static void launchActivity(Activity activity, VideoInfo videoInfo) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("video",videoInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        model = ViewModelProviders.of(this).get(UserViewModel.class);
        user = model.getUser();
        mBinding.videoPlayView.setPlayData(videoInfo.tId);
        loadData();
    }

    private void initView() {
        videoInfo = (VideoInfo) getIntent().getSerializableExtra("video");

        mBinding.tvVideoTitle.setText(videoInfo.title);
        mBinding.tvPlayCount.setText(videoInfo.playCountText+"次播放");
        if (!TextUtils.isEmpty(videoInfo.snsScore))
            mBinding.tvVideoScore.setText(videoInfo.snsScore+"分");

        mCommentAdapter = new CommentAdapter(R.layout.item_comment,comments,this);
        mBinding.commentList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.commentList.setAdapter(mCommentAdapter);
        mBinding.videoPlayView.setListener(this::onBackPressed);
        mBinding.videoPlayView.setTitle(videoInfo.title);


        mBinding.commentSend.setOnClickListener((v -> {
            if (!TextUtils.isEmpty(mBinding.commentEdit.getText())){
                String content = mBinding.commentEdit.getText().toString();
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setTvId(videoInfo.tId);
                //comment.setUserAvatar("");
                String userName = "匿名用户";
                if (user != null)
                    userName = user.getNickName();

                comment.setUserName(userName);
                mBinding.commentEdit.setText("");
                KeyboardUtils.hideSoftInput(PlayActivity.this);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            Timber.i(s);
                             loadData();
                            //TODO
                        }else {
                            Toast.makeText(PlayActivity.this,"网络错误",Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        }));
    }

    private void loadData(){
        BmobQuery query =new BmobQuery(TABLE_NAME);
        query.addWhereEqualTo("tvId", videoInfo.tId);
        query.order("createdAt");
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (jsonArray != null) {
                    Timber.e(jsonArray.toString());
                    Gson gson = new Gson();
                    List<Comment> commentList = gson.fromJson(jsonArray.toString(),
                            new TypeToken<List<Comment>>() {
                            }.getType());
                    if (commentList != null && commentList.size() != 0) {
                        mBinding.commentNotice.setVisibility(View.GONE);
                    } else {
                        mBinding.commentNotice.setVisibility(View.VISIBLE);
                    }
                    comments.clear();
                    comments.addAll(commentList);
                    mCommentAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.commentLayout.setVisibility(View.GONE);
            currentOrientation = Configuration.ORIENTATION_LANDSCAPE;
            mBinding.videoPlayView.onScreenSizeChanged(screenWidth, screenHeight, true);
        }else{
            mBinding.commentLayout.setVisibility(View.VISIBLE);
            currentOrientation = ORIENTATION_PORTRAIT;
            mBinding.videoPlayView.onScreenSizeChanged(screenWidth, (int) (screenWidth * 9.0 / 16));
        }
    }

    @Override
    public void onBackPressed() {
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.videoPlayView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.videoPlayView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.videoPlayView.release();
    }
}
