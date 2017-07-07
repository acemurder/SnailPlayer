package com.ride.snailplayer.framework.ui.info;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityUserInfoBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.func.MainThreadSingleTransformer;
import com.ride.util.common.log.Timber;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * @author Stormouble
 * @since 2017/7/6.
 */

public class UserInfoActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DATE_DIALOG_TAG_BIRTHDAY = "birthday";

    private ActivityUserInfoBinding mBinding;
    private UserViewModel mUserViewModel;
    private UserInfoViewModel mUserInfoViewModel;
    private User mUser;

    public static void launchActivity(@NonNull Activity startingActivity) {
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, UserInfoActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, compat.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        mBinding.setUserInfoActionHandler(this);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUser = mUserViewModel.getUser();

        initUserAvatar();
    }

    private void initUserAvatar() {
        if (mUser != null) {
            mUserInfoViewModel.setAvatarForCircleImageView(mUser.getAvatarUrl())
                    .compose(MainThreadSingleTransformer.instance())
                    .subscribe(bitmap -> {}, Timber::e);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user_info_back:
                onBackPressed();
                break;
            case R.id.ll_user_info_avatar:
                break;
            case R.id.ll_user_info_nickname:
                break;
            case R.id.ll_user_info_sex:
                break;
            case R.id.ll_user_info_birthday:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = DatePickerDialog.newInstance(this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                dialog.dismissOnPause(true);
                dialog.showYearPickerFirst(false);
                dialog.setVersion(DatePickerDialog.Version.VERSION_2);
                dialog.show(getFragmentManager(), DATE_DIALOG_TAG_BIRTHDAY);
                break;
            case R.id.ll_user_info_university:
                break;
            case R.id.ll_user_info_sign:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        switch (view.getTag()) {
            case DATE_DIALOG_TAG_BIRTHDAY:
                mBinding.tvUserInfoBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                break;
        }
    }
}
