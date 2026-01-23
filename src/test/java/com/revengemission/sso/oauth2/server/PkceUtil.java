package com.revengemission.sso.oauth2.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PKCE (Proof Key for Code Exchange) 工具类
 * 生成符合 OAuth 2.0 规范的 code_verifier 和 code_challenge
 */
public class PkceUtil {

    // code_verifier 长度范围：43~128 字符（推荐64字符，此处用96字节随机数生成约128字符的verifier）
    private static final int VERIFIER_BYTE_LENGTH = 96;

    /**
     * 生成符合 PKCE 规范的 code_verifier
     * 要求：43~128字符，仅包含[a-zA-Z0-9-._~]（URL安全字符）
     *
     * @return 合规的 code_verifier
     */
    public static String generateCodeVerifier() {
        // 1. 生成加密安全的随机字节
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[VERIFIER_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);

        // 2. 进行URL安全的Base64编码（自动符合字符集要求），并去掉末尾的=
        String codeVerifier = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes);

        // 验证长度（避免极端情况）
        if (codeVerifier.length() < 43 || codeVerifier.length() > 128) {
            throw new RuntimeException("生成的code_verifier长度不符合PKCE规范");
        }

        return codeVerifier;
    }

    /**
     * 根据 code_verifier 生成 code_challenge（推荐S256算法）
     *
     * @param codeVerifier 已生成的 code_verifier
     * @return 合规的 code_challenge
     * @throws NoSuchAlgorithmException SHA-256 算法不存在（理论上不会发生）
     */
    public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        // 1. 对 code_verifier 进行 SHA-256 哈希
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));

        // 2. URL安全的Base64编码，去掉末尾的=
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(digest);
    }

    // 测试方法
    public static void main(String[] args) {
        try {
            // 生成 code_verifier
            String codeVerifier = generateCodeVerifier();
            System.out.println("✅ 生成的 code_verifier:");
            System.out.println(codeVerifier);

            // 生成对应的 code_challenge
            String codeChallenge = generateCodeChallenge(codeVerifier);
            System.out.println("\n✅ 生成的 code_challenge (S256算法):");
            System.out.println(codeChallenge);

            System.out.println("\n📌 code_challenge_method: S256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ 算法异常：" + e.getMessage());
        }
    }
}
