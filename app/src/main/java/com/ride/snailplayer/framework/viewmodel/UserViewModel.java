package com.ride.snailplayer.framework.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.ride.snailplayer.framework.base.model.User;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.reactivex.Observable;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public class UserViewModel extends ViewModel {

    private User mUser;

    public UserViewModel() {
        mUser = BmobUser.getCurrentUser(User.class);
    }

    public User getUser() {
        return mUser;
    }

    public Observable<String> updateUserAvatar(@NonNull String url) {
        return Observable.create(emitter -> {
            if (mUser != null) {
                User newUser = new User();
                newUser.setAvatarUrl(url);
                newUser.update(mUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(url);
                            emitter.onComplete();
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("user == null"));
            }
        });
    }

    public Observable<String> uploadUserAvatar(@NonNull Uri avatarUri) {
        return Observable.create(emitter -> {
            BmobFile bmobFile = new BmobFile(new File(avatarUri.getPath()));
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        emitter.onNext(bmobFile.getFileUrl());
                        emitter.onComplete();
                    } else {
                        emitter.onError(e);
                    }
                }
            });
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
