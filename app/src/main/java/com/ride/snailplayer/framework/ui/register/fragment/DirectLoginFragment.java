package com.ride.snailplayer.framework.ui.register.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentDirectLoginBinding;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.home.HomeActivity;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class DirectLoginFragment extends BaseRegisterFragment {

    private FragmentDirectLoginBinding mBinding;
    private User mExistedUser;
    private boolean mIsPasswordVisibled;

    public static DirectLoginFragment newInstance(User existedUser) {
        DirectLoginFragment fragment = new DirectLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhoneBoundFragment.EXISTED_USER, existedUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DirectLoginFragment() {
        //need empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExistedUser = (User) getArguments().getSerializable(PhoneBoundFragment.EXISTED_USER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDirectLoginBinding.inflate(inflater, container, false);
        mBinding.setDirectLoginActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mExistedUser != null) {
            setupEditText();
            setupNextButton();
        }
    }

    private void setupEditText() {
        String hidedPhoneNumber = "*******" + mExistedUser.getMobilePhoneNumber().subSequence(7, 11);
        mBinding.etAccount.setText(hidedPhoneNumber);
        mBinding.etAccount.setEnabled(false);

        mBinding.etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            changeViewVisibility(mBinding.ivVisiblePassword, hasFocus);

            String str = mBinding.etPassword.getText().toString();
            changeViewVisibility(mBinding.ivClearPassword, hasFocus && !TextUtils.isEmpty(str));
        });
        mBinding.etPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearPassword, !TextUtils.isEmpty(s));

                changeNextButtonEnabled(RegexUtils.IsCorrectUserPassword(s.toString()));
            }
        });
    }

    private void setupNextButton() {
        changeNextButtonEnabled(false);
    }

    private void changeViewVisibility(View view, boolean shouldVisible) {
        if (shouldVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void changeNextButtonEnabled(boolean enabled) {
        mBinding.nextBtn.setEnabled(enabled);
        if (enabled) {
            mBinding.nextBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_common_btn));
        } else {
            mBinding.nextBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_light_grey));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置indicator
        RegisterActivity activity = (RegisterActivity) mActivity;
        activity.processIndicatorView(RegisterActivity.STEP_ZERO);

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etPassword);
            return false;
        });
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                RegisterActivity activity = (RegisterActivity) mActivity;
                activity.processBack();
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
            case R.id.next_btn:
                reset();
                showProgress();

                User user = new User();
                user.setUsername(mExistedUser.getUsername());
                user.setPassword(mBinding.etPassword.getText().toString());
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        dismissProgress();
                        if (e == null) {
                            Timber.i("登录成功");
                            HomeActivity.launchActivity(getActivity());
                        } else {
                            Timber.i("登录失败");
                            showCommonErrorDialog(getResources().getString(R.string.login_error_dialog_title));
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        reset();
    }

    private void changeVisibilityView() {
        if (mIsPasswordVisibled) {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_no_visibility);
        } else {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_visibility);
        }
        mIsPasswordVisibled = !mIsPasswordVisibled;
    }

    private void reset() {
        KeyboardUtils.hideSoftInput(mBinding.etPassword);
    }
}
