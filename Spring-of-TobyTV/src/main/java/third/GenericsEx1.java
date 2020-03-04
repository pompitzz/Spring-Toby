package third;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx1 {
    static class Hello<T> { // -> type parameter

    }
    public static void main(String[] args) {
        new Hello<String>(); // -> type argument

        List list = new ArrayList<Integer>(); // 이러한 것을 rawType이라고 한다.

        final List<Integer> ints = Arrays.asList(1, 2, 3);
        List rawInts = ints;
        List<Integer> ints2 = rawInts;

        List<String> strings = rawInts;
        // 컴파일 시 에러는 발생하지 않지만, 런타임 시 에러가 발생하므로 타입 안정성을 보장하지 못한다.
        // System.out.println(strings.get(0)); // Error 발생!
    }
}
