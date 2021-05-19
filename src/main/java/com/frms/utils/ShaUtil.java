package com.frms.utils;

import java.security.MessageDigest;

public class ShaUtil {
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String sha256Str = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            sha256Str = byte2hex(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha256Str;
    };
    public static String byte2hex(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }
        return builder.toString().toUpperCase();
    };
}
