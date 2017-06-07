package com.ride.util.common;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public final class  StringUtils {

    public StringUtils() {
        throw new UnsupportedOperationException("StringUtils can't be initialized");
    }

    /**
     * 判断字符串是否为null或全为空白字符
     *
     * @param str
     * @return
     */
    public static boolean isSpace(String str) {
        if (str == null) return true;
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
