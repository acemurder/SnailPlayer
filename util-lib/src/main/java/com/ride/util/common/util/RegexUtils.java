package com.ride.util.common.util;

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
     * 验证用户账号，字母开头，允许5-16字节，允许字母数字下划线
     *
     * @param account
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isCorrectUserAccount(@NonNull String account) {
        return account.length() > 4;
    }

    /**
     * 验证用户密码，以字母开头，长度在6~18之间，只能包含字母、数字和下划线
     *
     * @param password
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsCorrectUserPassword(@NonNull String password) {
        String regex = "^[a-zA-Z]\\w{5,18}$";
        return Pattern.matches(regex, password);
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

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回falsea
     */
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
}
