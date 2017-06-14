package com.ride.snailplayer.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ride.util.common.util.Utils;

/**
 * @author Stormouble
 * @since 2017/6/14.
 */

public class SettingUtils {

    public static final String PREF_USER_LOGIN = "pref_user_login";

    public static final String PREF_ALREADY_DISPLAY_WELCOME = "pref_already_display_welcome";

    public SettingUtils() {
        throw new UnsupportedOperationException("SettingUtils can't be initialized");
    }

    public static void markUserLogin(boolean isLogin) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        sp.edit().putBoolean(PREF_USER_LOGIN, isLogin).apply();
    }

    public static boolean isUserLogin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        return sp.getBoolean(PREF_USER_LOGIN, false);
    }

    public static void markAlreadyDisplayWelcome(boolean displayed) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        sp.edit().putBoolean(PREF_ALREADY_DISPLAY_WELCOME, displayed).apply();
    }

    public static boolean isAlreadyDisplayWelcome() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Utils.getContext());
        return sp.getBoolean(PREF_ALREADY_DISPLAY_WELCOME, false);
    }
}
