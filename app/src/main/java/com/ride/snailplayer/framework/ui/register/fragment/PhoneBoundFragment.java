package com.ride.snailplayer.framework.ui.register.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.FragmentPhoneBoundBinding;
import com.ride.snailplayer.framework.base.BaseFragment;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.register.RegisterActivity;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ActivityUtils;
import com.ride.util.common.util.ToastUtils;

/**
 * @author Stormouble
 * @since 2017/6/18.
 */

public class PhoneBoundFragment extends BaseFragment {

    public static final String EXISTED_USER = "exisited_user";

    private FragmentPhoneBoundBinding mBinding;
    private User mExistedUser;

    public static PhoneBoundFragment newInstance(User existedUser) {
        PhoneBoundFragment fragment = new PhoneBoundFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXISTED_USER, existedUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExistedUser = (User) getArguments().getSerializable(EXISTED_USER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPhoneBoundBinding.inflate(inflater, container, false);
        mBinding.setPhoneRegisteredActionHandler(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mExistedUser != null) {
            setupUserInfo();
        } else {
            Timber.e("用户为空");
        }
    }

    private void setupUserInfo() {
        mBinding.tvAccount.setText(mExistedUser.getUsername());
        //隐藏部分手机号
        String hidedPhoneNumber = "*******" + mExistedUser.getMobilePhoneNumber().subSequence(7, 11);
        mBinding.tvPhoneNumber.setText(hidedPhoneNumber);

        Glide.with(getContext())
                .load(mExistedUser.getAvatraUrl())
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.default_profile))
                .into(mBinding.ivAvatar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置indicator
        RegisterActivity activity = (RegisterActivity) mActivity;
        activity.processIndicatorView(RegisterActivity.STEP_THIRD);
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.ll_register_back:
                RegisterActivity activity = (RegisterActivity) mActivity;
                activity.processBack();
                break;
            case R.id.direct_login_btn:
                ActivityUtils.startAnotherFragment(getFragmentManager(), this,
                        DirectLoginFragment.newInstance(mExistedUser), R.id.register_container);
                break;
            case R.id.continue_register_btn:
                if (mExistedUser != null) {
                    ActivityUtils.startAnotherFragment(getFragmentManager(), this,
                            FillBasicInfoFragment.newInstance(mExistedUser.getMobilePhoneNumber()), R.id.register_container);
                } else {
                    ToastUtils.showShortToast("暂不支持该操作");
                }
                break;
        }
    }
}
