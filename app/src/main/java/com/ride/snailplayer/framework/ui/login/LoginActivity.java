package com.ride.snailplayer.framework.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.bmoblib.listener.SimpleFindListener;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityLoginBinding;
import com.ride.snailplayer.databinding.DialogCommonBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;
import com.ride.util.common.util.ScreenUtils;
import com.ride.util.common.util.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import me.dkzwm.smoothrefreshlayout.utils.PixelUtl;
import rx.Subscriber;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding mBinding;
    private MaterialDialog mErrorDialog;

    private boolean mIsPasswordVisibled;

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, LoginActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.setLoginActionHandler(this);

        setupToolbar();
        setupEditText();
        setupProgressWheel();

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etAccount);
            return false;
        });
    }

    private void setupProgressWheel() {
        mBinding.progressWheel.spin();
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

    /**
     * 关于账号EditText和密码EditText的清空输入和展示密码View的可见性遵循如下的约定：
     * <ul>
     * <li>当EditText无焦点时，不显示清空输入View；获得焦点时，若此时输入不为空，显示清空输入View, 否则不显示</li>
     * <li>当用户正在输入时，若输入不为空，显示清空输入View，否则不显示</li>
     * <li>当密码EditText获得焦点时，显示展示密码View, 否则不显示</li>
     * </ul>
     */
    private void setupEditText() {
        mBinding.etAccount.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearAccount, !TextUtils.isEmpty(s));
            }
        });
        mBinding.etAccount.setOnFocusChangeListener((v, hasFocus) -> {
            String str = mBinding.etAccount.getText().toString();
            changeViewVisibility(mBinding.ivClearAccount, hasFocus && !TextUtils.isEmpty(str));
        });

        mBinding.etPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearPassword, !TextUtils.isEmpty(s));
            }
        });
        mBinding.etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            changeViewVisibility(mBinding.ivVisiblePassword, hasFocus);

            String str = mBinding.etPassword.getText().toString();
            changeViewVisibility(mBinding.ivClearPassword, hasFocus && !TextUtils.isEmpty(str));
        });
    }

    private void changeViewVisibility(View view, boolean shouldVisible) {
        if (shouldVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void changeVisibilityView() {
        if (mIsPasswordVisibled) {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_no_visibility);
        } else {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_visibility);
        }
        mIsPasswordVisibled = !mIsPasswordVisibled;
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.iv_clear_account:
                mBinding.etAccount.setText("");
                break;
            case R.id.iv_clear_password:
                mBinding.etPassword.setText("");
                break;
            case R.id.iv_visible_password:
                changeVisibilityView();

                //改变密码显示
                if (mIsPasswordVisibled) {
                    mBinding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mBinding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.tv_forget_password:
                break;
            case R.id.tv_new_user_register:
                RegisterActivity.launchActivity(this);
                break;
            case R.id.login_btn:
                String account = mBinding.etAccount.getText().toString();
                String password = mBinding.etPassword.getText().toString();
                processLogin(account, password);
                break;
        }
    }

    private void processLogin(String account, String password) {
        hideSoftInputAndDialog();

        if (TextUtils.isEmpty(account)) {
            mBinding.etAccount.requestFocus();
            ToastUtils.showShortToast("请输入账号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.etPassword.requestFocus();
            ToastUtils.showShortToast("请输入密码");
            return;
        }

        //验证账号和密码是否合法
        if (!RegexUtils.checkMobile(account) || !RegexUtils.IsCorrectUserPassword(password)) {
            Timber.i("用户输入不合法");
            showErrorDialog();
        }

        Timber.i("用户输入合法，开始登录");

        //查询用户
        User.loginByAccountObservable(User.class, account, password)
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onStart() {
                        mBinding.progressWheel.spin();
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                    }

                    @Override
                    public void onNext(User user) {

                    }
                });
    }

    private void showErrorDialog() {
        DialogCommonBinding binding = DialogCommonBinding.inflate(getLayoutInflater());
        binding.setIsSingleChoice(true);
        binding.setTitle(getResources().getString(R.string.error_dialog_title));
        binding.setContent(getResources().getString(R.string.error_dialog_content));
        binding.setListener(view -> {
            int id = view.getId();
            switch (id) {
                case R.id.tv_common_dialog_single:
                    if (mErrorDialog != null && mErrorDialog.isShowing()) {
                        mErrorDialog.dismiss();
                    }
                    break;
            }
        });
        mErrorDialog = new MaterialDialog.Builder(this)
                .customView(binding.getRoot(), false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInputAndDialog();
    }

    private void hideSoftInputAndDialog() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
        }
        KeyboardUtils.hideSoftInput(mBinding.etAccount);
        KeyboardUtils.hideSoftInput(mBinding.etPassword);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
