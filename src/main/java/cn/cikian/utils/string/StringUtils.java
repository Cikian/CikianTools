package cn.cikian.utils.string;


import cn.cikian.dictionary.CharSet;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static cn.cikian.dictionary.CharSet.combine;

/**
 * 字符串工具类
 *
 * @author Cikian
 * @version 1.0
 * @since 2025/4/8 10:50
 */

public class StringUtils {

    /**
     * 生成随机字符串，大小写字母、数字、符号
     *
     * @param length 生成长度
     * @return 随机字符串
     */
    public static String getRandomStr(int length) {
        Set<Character> allChars = combine(
                CharSet.UPPERCASE,
                CharSet.LOWERCASE,
                CharSet.DIGITS,
                CharSet.SYMBOLS
        );

        Character[] charArray = allChars.toArray(new Character[0]);
        return ThreadLocalRandom.current()
                .ints(length, 0, charArray.length)
                .mapToObj(i -> charArray[i].toString())
                .collect(Collectors.joining());
    }

    /**
     * 生成随机字符串
     *
     * @param length      生成长度
     * @param excludedStr 排除的字符
     * @return 随机字符串
     */
    public static String getRandomStr(int length, String excludedStr) {
        Set<Character> allChars = new java.util.HashSet<>(combine(
                CharSet.UPPERCASE,
                CharSet.LOWERCASE,
                CharSet.DIGITS,
                CharSet.SYMBOLS
        ));

        allChars.removeAll(excludedStr.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));

        Character[] charArray = allChars.toArray(new Character[0]);
        return ThreadLocalRandom.current()
                .ints(length, 0, charArray.length)
                .mapToObj(i -> charArray[i].toString())
                .collect(Collectors.joining());
    }

    /**
     * 生成随机字符串
     *
     * @param length  生成长度
     * @param baseStr 候选字符集
     * @return 随机字符串
     */
    public static String getRandomStrWithCustomStr(int length, String baseStr) {
        Character[] charArray = baseStr.chars().mapToObj(c -> (char) c).distinct().toArray(Character[]::new);
        return ThreadLocalRandom.current()
                .ints(length, 0, charArray.length)
                .mapToObj(i -> charArray[i].toString())
                .collect(Collectors.joining());
    }

    /**
     * 将大写字母转为小写
     *
     * @param str 字符串
     * @return 小写字符串
     */
    public static String toLowerCase(String str) {
        return str.toLowerCase();
    }

    /**
     * 将小写字母转为大写
     *
     * @param str 字符串
     * @return 大写字符串
     */
    public static String toUpperCase(String str) {
        return str.toUpperCase();
    }

    /**
     * 去除字符串中的字母
     *
     * @param str 字符串
     * @return 去除字母后的字符串
     */
    public static String removeLetter(String str) {
        return str.replaceAll("[a-zA-Z]", "");
    }

    /**
     * 去除字符串中的数字
     *
     * @param str 字符串
     * @return 去除数字后的字符串
     */
    public static String removeDigit(String str) {
        return str.replaceAll("[0-9]", "");
    }

    /**
     * 去除字符串中的符号
     *
     * @param str 字符串
     * @return 去除符号后的字符串
     */
    public static String removeSymbol(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * 去除字符串中的空格
     *
     * @param str 字符串
     * @return 去除空格后的字符串
     */
    public static String removeSpace(String str) {
        return str.replaceAll("\\s", "");
    }

    /**
     * 去除字符串中的特殊字符
     *
     * @param str 字符串
     * @return 去除特殊字符后的字符串
     */
    public static String removeSpecialChar(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * 下划线命名转小驼峰
     *
     * @param str 字符串
     * @return 小驼峰命名后的字符串
     */
    public static String underlineToCamel(String str) {
        if (str == null) {
            return null;
        }
        String[] parts = str.split("_+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            if (i == 0) {
                sb.append(parts[i].toLowerCase());
            } else {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    sb.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 下划线命名转大驼峰
     *
     * @param str 字符串
     * @return 大驼峰命名后的字符串
     */
    public static String underlineToPascal(String str) {
        if (str == null) {
            return null;
        }
        String[] parts = str.split("_+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰命名转下划线
     *
     * @param str 字符串
     * @return 下划线命名后的字符串
     */
    public static String camelToUnderline(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否只包含数字
     *
     * @param str 字符串
     * @return 是否只包含数字
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}
