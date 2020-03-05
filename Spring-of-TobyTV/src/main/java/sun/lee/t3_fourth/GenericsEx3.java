package sun.lee.t3_fourth;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx3 {
    // Bounded Type Parameter

    // Bounded Type Parameter는 하나가 아닌 여러개가 올 수 있다.
    // class는 하나만 정의되고 interface는 여러개 가능
    static <T extends List & Serializable & Comparable & Cloneable> void print(T t) {
    }

    static long countGreaterThan(Integer[] arr, Integer elem) {
        return Arrays.stream(arr).filter(i -> i > elem).count();
    }

    static <T> long countGreaterThan(T[] arr, Predicate<T> predicate) {
        return Arrays.stream(arr).filter(predicate).count();
    }

    // Bounded Type Parameter를 활용하여 compareTo메서드를 사용할 수 있게됨
    static <T extends Comparable<T>> long countGreaterThan(T[] arr, T elem) {
        return Arrays.stream(arr).filter(i -> i.compareTo(elem) > 0).count();
    }

    public static void main(String[] args) {
        final Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println("countGreaterThan(arr, 3) = " + countGreaterThan(arr, 3));

        final String[] strings = {"a", "b", "c", "d", "e"};
        System.out.println("countGreaterThan(strings, \"b\") = " + countGreaterThan(strings, "b"));
        Predicate<String> p = s -> s.compareTo("b") > 0;
        System.out.println("countGreaterThan(strings, (s -> s.compareTo(\"b\") < 0)) = " +
                countGreaterThan(strings,  p));
    }
}
