package com.ride.snailplayer.framework.ui.register.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentInputPhoneNumberBinding;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.snailplayer.util.TextWatcherAdapter;
import com.ride.util.common.util.ActivityUtils;
import com.ride.util.common.util.KeyboardUtils;
import com.ride.util.common.util.RegexUtils;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class InputPhoneNumberFragment extends BaseRegisterFragment {

    public static final String PHONE_NUMBER = "phone_number";

    private FragmentInputPhoneNumberBinding mBinding;

    public static InputPhoneNumberFragment newInstance() {
        InputPhoneNumberFragment fragment = new InputPhoneNumberFragment();
        return fragment;
    }

    public InputPhoneNumberFragment() {
        //need empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentInputPhoneNumberBinding.inflate(inflater, container, false);
        mBinding.setInputPhoneNumberActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEditText();
        setupNextButton();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置indicator
        mHostActivity.processIndicatorView(RegisterActivity.STEP_FIRST);

        //打开软键盘
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(() -> {
            KeyboardUtils.showSoftInput(mBinding.etPhoneNumber);
            return false;
        });
    }

    /**
     * 监听EditText以显示clear view
     */
    private void setupEditText() {
        mBinding.etPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            String str = mBinding.etPhoneNumber.getText().toString();
            changeViewVisibility(mBinding.ivClearPhoneNumber, hasFocus && !TextUtils.isEmpty(str));
        });
        mBinding.etPhoneNumber.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeViewVisibility(mBinding.ivClearPhoneNumber, !TextUtils.isEmpty(s));
                changeNextButtonEnabled(RegexUtils.checkMobile(s.toString()));
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

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                mHostActivity.processBack();
                break;
            case R.id.iv_clear_phone_number:
                mBinding.etPhoneNumber.setText("");
                break;
            case R.id.next_btn:
                processRegister();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftInput();
    }

    private void processRegister() {
        hideSoftInput();

        String phoneNumber = mBinding.etPhoneNumber.getText().toString();
        ActivityUtils.startAnotherFragment(getFragmentManager(), this,
                CheckSMSCodeFragment.newInstance(phoneNumber), R.id.register_container);
    }

    private void hideSoftInput() {
        KeyboardUtils.hideSoftInput(mBinding.etPhoneNumber);
    }
}
