package com.ride.snailplayer.framework.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.net.ApiClient;
import com.ride.util.common.util.RegexUtils;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Stormouble
 * @since 2017/6/17.
 */

public class UserViewModel extends ViewModel {

    public UserViewModel() {
    }

    public User getUser() {
        return BmobUser.getCurrentUser(User.class);
    }

    public Observable<String> updateUserAvatar(@NonNull String url) {
        return Observable.create(emitter -> {
            User user = getUser();
            if (user != null) {
                User newUser = new User();
                newUser.setAvatarUrl(url);
                newUser.update(user.getObjectId(), new UpdateListener() {
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

    public Observable<Bitmap> setAvatarForCircleImageView(@NonNull String url) {
        return Observable.create(emitter -> {
            if (!TextUtils.isEmpty(url) && RegexUtils.isURL(url)) {
                OkHttpClient client = ApiClient.IQIYI.getOkHttpClient();
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.isSuccessful()) {
                            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            emitter.onNext(bitmap);
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Exception("头像bitmap下载失败"));
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("url错误，url=" + url));
            }
        });
    }
}
