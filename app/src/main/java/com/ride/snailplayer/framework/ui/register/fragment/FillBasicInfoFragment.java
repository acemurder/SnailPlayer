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

import com.afollestad.materialdialogs.MaterialDialog;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentFillBasicInfoBinding;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ActivityUtils;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import rx.Subscriber;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class FillBasicInfoFragment extends BaseRegisterFragment {

    private FragmentFillBasicInfoBinding mBinding;

    private String mPhoneNumber;
    private boolean mIsPasswordVisibled;
    private boolean mHasInputCorrectAccount;

    public static FillBasicInfoFragment newInstance(String phoneNumber) {
        FillBasicInfoFragment fragment = new FillBasicInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(InputPhoneNumberFragment.PHONE_NUMBER, phoneNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FillBasicInfoFragment() {
        //need empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoneNumber = getArguments().getString(InputPhoneNumberFragment.PHONE_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFillBasicInfoBinding.inflate(inflater, container, false);
        mBinding.setFillBasicInfoActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEditText();
        setupNextButton();
    }

    private void setupEditText() {
        mBinding.etNickname.setOnFocusChangeListener((v, hasFocus) -> {
            String str = mBinding.etNickname.getText().toString();
            changeViewVisibility(mBinding.ivClearNickname, hasFocus && !TextUtils.isEmpty(str));
        });
        mBinding.etNickname.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //账号是否合法
                mHasInputCorrectAccount = RegexUtils.isCorrectUserAccount(s.toString());

                changeViewVisibility(mBinding.ivClearNickname, !TextUtils.isEmpty(s));
            }
        });

        mBinding.etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            changeViewVisibility(mBinding.ivVisiblePassword, hasFocus);

            String str = mBinding.etPassword.getText().toString();
            changeViewVisibility(mBinding.ivClearPassword, hasFocus && !TextUtils.isEmpty(str));
        });
        mBinding.etPassword.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearPassword, !TextUtils.isEmpty(s));

                //用户输入正确则使button enable
                changeNextButtonEnabled(mHasInputCorrectAccount && RegexUtils.IsCorrectUserPassword(s.toString()));
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
        mHostActivity.processIndicatorView(RegisterActivity.STEP_FOURTH);

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etNickname);
            return false;
        });
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                mHostActivity.processBack();
                break;
            case R.id.iv_clear_nickname:
                mBinding.etNickname.setText("");
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
                clear();
                showProgress();

                User user = new User();
                user.setUsername(mPhoneNumber);
                user.setNickName(mBinding.etNickname.getText().toString());
                user.setPassword(mBinding.etPassword.getText().toString());
                user.setMobilePhoneNumber(mPhoneNumber);
                user.setMobilePhoneNumberVerified(true);
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        dismissProgress();
                        if (e == null) {
                            Timber.i("注册成功");
                            ActivityUtils.startAnotherFragment(getFragmentManager(), FillBasicInfoFragment.this,
                                    CompleteRegisterFragment.newInstance(), R.id.register_container);
                        } else {
                            Timber.i("注册失败");
                            showCommonErrorDialog(getResources().getString(R.string.register_failed));
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void clear() {
        super.clear();
        KeyboardUtils.hideSoftInput(mBinding.etNickname);
        KeyboardUtils.hideSoftInput(mBinding.etPassword);
    }

    private void changeVisibilityView() {
        if (mIsPasswordVisibled) {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_no_visibility);
        } else {
            mBinding.ivVisiblePassword.setImageResource(R.drawable.ic_visibility);
        }
        mIsPasswordVisibled = !mIsPasswordVisibled;
    }

}
