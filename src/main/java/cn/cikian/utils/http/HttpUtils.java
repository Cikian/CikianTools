package cn.cikian.utils.http;


import cn.cikian.utils.http.enitry.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Iterator;
import java.net.URLEncoder;

/**
 * @author Cikian
 * @version 1.0
 * @since 2025/4/9 13:07
 */

public class HttpUtils {

    /**
     * 发送GET请求
     *
     * @param url 请求地址
     * @return 响应结果字符串
     * @throws IOException 网络异常
     */
    public static JSON getAction(String url) throws IOException {
        return getAction(url, null, null);
    }

    /**
     * 发送GET请求
     *
     * @param url 请求地址
     * @param params 请求参数Map
     * @return 响应结果字符串
     * @throws IOException 网络异常
     */
    public static JSON getAction(String url, Map<String, String> params) throws IOException {
        return getAction(url, null, params);
    }

    /**
     * 发送GET请求（带headers和params）
     *
     * @param url 请求地址
     * @param headers 请求头Map
     * @param params 请求参数Map
     * @return 响应结果字符串
     * @throws IOException 网络异常
     */
    public static JSON getAction(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpURLConnection connection = null;
        try {
            // 处理URL参数
            String fullUrl = url;
            if (params != null && !params.isEmpty()) {
                StringBuilder paramBuilder = new StringBuilder();
                Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    paramBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    if (it.hasNext()) {
                        paramBuilder.append("&");
                    }
                }
                fullUrl += (url.contains("?") ? "&" : "?") + paramBuilder;
            }

            // 创建连接
            URL requestUrl = new URL(fullUrl);
            connection = (HttpURLConnection) requestUrl.openConnection();

            // 设置请求方法
            connection.setRequestMethod("GET");
            
            // 设置请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return new JSON(response.toString());
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
