package com.ride.snailplayer.framework.ui.me.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * @author Stormouble
 * @since 2017/7/6.
 */

public class AvatarViewModel extends AndroidViewModel {

    public static final String AVATAR_FILE_NAME = "avatar.jpeg";

    public AvatarViewModel(Application application) {
        super(application);
    }

    public Maybe<Uri> getPhotoFromCameraUri(@NonNull File storeDir) {
        return Maybe.just(storeDir)
                .filter(dir -> dir.exists() || dir.mkdirs())
                .map(dir -> Uri.fromFile(new File(dir.getPath() + File.separator + System.currentTimeMillis() + ".jpeg")));
    }

    public Maybe<Uri> getTempPhotoFileUri(@NonNull File storeDir) {
        return Maybe.just(storeDir)
                .filter(dir -> dir.exists() || dir.mkdirs())
                .map(dir -> Uri.fromFile(new File(dir.getPath() + File.separator + AVATAR_FILE_NAME)));
    }

}
