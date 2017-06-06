package com.ride.util.common;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public final class EncryptUtils {

    public EncryptUtils() {
        throw new UnsupportedOperationException("EncryptUtils can't be initialized");
    }

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * SHA1加密
     *
     * @param str
     * @return
     */
    public static String encryptWithSHA1(String str) {
        return encryptWithSHA1(str, "UTF-8");
    }

    /**
     * SHA1加密
     *
     * @param str
     * @param charsetName 字符集，例如:UTF-8
     * @return
     */
    public static String encryptWithSHA1(String str, String charsetName) {
        if (TextUtils.isEmpty(str))
            return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(str.getBytes(charsetName));
            return byteArrayToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * byteArray转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    public static String byteArrayToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        int len = bytes.length;
        char[] buf = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            buf[j++] = HEX_DIGITS[bytes[i] >>> 4 & 0x0f];
            buf[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(buf);
    }
}
