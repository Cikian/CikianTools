package cn.cikian.utils.http.enitry;


import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:17
 */

public class Param {
    private Map<String, String> params;

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }

    public boolean containsValue(String value) {
        return params.containsValue(value);
    }

    public int size() {
        return params.size();
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public void clear() {
        params.clear();
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        params.putAll(m);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
