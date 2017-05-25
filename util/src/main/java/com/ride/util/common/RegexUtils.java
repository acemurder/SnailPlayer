package com.ride.util.common;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * @author Stormouble
 * @since 2016/12/21.
 */

public final class RegexUtils {

    private RegexUtils() {
        throw new UnsupportedOperationException("RegexUtils can't be instantiated");
    }

    /**
     * 验证输入username长度 (2-20位)
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isCorrectUserAccount(@NonNull String str) {

        return str.length() >= 2 && str.length() <= 20;
    }

    /**
     * 验证输入password长度 (6-18位)
     *
     * @param str
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsCorrectUserPassword(@NonNull String str) {

        return str.length() >= 6 && str.length() <= 18;
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>
     *               移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）
     *               </p>
     *               <p>
     *               联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）
     *               </p>
     *               <p>
     *               电信的号段：133、153、180（未启用）、189
     *               </p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }
}