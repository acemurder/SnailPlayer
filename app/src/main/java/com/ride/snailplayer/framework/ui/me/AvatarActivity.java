package com.ride.snailplayer.framework.ui.me;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ride.snailplayer.R;
import com.ride.snailplayer.config.contants.PermissionsConstants;
import com.ride.snailplayer.databinding.ActivityAvatarBinding;
import com.ride.snailplayer.databinding.DialogChangeAvatarBinding;
import com.ride.snailplayer.databinding.DialogCommonBinding;
import com.ride.snailplayer.framework.base.BaseActivity;
import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.framework.ui.me.event.OnAvatarChangeEvent;
import com.ride.snailplayer.framework.ui.me.viewmodel.AvatarViewModel;
import com.ride.snailplayer.framework.viewmodel.UserViewModel;
import com.ride.snailplayer.net.func.MainThreadObservableTransformer;
import com.ride.snailplayer.util.ucrop.UCropClient;
import com.ride.snailplayer.widget.dialog.BaseDialog;
import com.ride.snailplayer.widget.dialog.ProgressDialog;
import com.ride.util.common.log.Timber;
import com.ride.util.common.util.ToastUtils;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import pub.devrel.easypermissions.EasyPermissions;


public class AvatarActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    /**
     * 申请拍摄照片所需权限的RequestCode
     */
    private static final int PERM_RQ_CAMERA = 88;

    /**
     * 申请读取相册图片所需权限的RequestCode
     */
    private static final int PERM_RQ_STORAGE = 89;

    private static final int REQUEST_CODE_CAMERA = 90;
    private static final int REQUEST_CODE_ALBUM = 91;

    private ActivityAvatarBinding mBinding;
    private AvatarViewModel mAvatarViewModel;
    private UserViewModel mUserViewModel;
    private User mUser;

    private UCropClient mUCropClient;
    private MaterialDialog mChangeAvatarDialog;
    private MaterialDialog mPromptPermNeededDialog;
    private BaseDialog mProgressDialog;

    private Uri mPhotoUriFromCamera;

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
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mAvatarViewModel = ViewModelProviders.of(this).get(AvatarViewModel.class);
        mUser = mUserViewModel.getUser();

        init();
    }

    private void init() {
        mUCropClient = new UCropClient.Builder(this)
                .compressionFormat(Bitmap.CompressFormat.JPEG)
                .compressQuality(95)
                .build();
        if (mUser != null) {
            Glide.with(this).load(mUser.getAvatarUrl()).crossFade().centerCrop().into(mBinding.ivAvatar);
        } else {
            Glide.with(this).load(R.drawable.default_profile).into(mBinding.ivAvatar);
            Timber.w("应用发生错误，user ==  null");
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

    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.tv_change_avatar:
                DialogChangeAvatarBinding binding = DialogChangeAvatarBinding.inflate(getLayoutInflater());
                binding.setChangeAvatarCallback(v -> {
                    dismissChangeAvatarDialog();
                    switch (v.getId()) {
                        case R.id.btn_avatar_take_photo:
                            startTakePhoto();
                            break;
                        case R.id.btn_avatar_pick_from_album:
                            startPickPhotoFromAlbum();
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

    public void dismissChangeAvatarDialog() {
        if (mChangeAvatarDialog != null && mChangeAvatarDialog.isShowing()) {
            mChangeAvatarDialog.dismiss();
        }
    }

    private void startTakePhoto() {
        if (EasyPermissions.hasPermissions(this, PermissionsConstants.CAMERA_PERMISSIONS)) {
            doTakePhoto();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.perm_rational_of_camera), PERM_RQ_CAMERA, PermissionsConstants.CAMERA_PERMISSIONS);
        }
    }

    private void doTakePhoto() {
        mAvatarViewModel.getPhotoFromCameraUri(getExternalCacheDir() == null ? getCacheDir() : getExternalCacheDir())
                .doOnSuccess(uri -> mPhotoUriFromCamera = uri)
                .subscribe(uri -> {
                    Timber.i("相机图片所在路径=" + uri.getPath());
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                });
    }

    private void startPickPhotoFromAlbum() {
        if (EasyPermissions.hasPermissions(this, PermissionsConstants.STORAGE_PERMISSIONS)) {
            doPickPhotoFromAlbum();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.perm_rational_of_read_mobile_photo), PERM_RQ_STORAGE, PermissionsConstants.STORAGE_PERMISSIONS);
        }
    }

    private void doPickPhotoFromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Timber.i("requestCode=" + requestCode + ", 权限申请成功=" + perms.toString());
        switch (requestCode) {
            case PERM_RQ_CAMERA:
                doTakePhoto();
                break;
            case PERM_RQ_STORAGE:
                doPickPhotoFromAlbum();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Timber.i("requestCode=" + requestCode + ", 权限申请失败=" + perms.toString());
        switch (requestCode) {
            case PERM_RQ_CAMERA:
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    showPromptPermNeededDialog(getResources().getString(R.string.permissions_dialog_title),
                            getResources().getString(R.string.when_camera_perms_denied));
                }
                break;
            case PERM_RQ_STORAGE:
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    showPromptPermNeededDialog(getResources().getString(R.string.permissions_dialog_title),
                            getResources().getString(R.string.when_read_photo_perms_denied));
                }
                break;
        }
    }

    private void showPromptPermNeededDialog(String title, String content) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Timber.d("dialog title=" + title + ", content=" + content);
            return;
        }

        DialogCommonBinding binding = DialogCommonBinding.inflate(LayoutInflater.from(this));
        binding.setIsSingleChoice(true);
        binding.setTitle(title);
        binding.setContent(content);
        binding.setListener(view -> {
            dismissPromptPermNeededDialog();
            switch (view.getId()) {
                case R.id.tv_common_dialog_single:
                    break;
            }
        });
        mPromptPermNeededDialog = new MaterialDialog.Builder(this)
                .customView(binding.getRoot(), false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void dismissPromptPermNeededDialog() {
        if (mPromptPermNeededDialog != null && mPromptPermNeededDialog.isShowing()) {
            mPromptPermNeededDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    Timber.i("拍摄的图片所在路径=" + mPhotoUriFromCamera.getPath());
                    mAvatarViewModel.getTempPhotoFileUri(getCacheDir())
                            .subscribe(destinationUri -> mUCropClient.newCrop(mPhotoUriFromCamera, destinationUri, AvatarActivity.this));
                }
                break;
            case REQUEST_CODE_ALBUM:
                if (resultCode == RESULT_OK && data != null) {
                    Uri sourceUri = data.getData();
                    if (sourceUri != null) {
                        Timber.i("所选图片的路径=" + sourceUri.getPath());
                        mAvatarViewModel.getTempPhotoFileUri(getCacheDir())
                                .subscribe(destinationUri -> mUCropClient.newCrop(sourceUri, destinationUri, AvatarActivity.this));
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK && data != null) {
                    Uri cropResultUri = UCrop.getOutput(data);
                    if (cropResultUri != null) {
                        mUserViewModel.uploadUserAvatar(cropResultUri)
                                .compose(MainThreadObservableTransformer.instance())
                                .flatMap(new Function<String, ObservableSource<String>>() {
                                    @Override
                                    public ObservableSource<String> apply(@io.reactivex.annotations.NonNull String url) throws Exception {
                                        return mUserViewModel.updateUserAvatar(url);
                                    }
                                })
                                .doAfterNext(url -> EventBus.getDefault().post(new OnAvatarChangeEvent()))
                                .subscribe(new Observer<String>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                        if (!d.isDisposed()) {
                                            mProgressDialog = new ProgressDialog(AvatarActivity.this).initProgressDialog();
                                            mProgressDialog.show();
                                        }
                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull String url) {
                                        dismissProgressDialog();
                                        ToastUtils.showShortToast("上传头像成功");
                                        Glide.with(AvatarActivity.this)
                                                .load(url)
                                                .skipMemoryCache(true)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .centerCrop()
                                                .crossFade()
                                                .into(mBinding.ivAvatar);
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        dismissProgressDialog();
                                        Timber.e(e);
                                        ToastUtils.showShortToast("上传头像失败");
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    }
                }
                break;
            case UCrop.RESULT_ERROR:
                ToastUtils.showShortToast("剪裁失败");
                break;
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissChangeAvatarDialog();
        dismissPromptPermNeededDialog();
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
