package com.ride.snailplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stormouble
 * @since 2016/12/25.
 */

public class Utils {

    private static final String TAG = "Utils";

    private static List<File> mTempFileList = new ArrayList<>();

    private Utils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 在内部存储中创建一个临时的图片文件
     *
     * @param context
     * @param format 图片格式
     * @return
     */
    public static File createTempPhotoFileLocal(@NonNull Context context, Bitmap.CompressFormat format) {
        File file = new File(context.getCacheDir()
                + File.separator + System.currentTimeMillis() + getPhotoFileSuffix(format));

        mTempFileList.add(file);

        return file;
    }

    /**
     * 在外部存储中创建一个临时的图片文件在
     *
     * @param context
     * @param format 图片格式
     * @return
     */
    public static File createTempPhotoFileInSdCard(@NonNull Context context, Bitmap.CompressFormat format) {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + System.currentTimeMillis() + getPhotoFileSuffix(format));
        mTempFileList.add(file);

        return file;
    }

    public static String getPhotoFileSuffix(Bitmap.CompressFormat format) {
        String fileSuffix = null;
        if (format == Bitmap.CompressFormat.JPEG) {
            fileSuffix = ".jpg";
        } else if (format == Bitmap.CompressFormat.PNG) {
            fileSuffix = ".png";
        } else {
            fileSuffix = ".webp";
        }
        return fileSuffix;
    }

    /**
     * 清理临时文件
     */
    public static void clean() {
        new CleanUpThread().start();
    }

    public static boolean equal(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    private static class CleanUpThread extends Thread {

        CleanUpThread() {
            super();
            setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            for (int i = 0; i < mTempFileList.size(); i++) {
                File file = mTempFileList.get(i);
                file.deleteOnExit();
            }
        }
    }
}
