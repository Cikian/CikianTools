package cn.cikian.utils.http.enitry;

import cn.cikian.code.ErrorCode;
import cn.cikian.exception.CikException;
import cn.cikian.utils.file.FileUtils;
import cn.cikian.utils.string.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:42
 */

public class JSON {
    private Map<String, JSONObj> json = new HashMap<>();

    public String get(String key) {
        JSONObj jsonObj = json.get(key);
        if (jsonObj == null) {
            return null;
        }
        Object v = jsonObj.get();
        if (v.toString().startsWith("{") && v.toString().endsWith("}")) {
            throw new CikException(ErrorCode.FAIL.code(), "这个key返回的是一个JSON对象，请使用getJSON方法！");
        }
        if (v.toString().startsWith("[") && v.toString().endsWith("]")) {
            throw new CikException(ErrorCode.FAIL.code(), "这个key返回的是一个JSON数组，请使用getArray方法！");
        }
        return null;
    }

    public JSON getJSON(String key) {
        JSONObj jsonObj = json.get(key);
        if (jsonObj == null) {
            return null;
        }
        Object v = jsonObj.get();
        if (v.toString().startsWith("[") && v.toString().endsWith("]")) {
            throw new CikException(ErrorCode.FAIL.code(), "这个key返回的是一个JSON数组，请使用getArray方法！");
        }

        if (!v.toString().startsWith("{") && !v.toString().endsWith("}")) {
            throw new CikException(ErrorCode.FAIL.code(), "这个key返回的不是JSON对象，请使用get方法！");
        }

        return new JSON(v.toString());

    }

    public JSON(String v) {
        parse(v);
    }

    private Map<String, JSONObj> parse(Object o) {
        if (o == null) return null;

        String jsonStr = o.toString().trim();
        if (!jsonStr.startsWith("{") || !jsonStr.endsWith("}")) {
            throw new CikException(ErrorCode.FAIL.code(), "JSON格式错误！");
        }

        jsonStr = jsonStr.substring(1, jsonStr.length() - 1);

        int depth = 0;
        boolean inString = false;
        boolean isKey = true;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();

        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);

            // 处理转义字符
            if (c == '\\' && i < jsonStr.length() - 1) {
                currentValue.append(c).append(jsonStr.charAt(++i));
                continue;
            }

            // 处理字符串状态
            if (c == '"') {
                inString = !inString;
                if (isKey && !inString) { // 键结束
                    currentKey = new StringBuilder(currentValue);
                    currentValue.setLength(0);
                }
            }

            if (!inString) {
                // 处理括号深度
                if (c == '{' || c == '[') depth++;
                if (c == '}' || c == ']') depth--;

                // 遇到冒号且不在嵌套中时切换为值模式
                if (depth == 0 && c == ':' && isKey) {
                    isKey = false;
                    currentValue.setLength(0);
                    // 跳过冒号后的空白字符
                    while (i + 1 < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(i + 1))) {
                        i++;
                    }
                    continue;
                }

                // 遇到逗号或结束符时保存键值对
                // 加强结束符检测，确保完整捕获数值
                // 加强结束符检测，确保捕获到数值末尾
                if ((depth == 0 && (c == ',' || c == '}' || (i == jsonStr.length() - 1 && !inString)) && !isKey)) {
                    if (currentKey.length() > 0 && currentValue.length() > 0) {
                        String key = currentKey.toString().trim();
                        String value = currentValue.toString().trim();

                        // 去除键的引号
                        if (key.startsWith("\"") && key.endsWith("\"")) {
                            key = key.substring(1, key.length() - 1);
                        }

                        // 智能处理值类型
                        if (value.startsWith("{") || value.startsWith("[")) {
                            // 保留完整对象/数组结构
                        } else if (value.startsWith("\"") && value.endsWith("\"")) {
                            // 保留原始数值格式
                            if (value.matches("^\".*\"$") && !value.matches("^-?\\d+(\\.\\d+)?$")) {
                                value = value.substring(1, value.length() - 1);
                            }
                        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                            // 保留数字原始格式
                        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                            // 保留布尔值原始格式
                        }

                        this.put(key, value);
                        currentKey.setLength(0);
                        currentValue.setLength(0);
                        isKey = true;
                    }
                    continue;
                }
            }

            // 收集字符
            if (!isKey || (isKey && (inString || !Character.isWhitespace(c)))) {
                if (!Character.isWhitespace(c) || inString) {
                    // 确保收集最后一个字符
                    if (i == jsonStr.length() - 1 && !Character.isWhitespace(c)) {
                        currentValue.append(c);
                        // 立即处理末尾数字字符
                        if (Character.isDigit(c) && !isKey) {
                            saveKeyValue(currentKey, currentValue);
                            currentValue.setLength(0);
                        }
                        // 立即处理最后一个数字字符
                        // 立即处理连续数字结尾
                        if (Character.isDigit(c) && !isKey) {
                            if (i == jsonStr.length() - 1) {
                                saveKeyValue(currentKey, currentValue);
                                currentValue.setLength(0);
                            }
                        }
                        
                        // 立即处理数值结尾字符
                        if (!isKey && (currentValue.toString().matches("-?\\d+$") || c == '0')) {
                            saveKeyValue(currentKey, currentValue);
                            currentValue.setLength(0);
                        }
                        
                        // 处理最后一个键值对
                        if (!isKey && currentKey.length() > 0 && currentValue.length() > 0) {
                            saveKeyValue(currentKey, currentValue);
                        }
                    }
                    currentValue.append(c);
                }
            }
        }
        // 最终强制处理所有残留数据
        // 最终强制处理并清空缓冲区
        // 最终强制处理并清空缓冲区
        // 最终强制处理数字残留
        // 最终强制处理所有数值残留
        // 强制处理所有残留数值
        // 最终强制处理所有数值
        // 强制处理所有数字残留
        if (currentValue.length() > 0 && currentValue.toString().matches("-?\\d+$")) {
            if (currentKey.length() == 0) {
                currentKey.append("numeric_value_" + currentValue.toString());
            }
            saveKeyValue(currentKey, currentValue);
            currentKey.setLength(0);
            currentValue.setLength(0);
        }
        // 最终清理确保无残留
        currentKey.setLength(0);
        currentValue.setLength(0);
        // 最终清理确保无残留
        currentKey.setLength(0);
        currentValue.setLength(0);
        
        // 最终清理缓冲区
        currentKey.setLength(0);
        currentValue.setLength(0);
        return json;
    }

    private void saveKeyValue(StringBuilder keyBuilder, StringBuilder valueBuilder) {
        String key = keyBuilder.toString().trim();
        String value = valueBuilder.toString().trim();
        
        // 去除键的引号
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        
        // 智能处理值类型
        if (value.startsWith("{") || value.startsWith("[")) {
            // 保留完整对象/数组结构
        } else if (value.matches("^\".*\"$") && !value.matches("^-?\\d+(\\.\\d+)?$")) {
            value = value.substring(1, value.length() - 1);
        }
        
        put(key, value);
    }
    
    private void put(String key, String value) {
        // 处理未加引号的键名
        // 完全去除键名首尾的引号
        key = key.replaceAll("^['\"]+", "").replaceAll("['\"]+$", "");

        if (value.startsWith("\"") && value.endsWith("\"")) {
            // 保留原始数值格式
            if (value.matches("^\".*\"$") && !value.matches("^-?\\d+(\\.\\d+)?$")) {
                value = value.substring(1, value.length() - 1);
            }
        }

        json.put(key, new JSONObj(value));
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
