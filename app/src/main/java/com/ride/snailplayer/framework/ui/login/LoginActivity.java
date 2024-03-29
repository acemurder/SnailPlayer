package com.ride.snailplayer.framework.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityLoginBinding;
import com.ride.snailplayer.databinding.DialogPopupBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.db.UserContract;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.info.event.OnUserInfoUpdateEvent;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.snailplayer.widget.dialog.BaseDialog;
import com.ride.snailplayer.widget.dialog.ErrorDialog;
import com.ride.snailplayer.widget.dialog.ProgressDialog;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.NetworkUtils;
import com.ride.util.common.util.RegexUtils;
import com.ride.util.common.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding mBinding;
    private DialogPopupBinding mDialogPopupBinding;
    private BottomSheetDialog mBottomSheetDialog;
    private BaseDialog mProgressDialog;
    private ErrorDialog mErrorDialog;

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

        mProgressDialog = new ProgressDialog(this).initProgressDialog();

        setupToolbar();
        setupEditText();

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etAccount);
            return false;
        });
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
                KeyboardUtils.hideSoftInput(this);
                mDialogPopupBinding = DialogPopupBinding.inflate(getLayoutInflater());
                mDialogPopupBinding.setFirstBtnText(getResources().getString(R.string.find_password));
                mDialogPopupBinding.setThirdBtnText(getResources().getString(R.string.negative_text));
                mDialogPopupBinding.setPopupCallback(v -> {
                    dismissBottomSheetDialog();
                    switch (v.getId()) {
                        case R.id.dialog_popup_first_btn:
                            break;
                    }
                });
                mBottomSheetDialog = new BottomSheetDialog(this);
                mBottomSheetDialog.setContentView(mDialogPopupBinding.getRoot());
                mBottomSheetDialog.show();
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

    private void dismissBottomSheetDialog() {
        if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.dismiss();
        }
    }

    private void processLogin(String account, String password) {
        KeyboardUtils.hideSoftInput(this);

        if (TextUtils.isEmpty(account)) {
            mBinding.etAccount.requestFocus();
            ToastUtils.showShortToast(LoginActivity.this, "请输入账号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.etPassword.requestFocus();
            ToastUtils.showShortToast(LoginActivity.this, "请输入密码");
            return;
        }

        //验证账号和密码是否合法
        if (!RegexUtils.checkMobile(account) || !RegexUtils.IsCorrectUserPassword(password)) {
            Timber.i("用户输入不合法");
            ErrorDialog dialog = new ErrorDialog(this);
            dialog.initSingleButtonDialog(getResources().getString(R.string.login_error_dialog_title),
                    getResources().getString(R.string.login_error_dialog_content));
            dialog.show();
            return;
        } else {
            Timber.i("用户输入合法，开始登录");
        }

        mProgressDialog.show();

        BmobQuery<User> query = new BmobQuery<>();
        query = query.addWhereEqualTo(UserContract.USER_TABLE_USERNAME, account)
                .addWhereEqualTo(UserContract.USER_TABLE_PHONE_NUMBER, account)
                .addWhereEqualTo(UserContract.USER_TABLE_PASSWORD, password);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                mProgressDialog.dismiss();
                if (e != null || list == null || list.isEmpty()) {
                    Timber.i("用户不存在");

                    mErrorDialog = new ErrorDialog(LoginActivity.this);
                    if (NetworkUtils.isNetworkAvailable()) {
                        mErrorDialog.initSingleButtonDialog(getResources().getString(R.string.login_error_dialog_title),
                                getResources().getString(R.string.login_error_dialog_content)).show();
                    } else {
                        mErrorDialog.initNetworkDialog(getResources().getString(R.string.login_error_dialog_title)).show();
                    }
                } else {
                    Timber.i("用户存在");
                    User newUser = new User();
                    newUser.setUsername(account);
                    newUser.setMobilePhoneNumber(account);
                    newUser.setPassword(password);
                    newUser.login(new SaveListener<User>() {
                        @Override
                        public void done(User result, BmobException e) {
                            if (e == null) {
                                Timber.i("登录成功");
                                EventBus.getDefault().post(new OnUserInfoUpdateEvent());
                                HomeActivity.launchActivity(LoginActivity.this);
                            } else {
                                Timber.e("登录失败," + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void dismissErrorDialog() {
        if (mErrorDialog != null) {
            mErrorDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtils.hideSoftInput(this);
        dismissBottomSheetDialog();
        dismissProgressDialog();
        dismissErrorDialog();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
