package com.skytakeout.util;
import java.security.SecureRandom;

public class SaltGenerator {
    public static String generateSalt() {
        // 定义盐值的长度
        int saltLength = 16; // 例如，16字节的盐值
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        return bytesToHex(salt);
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateSalt());
    }
}