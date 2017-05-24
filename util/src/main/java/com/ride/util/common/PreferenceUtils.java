package com.ride.util.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public final class PreferenceUtils {

    public static final String DEFAULT_FILE_NAME = "share_data";

    private PreferenceUtils() {
        throw new UnsupportedOperationException("PreferenceUtils can't be instantiated");
    }

    public static void set(String key, Object object) {
        set(key, object, DEFAULT_FILE_NAME);
    }

    public static void set(String key, Object object, String fileName) {
        SharedPreferences sp = Utils.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        editor.apply();
    }

    public static Object get(String key) {
        return get(key, 0);
    }

    public static Object get(String key, Object defaultObject) {
        return get(key, defaultObject, DEFAULT_FILE_NAME);
    }

    public static Object get(String key, Object defaultObject, String fileName) {
        SharedPreferences sp = Utils.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static SharedPreferences getPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
}
