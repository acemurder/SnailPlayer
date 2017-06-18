package com.ride.snailplayer.framework.ui.me;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityMeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.login.event.UserLoginEvent;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.event.UserUpdateEvent;
import com.ride.util.common.AppExecutors;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MeActivity extends BaseActivity {

    private ActivityMeBinding mBinding;
    private Handler mHandler;
    private User mUser;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, MeActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_me);
        mBinding.setMeActionHandler(this);

        EventBus.getDefault().register(this);
        mHandler = new Handler();
        mUser = BmobUser.getCurrentUser(User.class);

        setupToolbar();
        setupBasicInfo();
    }

    private void setupBasicInfo() {
        if (mUser != null) {
            setUserAvatar(mUser.getAvatraUrl());
            mBinding.tvMeName.setText(mUser.getUsername());
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> navigateUpOrBack(this, null));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.rl_me_basic_info:
                AvatarActivity.launchActivity(this, mBinding.circleIvMeAvatar,
                        getResources().getString(R.string.transition_avatar));
                break;
            case R.id.tv_me_exit:
                new Handler().post(() -> {
                    User.logOut();
                    EventBus.getDefault().post(new UserLoginEvent());

                    HomeActivity.launchActivity(MeActivity.this);
                    onBackPressed();
                });
                break;
        }
    }

    @Subscribe
    public void onAvatarChange(OnAvatarChangeEvent event) {
        BmobFile bmobFile = new BmobFile(new File(AvatarActivity.AVATAR_FILE_PATH));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Timber.i("上传文件成功:" + bmobFile.getFileUrl());
                    updateUser(bmobFile.getFileUrl());
                } else {
                    Timber.i("上传文件失败：" + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
            }
        });
    }

    private void updateUser(String avatarUrl) {
        setUserAvatar(avatarUrl);

        User currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser != null) {
            User newUser = new User();
            newUser.setAvatraUrl(avatarUrl);
            newUser.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        EventBus.getDefault().post(new UserUpdateEvent());
                        Timber.i("更新用户信息成功");
                    } else {
                        Timber.i("更新用户信息失败:" + e.getMessage());
                    }
                }
            });
        }
    }

    private void setUserAvatar(String url) {
        AppExecutors.getInstance().getDiskIOExecutor().execute(() -> {
            try {
                Bitmap bitmap = Glide.with(MeActivity.this)
                        .load(url)
                        .asBitmap()
                        .centerCrop()
                        .into(ScreenUtils.dp2px(56), ScreenUtils.dp2px(56))
                        .get();

                mHandler.post(() -> mBinding.circleIvMeAvatar.setImageBitmap(bitmap));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
