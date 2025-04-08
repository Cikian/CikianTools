package cn.cikian.dictionary;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 字符字典枚举实现 - 提供类型安全的字符集操作
 *
 * <p>使用示例：
 * <pre>{@code
 * // 获取大写字母+数字的组合字符集
 * Set<Character> charset = CharSet.combine(
 *     CharSet.UPPERCASE,
 *     CharSet.DIGITS
 * );
 *
 * // 生成排除易混淆字符的密码
 * String password = CharSet.generatePassword(12,
 *     CharSet.UPPERCASE.excludeAmbiguous(),
 *     CharSet.LOWERCASE.excludeAmbiguous(),
 *     CharSet.DIGITS.excludeAmbiguous()
 * );
 * }</pre>
 */
public enum CharSet {
    /** 大写字母 A-Z */
    UPPERCASE(range('A', 'Z')),

    /** 小写字母 a-z */
    LOWERCASE(range('a', 'z')),

    /** 数字 0-9 */
    DIGITS(range('0', '9')),

    /** 基础符号集 */
    SYMBOLS("!@#$%^&*()-_=+[]{}|;:,.<>/?".toCharArray()),

    /** 密码常用符号集 */
    COMMON_SYMBOLS("_-!@".toCharArray()),

    /** 十六进制小写 */
    HEX_LOWER(range('0','9','a','f')),

    /** 十六进制大写 */
    HEX_UPPER(range('0','9','A','F'));

    // 排除易混淆字符
    private static final String AMBIGUOUS = "0OIl1";
    private final char[] characters;

    CharSet(char[] chars) {
        this.characters = chars;
    }

    /**
     * 获取原始字符数组（防御性拷贝）
     */
    public char[] getCharacters() {
        return Arrays.copyOf(characters, characters.length);
    }

    /**
     * 组合多个字符集
     * @param sets 需要组合的字符集枚举
     */
    public static Set<Character> combine(CharSet... sets) {
        Set<Character> combined = new LinkedHashSet<>();
        for (CharSet set : sets) {
            for (char c : set.getCharacters()) {
                combined.add(c);
            }
        }
        return Collections.unmodifiableSet(combined);
    }

    // 字符范围生成（静态工具方法）
    private static char[] range(int... ranges) {
        Set<Character> chars = new LinkedHashSet<>();
        for (int i = 0; i < ranges.length; i += 2) {
            int start = ranges[i];
            int end = ranges[i+1];
            for (int c = start; c <= end; c++) {
                chars.add((char)c);
            }
        }
        return chars.stream()
                .map(c -> c.toString())
                .collect(Collectors.joining())
                .toCharArray();
    }

    // 过滤易混淆字符
    private static char[] filterCharacters(char[] original) {
        return new String(original).replaceAll("[" + AMBIGUOUS + "]", "")
                .toCharArray();
    }

}
