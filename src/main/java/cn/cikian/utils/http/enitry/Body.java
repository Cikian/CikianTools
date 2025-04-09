package cn.cikian.utils.http.enitry;


import java.util.List;
import java.util.Map;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:13
 */

public class Body {
    private Map<String, JSON> body;
    private String contentType;
    private String charset;

    public boolean isEmpty() {
        return body.isEmpty();
    }


    public void setBody(Map<String, JSON> body) {
        this.body = body;
    }
}
