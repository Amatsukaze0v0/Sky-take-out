package com.skytakeout.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Util {

    // 使用SHA-256算法进行加密
    public static String hashPassword(String password){
        // 创建一个MessageDigest实例，使用SHA-256算法
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("STH wrong with the SHA process.");
            throw new RuntimeException(e);
        }

        // 获取密码的字节数组
        byte[] encodedhash = digest.digest(password.getBytes());

        // 将字节数组转换为十六进制格式字符串
        return bytesToHex(encodedhash);
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }

    // 将哈希结果进行Base64编码（可选，取决于需要的存储格式）
    public static String hashPasswordBase64(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(encodedhash);
    }

    public static void main(String[] args) {
        try {
            String password = "123456";

            // 获取SHA-256加密后的密码
            String hashedPassword = SHA256Util.hashPassword(password+"4b12980c219bc5b7f4c693cf08fbefcd");
            System.out.println("SHA-256 Hash: " + hashedPassword);

            // 获取Base64编码后的SHA-256加密密码
            String base64HashedPassword = SHA256Util.hashPasswordBase64(password);
            System.out.println("Base64 Encoded SHA-256 Hash: " + base64HashedPassword);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
