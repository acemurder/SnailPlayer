package com.ride.snailplayer.framework.ui.register.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.snailplayer.databinding.FragmentCheckSmsCodeBinding;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ActivityUtils;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class CheckSMSCodeFragment extends BaseRegisterFragment {

    private FragmentCheckSmsCodeBinding mBinding;
    private CountDownTimer mTimer;
    private String mPhoneNumber;

    public static CheckSMSCodeFragment newInstance(String phoneNumber) {
        CheckSMSCodeFragment fragment = new CheckSMSCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(InputPhoneNumberFragment.PHONE_NUMBER, phoneNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CheckSMSCodeFragment() {
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
        mBinding = FragmentCheckSmsCodeBinding.inflate(inflater, container, false);
        mBinding.setCheckSMSCodeActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSMSCodeInfo();
        setupEditText();
        setupNextButton();
    }

    private void setupSMSCodeInfo() {
        String info = String.format(getResources().getString(R.string.sms_code_info), "+86 " + mPhoneNumber);
        Spannable span = new SpannableString(info);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.theme_accent)),
                9, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvSmsCodeInfo.setText(span);
    }

    private void setupEditText() {
        mBinding.etSmsCode.setOnFocusChangeListener((v, hasFocus) -> {
            String str = mBinding.etSmsCode.getText().toString();
            changeViewVisibility(mBinding.ivClearSmsCode, hasFocus && !TextUtils.isEmpty(str));
        });
        mBinding.etSmsCode.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearSmsCode, !TextUtils.isEmpty(s));

                changeNextButtonEnabled(RegexUtils.isMatch("^\\d{6}$", s));
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
        mHostActivity.processIndicatorView(RegisterActivity.STEP_SECOND);

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etSmsCode);
            return false;
        });

        //请求sms code
        sendSMSCode();

        //设置重新计时的textview
        changeResendEnabled(false);
        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String secondLeft = String.valueOf(millisUntilFinished / 1000) + "s";
                String text = String.format(
                        getResources().getString(R.string.resend_sms_code_with_countdown), secondLeft);
                mBinding.tvResendSmsCode.setText(text);
            }

            @Override
            public void onFinish() {
                changeResendEnabled(true);
                String text = String.format(
                        getResources().getString(R.string.resend_sms_code_with_countdown), "");
                mBinding.tvResendSmsCode.setText(text);
            }
        };
        mTimer.start();
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                mHostActivity.processBack();
                break;
            case R.id.tv_resend_sms_code:
                changeResendEnabled(false);
                mTimer.start();
                break;
            case R.id.iv_clear_sms_code:
                mBinding.etSmsCode.setText("");
                break;
            case R.id.next_btn:
                reset();
                showProgress();

                BmobQuery<User> query = new BmobQuery<>();
                query = query.addWhereEqualTo("mobilePhoneNumber", mPhoneNumber);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        dismissProgress();
                        if (e != null) {
                            Timber.i("查询用户是否存在失败");
                        } else {
                            Timber.i("查询用户是否存在成功");
                            if (list != null) {
                                if (list.isEmpty()) {
                                    Timber.i("用户不存在");
                                    ActivityUtils.startAnotherFragment(getFragmentManager(), CheckSMSCodeFragment.this,
                                            FillBasicInfoFragment.newInstance(mPhoneNumber), R.id.register_container);
                                } else {
                                    Timber.i("用户已存在");
                                    ActivityUtils.startAnotherFragment(getFragmentManager(), CheckSMSCodeFragment.this,
                                            PhoneBoundFragment.newInstance(list.get(0)), R.id.register_container);
                                }
                            }
                        }
                    }
                });
                break;
        }
    }

    private void processNext() {
        reset();
        showProgress();

        //验证
        String inputCode = mBinding.etSmsCode.getText().toString();
        BmobSMS.verifySmsCode(mPhoneNumber, inputCode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dismissProgress();
                if (e != null) {
                    Timber.i("验证失败");
                    showErrorDialog(getResources().getString(R.string.sms_error_dialog_title),
                            getResources().getString(R.string.sms_error_dialog_content));
                } else {
                    Timber.i("验证成功");
                    BmobQuery<User> query = new BmobQuery<>();
                    query = query.addWhereEqualTo("mobilePhoneNumber", mPhoneNumber);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e != null) {
                                Timber.i("查询用户是否存在失败");
                                showCommonErrorDialog(getResources().getString(R.string.sms_error_dialog_title));
                            } else {
                                Timber.i("查询用户是否存在成功");
                                if (list != null && list.isEmpty()) {
                                    Timber.i("用户不存在");
                                    ActivityUtils.startAnotherFragment(getFragmentManager(), CheckSMSCodeFragment.this,
                                            FillBasicInfoFragment.newInstance(mPhoneNumber), R.id.register_container);
                                } else {
                                    Timber.i("用户已存在");
                                    ActivityUtils.startAnotherFragment(getFragmentManager(), CheckSMSCodeFragment.this,
                                            PhoneBoundFragment.newInstance(list.get(0)), R.id.register_container);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changeResendEnabled(boolean enabled) {
        mBinding.tvResendSmsCode.setEnabled(enabled);
        if (enabled) {
            mBinding.tvResendSmsCode.setTextColor(ContextCompat.getColor(getContext(), R.color.selector_common_text));
        } else {
            mBinding.tvResendSmsCode.setTextColor(ContextCompat.getColor(getContext(), R.color.body_text_disabled));
        }
    }

    private void sendSMSCode() {
        BmobSMS.requestSMSCode(mPhoneNumber, SnailPlayerConfig.BMOB_SMS_TEMPLATE, new QueryListener<Integer>() {
            @Override
            public void done(Integer result, BmobException e) {
                if (e != null) {
                    Timber.e("短信发送失败");
                } else {
                    Timber.i("短信发送成功");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void reset() {
        KeyboardUtils.hideSoftInput(mBinding.etSmsCode);
    }
}
