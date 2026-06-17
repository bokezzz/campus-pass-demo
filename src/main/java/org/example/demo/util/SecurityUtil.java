package org.example.demo.util;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 安全相关工具类。
 *
 * <p>本系统的安全要求主要来自任务书中的“等保三级和国密算法”部分：
 * 后台密码复杂度校验、SM3 摘要保存、身份证号和手机号脱敏显示等，
 * 都集中放在这个类中，便于复用和答辩说明。</p>
 */
public final class SecurityUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private SecurityUtil() {
    }

    /**
     * 判断后台管理员密码是否满足复杂度要求。
     *
     * <p>规则：长度至少 8 位，同时包含小写字母、大写字母、数字和特殊字符。
     * 这对应任务书中“密码复杂度要求”的身份鉴别部分。</p>
     */
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

    /**
     * 使用 BouncyCastle 提供的 SM3Digest 计算国密 SM3 摘要。
     *
     * <p>注意：这里返回的是 64 位十六进制字符串。数据库中保存该摘要，
     * 登录时再对用户输入的密码计算 SM3 后比对，避免保存明文密码。</p>
     */
    public static String sm3(String text) {
        byte[] input = text == null ? new byte[0] : text.getBytes(StandardCharsets.UTF_8);
        SM3Digest digest = new SM3Digest();
        digest.update(input, 0, input.length);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);
        return Hex.toHexString(output);
    }

    /**
     * 生成通行码编号。
     *
     * <p>格式为 CP + 当前时间 + 6 位随机数，既能体现生成时间，
     * 又能降低短时间内重复的概率。</p>
     */
    public static String generatePassCode() {
        int number = 100000 + RANDOM.nextInt(900000);
        return "CP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + number;
    }

    /**
     * 姓名脱敏。
     *
     * <p>任务书要求二维码信息中的姓名中间用 * 代替。
     * 这里保留第一个字，其余全部替换为 *，例如“王小明”显示为“王**”。</p>
     */
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

    /**
     * 身份证号脱敏。
     *
     * <p>页面和二维码中不直接暴露完整身份证号，保留前 3 位和后 3 位，
     * 中间使用 * 代替，体现隐私保护要求。</p>
     */
    public static String maskIdentityNo(String identityNo) {
        if (identityNo == null || identityNo.length() < 6) {
            return "";
        }
        int tail = Math.min(3, identityNo.length() - 3);
        return identityNo.substring(0, 3) + "*".repeat(identityNo.length() - 3 - tail) + identityNo.substring(identityNo.length() - tail);
    }

    /**
     * 手机号脱敏。
     *
     * <p>常见展示方式是保留前三位和后四位，中间四位用星号替代，
     * 例如 13812345678 显示为 138****5678。</p>
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
