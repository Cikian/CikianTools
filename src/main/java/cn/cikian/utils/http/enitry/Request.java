package cn.cikian.utils.http.enitry;


/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:16
 */

public class Request {
    private String url;
    private String method;
    private Header headers;
    private Param params;
    private Body body;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Header getHeaders() {
        return headers;
    }

    public void setHeaders(Header headers) {
        this.headers = headers;
    }

    public Param getParams() {
        return params;
    }

    public void setParams(Param params) {
        this.params = params;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
