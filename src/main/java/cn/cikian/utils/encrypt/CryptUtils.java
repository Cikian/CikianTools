package cn.cikian.utils.encrypt;


import org.bouncycastle.crypto.generators.SCrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Crypt加密工具
 *
 * @author Cikian
 * @since 2025/4/8 14:00
 * @version 1.0
 */

public class CryptUtils {

    // 常量
    private static final int SALT_LENGTH = 8;  // 盐值长度（16字节=128位）
    private static final int KEY_LENGTH = 32;   // 密钥长度（32字节=256位）
    private static final int N = 16384;         // SCrypt参数
    private static final int r = 8;
    private static final int p = 1;
    private static final int HASH_LENGTH = 56; // 输出哈希长度

    /**
     * SCrypt加密
     * @param password 待加密字符串
     * @return Base64编码的存储哈希
     */
    public static String SCrypt(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        // 生成随机盐
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        // 计算哈希
        byte[] hash = SCrypt.generate(
                password.getBytes(),
                salt,
                N, r, p, HASH_LENGTH
        );

        // 合并盐和哈希（盐在前，哈希在后）
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(hash, 0, combined, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 验证密码
     * @param password 待验证明文密码
     * @param storedHash 加密后的密码
     * @return boolean 验证结果
     */
    public static boolean verify(String password, String storedHash) {
        if (password == null || password.isEmpty() || storedHash == null || storedHash.isEmpty()) {
            throw new IllegalArgumentException("密码和存储哈希不能为空");
        }
        
        // 解码Base64获取完整数据
        byte[] combined;
        try {
            combined = Base64.getDecoder().decode(storedHash);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的Base64编码存储哈希", e);
        }

        // 正确分离盐和哈希
        byte[] salt = extractSalt(combined);
        byte[] originalHash = extractHash(combined);

        // 重新计算哈希
        byte[] testHash = SCrypt.generate(
                password.getBytes(),
                salt,
                N, r, p, HASH_LENGTH
        );

        return MessageDigest.isEqual(originalHash, testHash);
    }

    /**
     * 生成加密哈希并返回密钥
     * @param password 待加密字符串
     * @return 数组：[0]=Base64编码的存储哈希，[1]=Base64编码的密钥
     */
    public static String[] SCryptWithKey(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        try {
            // 生成随机盐
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            // 生成随机密钥（256位）
            byte[] keyBytes = new byte[KEY_LENGTH];
            new SecureRandom().nextBytes(keyBytes);
            String encodedKey = Base64.getEncoder().encodeToString(keyBytes);

            // 使用HMAC-SHA256绑定密钥和密码
            SecretKeySpec hmacKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(hmacKey);
            byte[] hmacDigest = hmac.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // 计算SCrypt哈希
            byte[] scryptHash = SCrypt.generate(hmacDigest, salt, N, r, p, HASH_LENGTH);

            // 合并盐和哈希
            byte[] combined = new byte[SALT_LENGTH + HASH_LENGTH];
            System.arraycopy(salt, 0, combined, 0, SALT_LENGTH);
            System.arraycopy(scryptHash, 0, combined, SALT_LENGTH, HASH_LENGTH);

            return new String[] {
                    Base64.getEncoder().encodeToString(combined),
                    encodedKey
            };

        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 验证三因素（密码、存储哈希、密钥）
     *
     * @param password 密码
     * @param storedHash 存储哈希
     * @param key Base64编码的密钥
     *
     * @return boolean 是否验证成功
     */
    public static boolean verifyWithKey(String password, String storedHash, String key) {
        if (password == null || password.isEmpty() || storedHash == null || storedHash.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("密码、存储哈希和密钥不能为空");
        }
        
        try {
            // 解码输入数据
            byte[] combined;
            byte[] keyBytes;
            try {
                combined = Base64.getDecoder().decode(storedHash);
                keyBytes = Base64.getDecoder().decode(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的Base64编码数据", e);
            }

            // 提取盐和原始哈希
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            byte[] originalHash = new byte[HASH_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, originalHash, 0, HASH_LENGTH);

            // 计算HMAC绑定密钥和密码
            SecretKeySpec hmacKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(hmacKey);
            byte[] hmacDigest = hmac.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // 重新生成哈希进行对比
            byte[] testHash = SCrypt.generate(hmacDigest, salt, N, r, p, HASH_LENGTH);
            return MessageDigest.isEqual(originalHash, testHash);

        } catch (Exception e) {
            throw new RuntimeException("验证失败", e);
        }
    }








    // 提取盐值
    private static byte[] extractSalt(byte[] combined) {
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
        return salt;
    }

    // 提取哈希值
    private static byte[] extractHash(byte[] combined) {
        byte[] hash = new byte[combined.length - SALT_LENGTH];
        System.arraycopy(combined, SALT_LENGTH, hash, 0, hash.length);
        return hash;
    }
}
