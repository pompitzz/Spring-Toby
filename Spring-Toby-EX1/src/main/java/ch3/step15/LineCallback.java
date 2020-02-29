package ch3.step15;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public interface LineCallback<T> {
    T doSomethingWithLine(final String line, T value);
}
