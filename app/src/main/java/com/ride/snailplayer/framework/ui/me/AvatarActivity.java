package com.ride.snailplayer.framework.ui.me;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ride.snailplayer.R;
import com.ride.snailplayer.databinding.ActivityAvatarBinding;
import com.ride.snailplayer.databinding.DialogChangeAvatarBinding;
import com.ride.snailplayer.databinding.DialogPopupBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.event.listener.DataBindingClickListener;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.viewmodel.AvatarViewModel;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.util.Utils;
import com.ride.snailplayer.util.ucrop.UCropClient;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ToastUtils;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobUser;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AvatarActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    public static String AVATAR_FILE_PATH;

    private static final int PERMISSION_CODE_CAMERA = 88;
    private static final int PERMISSION_CODE_ALBUM = 89;

    private static final int REQUEST_CODE_CAMERA = 90;
    private static final int REQUEST_CODE_ALBUM = 91;

    private ActivityAvatarBinding mBinding;
    private AvatarViewModel mAvatarViewModel;
    private UserViewModel mUserViewModel;

    private User mUser;

    private MaterialDialog mChangeAvatarDialog;

    private Uri mCameraPhotoUri;
    private Uri mCropResultUri;
    private PopupWindow mPopupWindow;
    private UCropClient mUCropClient;

    public static void launchActivity(Activity activity, View element, String elementName) {
        Intent intent = new Intent(activity, AvatarActivity.class);
        ActivityOptionsCompat optionsCompat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity, element, elementName);
        } else {
            optionsCompat = ActivityOptionsCompat
                    .makeScaleUpAnimation(element, element.getWidth() / 2, element.getHeight() / 2, 0, 0);
        }
        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_avatar);
        mBinding.setAvatarActionHandler(this);
        mAvatarViewModel = ViewModelProviders.of(this).get(AvatarViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        init();
    }

    private void init() {
        mUser = BmobUser.getCurrentUser(User.class);

        AVATAR_FILE_PATH =  getCacheDir() + File.separator  + "avatar.jpg";

        mUCropClient = new UCropClient.Builder(this)
                .compressionFormat(Bitmap.CompressFormat.JPEG)
                .compressQuality(95)
                .build();

        mBinding.ivAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_profile));
    }

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.tv_change_avatar:
                DialogChangeAvatarBinding binding = DialogChangeAvatarBinding.inflate(getLayoutInflater());
                binding.setChangeAvatarCallback(v -> {
                    dismissDialog();
                    switch (v.getId()) {
                        case R.id.btn_avatar_take_photo:
                            break;
                        case R.id.btn_avatar_pick_from_album:
                            break;
                    }
                });

                mChangeAvatarDialog = new MaterialDialog.Builder(this)
                        .customView(binding.getRoot(), false)
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .show();
                break;
        }
    }

    public void dismissDialog() {
        if (mChangeAvatarDialog != null && mChangeAvatarDialog.isShowing()) {
            mChangeAvatarDialog.dismiss();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        if (!handled) {
            onBackPressed();
        }
        return true;
    }

    private void showPopupWindow() {
        DialogPopupBinding binding = DialogPopupBinding.inflate(LayoutInflater.from(this));
        binding.setFirstBtnText(getResources().getString(R.string.take_photo));
        binding.setSecondBtnText(getResources().getString(R.string.pick_photo_from_album));
        binding.setThirdBtnText(getResources().getString(R.string.negative_text));
        binding.setPopupCallback(view -> {
            mPopupWindow.dismiss();

            final int id = view.getId();
            switch (id) {
                case R.id.dialog_popup_first_btn:
                    getPhotoFormCamera();
                    break;
                case R.id.dialog_popup_second_btn:
                    getPhotoFormAlbum();
                    break;
                case R.id.dialog_popup_third_btn:
                    //Do nothing
                    break;
            }
        });
        binding.executePendingBindings();

        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setContentView(binding.getRoot());
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.app_white)));
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnimationStyle);
        mPopupWindow.showAtLocation(mBinding.getRoot(), Gravity.BOTTOM, 0, 0);
    }

    @AfterPermissionGranted(PERMISSION_CODE_CAMERA)
    public void getPhotoFormCamera() {
        String[] perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mCameraPhotoUri = Uri.fromFile(Utils.createTempPhotoFileInSdCard(this, Bitmap.CompressFormat.JPEG));
            if (mCameraPhotoUri != null) {
                Timber.i("相机图片所在路径=" + mCameraPhotoUri.getPath());

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPhotoUri);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        } else {
            EasyPermissions.requestPermissions(this, "拍照需要访问您的相机和您的存储空间", PERMISSION_CODE_CAMERA, perms);
        }
    }

    @AfterPermissionGranted(PERMISSION_CODE_ALBUM)
    public void getPhotoFormAlbum() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_ALBUM);
        } else {
            EasyPermissions.requestPermissions(this, "读取图片需要访问您的存储空间", PERMISSION_CODE_ALBUM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }

                mCameraPhotoUri = Uri.fromFile(Utils.createTempPhotoFileInSdCard(this, Bitmap.CompressFormat.JPEG));
                if (mCameraPhotoUri != null) {
                    Timber.i("相机图片所在路径=" + mCameraPhotoUri.getPath());

                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPhotoUri);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                break;
            case PERMISSION_CODE_ALBUM:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Timber.i("权限申请成功=" + perms.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Timber.i("权限申请失败=" + perms.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    Timber.i("拍摄的图片所在路径=" + mCameraPhotoUri.getPath());

                    Uri destinationUri = Uri.fromFile(new File(AVATAR_FILE_PATH));
                    mUCropClient.newCrop(mCameraPhotoUri, destinationUri, AvatarActivity.this);
                }
                break;
            case REQUEST_CODE_ALBUM:
                if (resultCode == RESULT_OK && data != null) {
                    Uri sourceUri = data.getData();
                    if (sourceUri != null) {
                        Timber.i("所选图片的路径=" + sourceUri.getPath());

                        Uri destinationUri = Uri.fromFile(new File(AVATAR_FILE_PATH));
                        mUCropClient.newCrop(sourceUri, destinationUri, AvatarActivity.this);
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK && data != null) {
                    mCropResultUri = UCrop.getOutput(data);
                    if (mCropResultUri != null) {
                        Glide.with(AvatarActivity.this)
                                .load(mCropResultUri)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .centerCrop()
                                .into(mBinding.ivAvatar);
                    }
                }
                break;
            case UCrop.RESULT_ERROR:
                ToastUtils.showShortToast("剪裁失败");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mCropResultUri != null) {
            EventBus.getDefault().post(new OnAvatarChangeEvent());
        }
        ActivityCompat.finishAfterTransition(this);
    }
}
