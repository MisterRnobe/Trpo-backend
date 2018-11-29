package org.medvedev.nikita.services;

import io.jsonwebtoken.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.medvedev.nikita.objects.UserData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class);

    public static final long DAY = 24 * 60 * 60 * 1000;

    private static final byte[] SECRET = "SeCrEt".getBytes(StandardCharsets.UTF_8);

    public static String generateToken(@NotNull UserData userData) {
        logger.info("Generating token for: "+userData);
        return Jwts.builder()
                .setSubject("userdata")
                .setExpiration(new Date(System.currentTimeMillis() + DAY))
                .claim("login", userData.getLogin())
                .claim("email", userData.getEmail())
                .claim("first_name", userData.getFirstName())
                .claim("second_name", userData.getSecondName())
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static UserData decodeToken(@NotNull String token) {
        Jws<Claims> jwt = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token);
        Claims bodyClaims = jwt.getBody();
        return new UserData()
                .setLogin(bodyClaims.get("login").toString())
                .setEmail(bodyClaims.get("email").toString())
                .setFirstName(bodyClaims.get("first_name").toString())
                .setSecondName(bodyClaims.get("second_name").toString());
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
