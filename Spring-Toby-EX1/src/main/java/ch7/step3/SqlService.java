package ch7.step3;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/16
 */
public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
