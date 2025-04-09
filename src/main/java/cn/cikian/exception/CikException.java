package cn.cikian.exception;

/**
 * Cik异常类
 *
 * @author Cikian
 * @version 1.0
 * @since 2025/4/8
 */
public class CikException extends RuntimeException {
    private final int errorCode;
    private final String errorDetail;

    public CikException(int errorCode, String errorDetail) {
        super("ErrorCode: " + errorCode + ", Message: " + errorDetail);
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    public CikException(int errorCode, String errorDetail, Throwable cause) {
        super("ErrorCode: " + errorCode + ", Message: " + errorDetail, cause);
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    @Override
    public String toString() {
        return "CryptException{" +
                "errorCode=" + errorCode +
                ", errorDetail='" + errorDetail + '\'' +
                '}';
    }
}