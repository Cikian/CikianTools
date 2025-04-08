package cn.cikian.utils.encrypt;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 * @author Cikian
 * @since 2025/4/8 13:36
 * @version 1.0
 */

public class MD5Utils {

    /**
     * 使用MD5算法对字符串进行加密
     *
     * @param input 要加密的字符串
     * @return 32位MD5
     */
    public static String md5(String input) {
        try {
            // 1. 创建MD5加密器
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2. 将字符串转为字节数组（UTF-8编码）
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // 3. 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = String.format("%02x", b & 0xff);
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
