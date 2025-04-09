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
        if (o == null) {
            return null;
        }

        if (!o.toString().startsWith("{") || !o.toString().endsWith("}")) {
            throw new CikException(ErrorCode.FAIL.code(), "JSON格式错误！");
        }
        String v = StringUtils.removeSpace(o.toString());
        // 去掉首尾的{}
        v = v.substring(1, v.length() - 1);
        // 转为字符数组
        char[] chars = v.toCharArray();

        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        int currentIndex = 0;
        int startFlag = 0;
        int endFlag;
        boolean flag = false;
        boolean hasKey = false;
        boolean noQuotation = false;

        for (int i = 0; i < chars.length; i++) {
            if (i != 0 && chars[i] == ',') {
                if (chars[i - 1] != '"' && chars[i + 1] == '"'){
                    endFlag = i;
                    value.append(chars, startFlag, endFlag - startFlag);
                    this.put(key.toString(), value.toString());
                    flag = false;
                    hasKey = false;
                    key.setLength(0);
                    value.setLength(0);
                }
            }
            if (chars[i] == '"') {
                if (flag) {
                    endFlag = i;
                    if (hasKey) {
                        value.append(chars, startFlag + 1, endFlag - startFlag - 1);
                        this.put(key.toString(), value.toString());
                        flag = false;
                        hasKey = false;
                        key.setLength(0);
                        value.setLength(0);
                    } else {
                        key.append(chars, startFlag + 1, endFlag - startFlag - 1);
                        hasKey = true;
                        flag = false;
                    }
                } else {
                    startFlag = i;
                    flag = true;
                }

            }
        }

        if (chars[chars.length - 1] != '"') {
            endFlag = chars.length;
            for (int i = chars.length - 1; i >= 0; i--) {
                if (chars[i] == ':') {
                    startFlag = i + 1;
                    value.append(chars, startFlag, endFlag - startFlag);
                    endFlag = i - 1;
                    i-=2;
                }
                if (chars[i] == '"') {
                    startFlag = i + 1;
                    key.append(chars, startFlag, endFlag - startFlag);
                    break;
                }
            }
            this.put(key.toString(), value.toString());
        }


        return null;
    }

    private void put(String key, String value) {
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        json.put(key, new JSONObj(value));
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
