package com.ride.snailplayer.framework.ui.info;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ride.snailplayer.framework.base.model.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;

/**
 * @author Stormouble
 * @since 2017/7/7.
 */

public class UserInfoViewModel extends ViewModel {

    public UserInfoViewModel() {
    }

    public Observable<String> isUserNicknameChanged(User user, @NonNull String newNickname) {
        return Observable.just(newNickname)
                .filter(nickname -> user != null && !nickname.equals(user.getNickName()));
    }

    public Observable<String> isUserSignChanged(User user, @NonNull String newSign) {
        return Observable.just(newSign)
                .filter(sign -> user != null && !sign.equals(user.getSign()));
    }

    public Observable<String> isUserBirthdayChanged(User user, @NonNull String newBirthday) {
        return Observable.just(newBirthday)
                .filter(birthday -> user != null && !birthday.equals(user.getBirthday()));
    }

    public Observable<String> isUserSexChanged(User user, @NonNull String newSex) {
        return Observable.just(newSex)
                .filter(sex -> user != null && !sex.equals(user.getSex()));
    }

    public Observable<String> updateUserNickname(User user, String nickname) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(nickname)) {
                User newUser = new User();
                newUser.setNickName(nickname);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(nickname);
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

    public Observable<String> updateUserSign(User user, String sign) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(sign)) {
                User newUser = new User();
                newUser.setSign(sign);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(sign);
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

    public Observable<String> updateUserSex(User user, String sex) {
        return Observable.create(emitter -> {
            if (user != null && !TextUtils.isEmpty(sex)) {
                User newUser = new User();
                newUser.setSex(sex);
                newUser.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            emitter.onNext(sex);
                            emitter.onComplete();
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            } else {
                emitter.onError(new Exception("user=" + user + ", sex=" + sex));
            }
        });
    }
}
