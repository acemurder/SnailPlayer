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
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityEditSignBinding;
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

public class EditSignActivity extends BaseActivity {

    private ActivityEditSignBinding mBinding;
    private UserInfoViewModel mUserInfoViewModel;
    private UserViewModel mUserViewModel;
    private User mUser;
    private BaseDialog mProgressDialog;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, EditSignActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sign);
        mBinding.setEditSignActionHandler(this);
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUser = mUserViewModel.getUser();

        setupEditText();
        setupProgress();

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etDescription);
            return false;
        });
    }

    private void setupEditText() {
        mBinding.etDescription.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.tvWordCount.setText(150 - s.length());
            }
        });
    }

    private void setupProgress() {
        mProgressDialog = new ProgressDialog(this).initProgressDialog();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit_sign_back:
                onBackPressed();
                break;
            case R.id.tv_edit_sign_save:
                mUserInfoViewModel.isUserSignChanged(mUser, mBinding.etDescription.getText().toString())
                        .flatMap(new Function<String, ObservableSource<User>>() {
                            @Override
                            public ObservableSource<User> apply(@NonNull String sign) throws Exception {
                                return mUserInfoViewModel.updateUserSign(mUser, sign);
                            }
                        })
                        .compose(MainThreadObservableTransformer.instance())
                        .doAfterNext(user -> EventBus.getDefault().post(new OnUserInfoUpdateEvent()))
                        .subscribe(new Observer<User>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                if (!d.isDisposed()) {
                                    mProgressDialog.show();
                                }
                            }

                            @Override
                            public void onNext(@NonNull User user) {
                                dismissProgressDialog();
                                ToastUtils.showShortToast(EditSignActivity.this, "保存成功");
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                dismissProgressDialog();
                                Timber.e(e);
                                ToastUtils.showShortToast(EditSignActivity.this, "保存失败");
                            }

                            @Override
                            public void onComplete() {
                                dismissProgressDialog();
                                KeyboardUtils.hideSoftInput(EditSignActivity.this);
                                onBackPressed();
                            }
                        });
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
        KeyboardUtils.hideSoftInput(this);
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
