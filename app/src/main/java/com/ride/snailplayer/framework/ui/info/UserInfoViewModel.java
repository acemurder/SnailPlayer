package com.ride.snailplayer.framework.ui.info;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.ride.snailplayer.net.ApiClient;

import java.io.IOException;

import io.reactivex.Single;
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

    public Single<Bitmap> setAvatarForCircleImageView(@NonNull String url) {
        return Single.create(emitter -> {
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
                        emitter.onSuccess(bitmap);
                    } else {
                        emitter.onError(new Exception("头像bitmap下载失败"));
                    }
                }
            });
        });
    }
}
