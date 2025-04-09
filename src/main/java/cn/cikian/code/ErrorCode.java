package cn.cikian.code;

/**
 * 加密相关错误代码枚举
 * @author Cikian
 * @since 2025/4/8
 * @version 1.0
 */
public enum ErrorCode {
    SUCCESS(0, "成功", "操作成功完成"),
    FAIL(-1, "失败", "操作未完成"),
    INVALID_KEY(1001, "无效密钥", "提供的加密密钥格式不正确"),
    ENCRYPT_FAILED(1002, "加密失败", "加密过程中发生错误"),
    DECRYPT_FAILED(1003, "解密失败", "解密过程中发生错误"),
    UNSUPPORTED_ALGORITHM(1004, "不支持的算法", "请求的加密算法不被支持"),
    INVALID_INPUT(1005, "无效输入", "输入数据不符合要求");

    private final int code;
    private final String message;
    private final String detail;

    ErrorCode(int code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    public int code() {
        return code;
    }

    public String mes() {
        return message;
    }

    public String detail() {
        return detail;
    }

    @Override
    public String toString() {
        return "CodeEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}