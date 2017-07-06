package com.ride.snailplayer.framework.ui.me;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityMeBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItem;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentPagerItems;
import com.ride.snailplayer.framework.base.adapter.viewpager.v4.FragmentStatePagerItemAdapter;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.event.UserUpdateEvent;
import com.ride.snailplayer.framework.ui.me.fragment.AboutMeFragement;
import com.ride.snailplayer.framework.ui.me.fragment.AttentionFragment;
import com.ride.snailplayer.framework.ui.me.viewmodel.MeViewModel;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.ApiClient;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.widget.GradientTextView;
import com.ride.util.common.AppExecutors;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.BarUtils;
import com.ride.util.common.util.ScreenUtils;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeActivity extends BaseActivity {

    private ActivityMeBinding mBinding;
    private FragmentPagerItems mItems;
    private FragmentStatePagerItemAdapter mAdapter;
    private MeViewModel mMeViewModel;
    private UserViewModel mUserViewModel;
    private User mUser;

    private boolean mIsTabClicked;

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
        mMeViewModel = ViewModelProviders.of(this).get(MeViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUser = mUserViewModel.getUser();
        EventBus.getDefault().register(this);

        initAppBar();
        initTabLayout();
    }

    private void initAppBar() {
        //设置Title渐变效果
        mBinding.appbarMe.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.action_bar_size);
            int collapsingLayoutMaxOffset = getResources().getDimensionPixelSize(R.dimen.me_header_height)
                    - actionBarHeight - BarUtils.getStatusBarHeight(MeActivity.this);
            int titleAutoTransparentTotalOffset = 2 * actionBarHeight;
            int titleAutoTransparentMinOffset = collapsingLayoutMaxOffset - titleAutoTransparentTotalOffset;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= titleAutoTransparentMinOffset) {
                    float fraction = (Math.abs(verticalOffset) - titleAutoTransparentMinOffset) / (float) (titleAutoTransparentTotalOffset);
                    mBinding.tvMeTitle.setAlpha(fraction);
                } else {
                    mBinding.tvMeTitle.setAlpha(0f);
                }
            }
        });
    }

    private void initTabLayout() {
        mItems = FragmentPagerItems.with(this).create();
        mAdapter = new FragmentStatePagerItemAdapter(getSupportFragmentManager(), mItems);
        mBinding.viewPagerMe.setAdapter(mAdapter);

        mBinding.smartTabLayoutMe.setCustomTabView((container, position, adapter) -> {
            GradientTextView view = new GradientTextView(container.getContext());
            view.setText((String) adapter.getPageTitle(position));
            view.setTextSize(ScreenUtils.sp2px(14));
            view.setSourceTextColor(ContextCompat.getColor(this, R.color.app_secondary_text));
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

            //设置点击效果
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            view.setBackgroundResource(outValue.resourceId);

            //设置padding
            int horizontalPadding = ScreenUtils.dp2px(10);
            view.setPadding(horizontalPadding, 0, horizontalPadding, 0);
            return view;
        });
        mBinding.smartTabLayoutMe.setOnTabClickListener(position -> {
            mIsTabClicked = true;
            changeTab(position);
        });
        mBinding.smartTabLayoutMe.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0 && !mIsTabClicked) {
                    //渐变tab颜色
                    GradientTextView left = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position);
                    GradientTextView right = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position + 1);
                    left.setAlphaRatio(1 - positionOffset);
                    right.setAlphaRatio(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mIsTabClicked && state == ViewPager.SCROLL_STATE_IDLE) {
                    mIsTabClicked = false;
                }
            }
        });

        //初始化ViewPager
        mItems.add(FragmentPagerItem.of(getResources().getString(R.string.attention_frag_title), AttentionFragment.class));
        mItems.add(FragmentPagerItem.of(getResources().getString(R.string.about_me_frag_title), AboutMeFragement.class));
        mAdapter.notifyDataSetChanged();
        mBinding.smartTabLayoutMe.setViewPager(mBinding.viewPagerMe);
        changeTab(0);
    }

    private void changeTab(int position) {
        GradientTextView selected = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(position);
        selected.setAlphaRatio(1f);

        //reset其他tab
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (i != position) {
                GradientTextView tab = (GradientTextView) mBinding.smartTabLayoutMe.getTabAt(i);
                tab.setAlphaRatio(0f);
            }
        }
    }

    private void setupBasicInfo() {
        if (mUser != null) {
            updateUser(mUser.getAvatarUrl());
            //mBinding..setText(mUser.getNickName());
        }
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.iv_me_back:
                onBackPressed();
                break;
            case R.id.circle_iv_me_avatar:
                AvatarActivity.launchActivity(this, mBinding.circleIvMeAvatar, getResources().getString(R.string.transition_avatar));
                break;
            case R.id.ll_me_my_info:

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
                                        //mBinding.circleIvMeAvatar.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }
                    }));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
