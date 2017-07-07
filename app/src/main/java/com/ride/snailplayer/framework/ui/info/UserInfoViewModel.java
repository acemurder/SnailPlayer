package com.ride.snailplayer.framework.ui.info;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ride.snailplayer.framework.base.model.User;
import com.ride.snailplayer.net.ApiClient;

import java.io.IOException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Stormouble
 * @since 2017/7/7.
 */

public class UserInfoViewModel extends ViewModel {

    public UserInfoViewModel() {
    }

    public Observable<String> isUserNicknameChanged(User user, @NonNull String currentNickname) {
        return Observable.just(currentNickname)
                .filter(nickname -> {
                    if (user != null) {
                        if (user.getNickName() != null) {
                            return !user.getNickName().equals(nickname);
                        }
                    }
                    return false;
                });
    }

    public Observable<String> isUserSignChanged(User user, @NonNull String currentSign) {
        return Observable.just(currentSign)
                .filter(sign -> {
                    if (user != null) {
                        if (user.getSign() != null) {
                            return !user.getSign().equals(sign);
                        }
                    }
                    return false;
                });
    }

    public Observable<String> isUserBirthdayChanged(User user, @NonNull String newBirthday) {
        return Observable.just(newBirthday)
                .filter(birthday -> user != null && !birthday.equals(user.getBirthday()));
    }


    public Observable<User> updateUserNickname(User user, String nickname) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(nickname)) {
                User newUser = new User();
                newUser.setNickName(nickname);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(user);
                            emitter.onComplete();
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("user=" + user + ", nickname=" + nickname));
            }
        });
    }

    public Observable<User> updateUserSign(User user, String sign) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(sign)) {
                User newUser = new User();
                newUser.setSign(sign);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(user);
                            emitter.onComplete();
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("user=" + user + ", sign=" + sign));
            }
        });
    }

    public Observable<String> updateUserBirthday(User user, String birthday) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(birthday)) {
                User newUser = new User();
                newUser.setBirthday(birthday);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(birthday);
                            emitter.onComplete();
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("user=" + user + ", birthday=" + birthday));
            }
        });
    }
}
