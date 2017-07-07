package com.ride.snailplayer.framework.ui.info;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityEditNicknameBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.info.event.OnUserInfoUpdateEvent;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.snailplayer.widget.dialog.BaseDialog;
import com.ride.snailplayer.widget.dialog.ProgressDialog;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author Stormouble
 * @since 2017/7/7.
 */

public class EditNickNameActivity extends BaseActivity {

    private ActivityEditNicknameBinding mBinding;
    private UserInfoViewModel mUserInfoViewModel;
    private UserViewModel mUserViewModel;
    private BaseDialog mProgressDialog;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, EditNickNameActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_nickname);
        mBinding.setEditNickNameActionHandler(this);
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        setupEditText();
        setupProgress();

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etNickname);
            return false;
        });

        initUserInfo();
    }

    private void initUserInfo() {
        mBinding.setUser(mUserViewModel.getUser());
    }

    private void setupEditText() {
        mBinding.etNickname.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearNickname, !TextUtils.isEmpty(s));
            }
        });
        mBinding.etNickname.setOnFocusChangeListener((v, hasFocus) -> {
            //设置光标位置
            if (hasFocus) {
                mBinding.etNickname.setSelection(mBinding.etNickname.getText().length());
            }

            String str = mBinding.etNickname.getText().toString();
            changeViewVisibility(mBinding.ivClearNickname, hasFocus && !TextUtils.isEmpty(str));
        });
    }

    private void setupProgress() {
        mProgressDialog = new ProgressDialog(this).initProgressDialog();
    }

    private void changeViewVisibility(View view, boolean shouldVisible) {
        if (shouldVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit_nickname_back:
                KeyboardUtils.hideSoftInput(this);
                onBackPressed();
                break;
            case R.id.tv_edit_nickname_save:
                KeyboardUtils.hideSoftInput(this);
                User user = mUserViewModel.getUser();
                mUserInfoViewModel.isUserNicknameChanged(user, mBinding.etNickname.getText().toString())
                        .flatMap(new Function<String, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(@NonNull String nickname) throws Exception {
                                return mUserInfoViewModel.updateUserNickname(user, nickname);
                            }
                        })
                        .compose(MainThreadObservableTransformer.instance())
                        .doAfterNext(nickname -> EventBus.getDefault().post(new OnUserInfoUpdateEvent()))
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                if (!d.isDisposed()) {
                                    mProgressDialog.show();
                                }
                            }

                            @Override
                            public void onNext(@NonNull String nickname) {
                                dismissProgressDialog();
                                ToastUtils.showShortToast(EditNickNameActivity.this, "保存成功");
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                dismissProgressDialog();
                                Timber.e(e);
                                ToastUtils.showShortToast(EditNickNameActivity.this, "保存失败");
                            }

                            @Override
                            public void onComplete() {
                                dismissProgressDialog();
                                onBackPressed();
                            }
                        });
                break;
            case R.id.iv_clear_nickname:
                mBinding.etNickname.setText("");
                break;
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
