package ch6.step7;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/15
 */
public class Target implements TargetInterface {
    @Override
    public void hello() { }

    @Override
    public void hello(String a) { }

    @Override
    public int minus(int a, int b) throws RuntimeException { return 0; }

    @Override
    public int plus(int a, int b) { return 0; }

    public void method() { }
}
