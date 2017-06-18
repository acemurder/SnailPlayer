package com.ride.snailplayer.util.ucrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.ride.snailplayer.R;
import com.ride.util.common.util.Utils;
import com.yalantis.ucrop.UCrop;

/**
 * @author Stormouble
 * @since 2016/12/25.
 */

public class UCropClient {

    private final Context context;

    private final Bitmap.CompressFormat compressionFormat;
    private final int compressQuality;
    private final int aspectRatioX;
    private final int aspectRatioY;
    private final int maxResultWidth;
    private final int maxResultHeight;

    private UCrop mUCrop;

    public void newCrop(@NonNull Uri sourceUri, @NonNull Uri destinationUri,
                        @NonNull Activity activity) {
        newCrop(sourceUri, destinationUri, activity, null);
    }

    public void newCrop(@NonNull Uri sourceUri, @NonNull Uri destinationUri,
                        @NonNull Activity activity, Fragment fragment) {
        mUCrop = UCrop.of(sourceUri, destinationUri);

        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(Utils.getContext(), R.color.theme_primary));
        options.setStatusBarColor(ContextCompat.getColor(Utils.getContext(), R.color.theme_primary_dark));
        options.setActiveWidgetColor(ContextCompat.getColor(Utils.getContext(), R.color.theme_accent));
        options.setCompressionFormat(compressionFormat);
        options.setCompressionQuality(compressQuality);
        options.withAspectRatio(aspectRatioX, aspectRatioY);
        //options.withMaxResultSize(maxResultWidth, maxResultHeight);
        mUCrop.withOptions(options);

        if (fragment == null) {
            mUCrop.start(activity);
        } else {
            mUCrop.start(activity, fragment);
        }
    }

    public UCropClient(@NonNull Context context) {
        this(new Builder(context));
    }

    public UCropClient(@NonNull Builder builder) {
        this.context = builder.context;
        this.compressionFormat = builder.compressionFormat;
        this.compressQuality = builder.compressQuality;
        this.aspectRatioX = builder.aspectRatioX;
        this.aspectRatioY = builder.aspectRatioY;
        this.maxResultWidth = builder.maxResultWidth;
        this.maxResultHeight = builder.maxResultHeight;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        Context context;

        Bitmap.CompressFormat compressionFormat;
        int compressQuality;
        int aspectRatioX;
        int aspectRatioY;
        int maxResultWidth;
        int maxResultHeight;

        public Builder(@NonNull Context context) {
            this.context = context;
            compressionFormat = Bitmap.CompressFormat.JPEG;
            compressQuality = 90;
            aspectRatioX = 16;
            aspectRatioY = 9;
            maxResultWidth = 100;
            maxResultHeight = 100;
        }

        public Builder(UCropClient client) {
            context = client.context;
            compressionFormat = client.compressionFormat;
            compressQuality = client.compressQuality;
            aspectRatioX = client.aspectRatioX;
            aspectRatioY = client.aspectRatioY;
            maxResultWidth = client.maxResultWidth;
            maxResultHeight = client.maxResultHeight;
        }

        public Builder compressionFormat(@NonNull Bitmap.CompressFormat format) {
            this.compressionFormat = format;
            return this;
        }

        public Builder compressQuality(@IntRange(from = 0) int quality) {
            this.compressQuality = quality;
            return this;
        }

        public Builder maxResultWidth(@IntRange(from = 100) int maxResultWidth) {
            this.maxResultWidth = maxResultWidth;
            return this;
        }

        public Builder maxResultHeight(@IntRange(from = 100) int maxResultHeight) {
            this.maxResultHeight = maxResultHeight;
            return this;
        }

        public Builder aspectRatio(int aspectRatioX, int aspectRatioY) {
            this.aspectRatioX = aspectRatioX;
            this.aspectRatioY = aspectRatioY;
            return this;
        }

        public UCropClient build() {
            return new UCropClient(this);
        }
    }
}
