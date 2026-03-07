package com.Bit_Builder.x_ray.app.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class HashUtil {

    public String generateHash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }
}