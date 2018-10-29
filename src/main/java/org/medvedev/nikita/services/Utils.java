package org.medvedev.nikita.services;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static final long WEEK = 7 * 24 * 60 * 60 * 1000;
    public static String generateToken(@NotNull Object o)
    {
        return sha256(o.toString()+ System.currentTimeMillis());
    }
    public static String sha256(@NotNull String data) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
    private static String bytesToHex(@NotNull byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
