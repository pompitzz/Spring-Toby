package ch6.step7;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/15
 */
public interface TargetInterface {
    void hello();
    void hello(String a);
    int minus(int a, int b) throws RuntimeException;
    int plus(int a, int b);
}
