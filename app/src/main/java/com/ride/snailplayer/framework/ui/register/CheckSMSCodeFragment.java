package com.ride.snailplayer.framework.ui.register;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.config.SnailPlayerConfig;
import com.ride.snailplayer.databinding.FragmentCheckSmsCodeBinding;
import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.util.common.util.KeyboardUtils;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class CheckSMSCodeFragment extends BaseFragment {

    public static final String PHONE_NUMBER = "phone_number";

    private FragmentCheckSmsCodeBinding mBinding;
    private String mPhoneNumber;
    private Integer mSMSCode;

    private CountDownTimer mTimer;

    public static CheckSMSCodeFragment newInstance(String phoneNumber) {
        CheckSMSCodeFragment fragment = new CheckSMSCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PHONE_NUMBER, phoneNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CheckSMSCodeFragment() {
        //need empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoneNumber = getArguments().getString(PHONE_NUMBER);
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
    }

    private void setupSMSCodeInfo() {
        String info = String.format(getResources().getString(R.string.sms_code_info), mPhoneNumber);
        Spannable span = new SpannableString(info);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.theme_accent)),
                9, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvSmsCodeInfo.setText(span);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置indicator
        RegisterActivity activity = (RegisterActivity) mActivity;
        activity.processIndicatorView(RegisterActivity.STEP_SECOND);

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
                        getResources().getString(R.string.resend_smd_code_with_countdown), secondLeft);
                mBinding.tvResendSmsCode.setText(text);
            }

            @Override
            public void onFinish() {
                changeResendEnabled(true);
                String text = String.format(
                        getResources().getString(R.string.resend_smd_code_with_countdown), "");
                mBinding.tvResendSmsCode.setText(text);
            }
        };
        mTimer.start();
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                RegisterActivity activity = (RegisterActivity) mActivity;
                activity.processBack();
                break;
            case R.id.tv_resend_sms_code:
                changeResendEnabled(false);
                mTimer.start();
                break;
            case R.id.next_btn:

                break;
        }
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
                mSMSCode = result;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void hideSoftInput() {
        KeyboardUtils.hideSoftInput(mBinding.etSmsCode);
    }
}
