package org.example.demo.util;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SecurityUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private SecurityUtil() {
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean lower = false;
        boolean upper = false;
        boolean digit = false;
        boolean special = false;
        for (char c : password.toCharArray()) {
            lower |= Character.isLowerCase(c);
            upper |= Character.isUpperCase(c);
            digit |= Character.isDigit(c);
            special |= !Character.isLetterOrDigit(c);
        }
        return lower && upper && digit && special;
    }

    public static String sm3(String text) {
        byte[] input = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        SM3Digest digest = new SM3Digest();
        digest.update(input, 0, input.length);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);
        return Hex.toHexString(output);
    }

    public static String generatePassCode() {
        int number = 100000 + RANDOM.nextInt(900000);
        return "CP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + number;
    }

    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        String trimmed = name.trim();
        if (trimmed.length() == 1) {
            return trimmed + "*";
        }
        return trimmed.charAt(0) + "*".repeat(trimmed.length() - 1);
    }

    public static String maskIdentityNo(String identityNo) {
        if (identityNo == null || identityNo.length() < 6) {
            return "";
        }
        int tail = Math.min(3, identityNo.length() - 3);
        return identityNo.substring(0, 3) + "*".repeat(identityNo.length() - 3 - tail) + identityNo.substring(identityNo.length() - tail);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
