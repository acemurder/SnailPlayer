package com.ride.snailplayer.framework.ui.me;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityMeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.event.UserUpdateEvent;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.util.common.AppExecutors;
import com.ride.util.common.log.Timber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        mHandler = new Handler();
        mUser = BmobUser.getCurrentUser(User.class);
        EventBus.getDefault().register(this);

        setupToolbar();
        setupBasicInfo();
    }

    private void setupBasicInfo() {
        if (mUser != null) {
            updateUser(mUser.getAvatarUrl());
            mBinding.tvMeName.setText(mUser.getNickName());
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
                    BmobUser.logOut();
                    HomeActivity.launchActivity(MeActivity.this, true);
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
        updateUserAvatar(avatarUrl);

        User currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser != null) {
            User newUser = new User();
            newUser.setAvatarUrl(avatarUrl);
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

    private void updateUserAvatar(String url) {
        if (!TextUtils.isEmpty(url)) {
            Observable.just(url)
                    .compose(MainThreadObservableTransformer.instance())
                    .map(s -> {
                        OkHttpClient client = ApiClient.IQIYI.getOkHttpClient();
                        Request request = new Request.Builder().url(s).build();
                        return client.newCall(request);
                    })
                    .subscribe(call -> call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Timber.i("下载bitmap失败");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful() && response.body() != null) {
                                Timber.i("下载bitmap成功");
                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                AppExecutors.getInstance().getMainThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.circleIvMeAvatar.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }
                    }));
        }
    }

    @Override
    protected void clear() {
        super.clear();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
