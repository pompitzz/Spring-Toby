package ch7.step3;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/16
 */
public class SqlRetrievalFailureException extends RuntimeException {
    public SqlRetrievalFailureException(String message) {
        super(message);
    }

    // SQL을 가져오는데 실패한 근본 원인을 담을 수 있도록 중첩 예외를 저장할 수 있는 생성자를 만들어준다.
    public SqlRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
