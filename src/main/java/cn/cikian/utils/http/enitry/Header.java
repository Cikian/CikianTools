package cn.cikian.utils.http.enitry;


import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:08
 */

public class Header {
    Map<String, String> headers;

    public Header(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(String key) {
        return headers.get(key);
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public void remove(String key) {
        headers.remove(key);
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public boolean containsValue(String value) {
        return headers.containsValue(value);
    }

    public int size() {
        return headers.size();
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public void clear() {
        headers.clear();
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        headers.putAll(m);
    }
}
