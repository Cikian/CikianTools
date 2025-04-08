package cn.cikian.dictionary;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @className: CharDictionary
 * @author: Cikian
 * @date: 2025/4/8 11:03
 * @Version: 1.0
 * @description: 字典
 */


/**
 * 字符字典工具类 - 提供预定义字符集和动态组合功能
 *
 * <p>使用示例：
 * <pre>{@code
 * // 获取大写字母+数字的组合字符集
 * char[] chars = CharDictionary.builder()
 *     .uppercase()
 *     .digits()
 *     .build();
 *
 * // 生成8位随机字符串（大写字母+数字）
 * String random = CharDictionary.random(8, chars);
 * }</pre>
 */
public class CharDictionary {

    // 预定义基础字符集
    public static final char[] UPPERCASE = range('A', 'Z');
    public static final char[] LOWERCASE = range('a', 'z');
    public static final char[] DIGITS = range('0', '9');
    public static final char[] SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>/?".toCharArray();
    public static final char[] HEX_LOWER = range('0', '9', 'a', 'f');
    public static final char[] HEX_UPPER = range('0', '9', 'A', 'F');

    // 排除易混淆字符
    private static final String AMBIGUOUS = "0OIl1";

    /**
     * 字符范围生成器
     *
     * @param starts 连续范围的起始字符对
     * @return 合并后的字符数组
     */
    public static char[] range(int... starts) {
        Set<Character> chars = new LinkedHashSet<>();
        for (int i = 0; i < starts.length; i += 2) {
            int start = starts[i];
            int end = starts[i + 1];
            IntStream.rangeClosed(start, end)
                    .mapToObj(c -> (char) c)
                    .forEach(chars::add);
        }
        return toArray(chars);
    }

    /**
     * 创建字符集构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 字符集构建器（支持链式调用）
     */
    public static class Builder {
        private final Set<Character> chars = new LinkedHashSet<>();
        private boolean excludeAmbiguous = false;

        public Builder uppercase() {
            return add(UPPERCASE);
        }

        public Builder lowercase() {
            return add(LOWERCASE);
        }

        public Builder digits() {
            return add(DIGITS);
        }

        public Builder symbols() {
            return add(SYMBOLS);
        }

        public Builder add(char[] chars) {
            for (char c : chars) {
                this.chars.add(c);
            }
            return this;
        }

        public Builder excludeAmbiguous() {
            this.excludeAmbiguous = true;
            return this;
        }

        /**
         * 构建最终字符数组
         */
        public char[] build() {
            Set<Character> finalSet = new LinkedHashSet<>(chars);
            if (excludeAmbiguous) {
                finalSet.removeAll(AMBIGUOUS.chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.toSet()));
            }
            return toArray(finalSet);
        }
    }

    /**
     * 生成随机字符串
     *
     * @param length 字符串长度
     * @param chars  候选字符集
     */
    public static String random(int length, char[] chars) {
        if (chars == null || chars.length == 0) {
            throw new IllegalArgumentException("字符集不能为空");
        }
        if (length < 1) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        return ThreadLocalRandom.current()
                .ints(length, 0, chars.length)
                .map(i -> chars[i])
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // 辅助方法：Set转char[]
    private static char[] toArray(Set<Character> chars) {
        return chars.stream()
                .map(c -> c.toString())
                .collect(Collectors.joining())
                .toCharArray();
    }
}
