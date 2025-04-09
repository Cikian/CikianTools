package cn.cikian.utils.http.enitry;


import cn.cikian.utils.string.StringUtils;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:50
 */

public class JSONObj {
    Object v;
    String type;

    public Object get() {
        return v;
    }

    public void set(String v, String type) {
        this.v = v;
        this.type = type;
    }

    public JSONObj(Object v) {
        this.setJ(v.toString());
    }

    private void setJ(Object v) {
        this.v = v;

        if (v instanceof Integer) this.type = "int";
        else if (v instanceof Boolean) this.type = "boolean";
        else if (v.toString().startsWith("{") && v.toString().endsWith("}")) type = "obj";
        else if (v.toString().startsWith("[") && v.toString().endsWith("]")) type = "array";
        else type = "string";
    }

    @Override
    public String toString() {
        return v.toString();
    }
}
