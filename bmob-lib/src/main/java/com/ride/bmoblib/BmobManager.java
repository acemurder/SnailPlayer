package com.ride.bmoblib;

/**
 * @author Stormouble
 * @since 2017/6/12.
 */

public class BmobManager {

    private volatile static BmobManager sInstance;

    public static BmobManager getInstance() {
        if (sInstance == null) {
            synchronized (BmobManager.class) {
                if (sInstance == null) {
                    sInstance = new BmobManager();
                }
            }
        }
        return sInstance;
    }


}
