package cn.cikian.utils.date;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 日期工具类
 *
 * @author Cikian
 * @version 1.0
 * @since 2025/4/8 16:23
 */

public class DateUtils {


    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE =
            new ConcurrentHashMap<>();

    /**
     * LocalDateTime日期转字符串<br>
     * 转换为指定格式
     *
     * @param dateTime 日期
     * @param pattern  格式
     * @return 字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        Objects.requireNonNull(dateTime, "dateTime不能为null");
        validatePattern(pattern);

        return FORMATTER_CACHE
                .computeIfAbsent(pattern, DateTimeFormatter::ofPattern)
                .format(dateTime);
    }

    /**
     * LocalDateTime日期转字符串<br>
     * 转换为yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime 日期
     * @return 字符串
     */
    public static String format(LocalDateTime dateTime) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return format(dateTime, pattern);
    }

    /**
     * Date日期转字符串<br>
     * 转换为yyyy-MM-dd HH:mm:ss格式
     *
     * @param dateTime 日期
     * @return 字符串
     */
    public static String format(Date dateTime) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return format(dateTime, pattern);
    }

    /**
     * Date日期转字符串<br>
     * 转换为指定格式
     *
     * @param dateTime 日期
     * @return 字符串
     */
    public static String format(Date dateTime, String pattern) {
        LocalDateTime localDateTime = dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return format(localDateTime, pattern);
    }

    /**
     * 通过Date获取时间戳
     *
     * @param date 日期
     * @return 13位时间戳
     */
    public static long getTimestamp(Date date) {
        return date.getTime();
    }

    /**
     * 通过LocalDateTime获取时间戳
     *
     * @param date 日期
     * @return 13位时间戳
     */
    public static long getTimestamp(LocalDateTime date) {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将字符串转为LocalDate
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        validatePattern(pattern);
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串转为LocalDateTime
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateStr, String pattern) {
        if (!pattern.contains("H") && !pattern.contains("h") && !pattern.contains("m") && !pattern.contains("s")) {
            throw new RuntimeException("日期格式必须包含年月日 时分秒信息");
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            throw new RuntimeException("日期格式不正确，请检查日期与格式化字符串格式是否匹配！");
        }
    }

    /**
     * 计算两个日期的天数差LocalDate
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期的天数差LocalDateTime
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期的月数差LocalDate
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 月数差
     */
    public static long monthsBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * 计算两个日期的月数差LocalDateTime
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 月数差
     */
    public static long monthsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * 时区转换
     *
     * @param localTime  日期时间
     * @param sourceZone 源时区
     * @param targetZone 目标时区
     * @return 转换后的日期时间
     */
    public static ZonedDateTime convertTimezone(LocalDateTime localTime, ZoneId sourceZone, ZoneId targetZone) {
        return localTime.atZone(sourceZone).withZoneSameInstant(targetZone);
    }

    /**
     * 获取指定日期范围内的所有日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getDateRange(LocalDate start, LocalDate end) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.toList());
    }


    private static void validatePattern(String pattern) {
        try {
            DateTimeFormatter.ofPattern(pattern);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("无效的日期格式: " + pattern);
        }
    }
}
