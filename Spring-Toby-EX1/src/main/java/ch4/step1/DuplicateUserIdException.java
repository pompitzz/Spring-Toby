package ch4.step1;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/10
 */
public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}
