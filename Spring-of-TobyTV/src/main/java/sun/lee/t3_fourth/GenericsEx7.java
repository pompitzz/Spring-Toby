package sun.lee.t3_fourth;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx7 {
    /* 타입 파라미터와 와일드카드를 차이에 대해 알아본다. */

    // Type Parameter를 지정하면 해당 파라미터를 여러군데에서 사용이 가능하다.
    static <T> void method(List<T> list) {
    }

    static void method2(List<?> list) {
        // list.add(1); 메서드가 특정 타입 파라미터를 요구하면 사용이 불가능하다.

        list.add(null);
        final int size = list.size();
        final Iterator<?> iterator = list.iterator();
        final boolean equals = list.equals(2);
    }

    static <T> boolean isEmpty(List<T> list) {
        return list.size() == 0;
    }

    // list에서 타입 파라미터가 필요없는 기능만 사용할 경우 와일드카드를 사용해도 된다.
    static boolean isEmpty2(List<?> list) {
        return list.size() == 0;
    }

    static <T> long frequency(List<T> list, T elem) {
        return list.stream()
                .filter(s -> s.equals(elem))
                .count();
    }

    static long frequency2(List<?> list, Object elem) {
        return list.stream()
                .filter(s -> s.equals(elem)) // equals와 같은 Object method는 와일드카드도 사용할 수 있다.
                .count();
    }

    // Upper Bound를 활용할 수 있다. 와일드 카드로 작성하면 더 깔끔하게 작성이 가능하다.
    static <T extends Comparable<T>> T max(List<T> list) {
        return list.stream()
                .reduce((a, b) -> a.compareTo(b) > 0 ? a : b)
                .orElseThrow(() -> new IllegalArgumentException("Boom!"));
    }

    // 메서드 내에서 사용하는 타입의 경우 상위 한정(extends)
    // 메서드 밖에서 사용되기 위해선 하위 한정(super)
    static <T extends Comparable<? super T>> T max2(List<? extends T> list) {
        return list.stream()
                .reduce((a, b) -> a.compareTo(b) > 0 ? a : b)
                .orElseThrow(() -> new IllegalArgumentException("Boom!"));
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 3);
        assertThat(isEmpty(list)).isFalse();
        assertThat(isEmpty2(list)).isFalse();

        assertThat(frequency(list, 3)).isEqualTo(2L);
        assertThat(frequency2(list, 3)).isEqualTo(2L);
        assertThat(frequency2(list, "A")).isEqualTo(0L);
        /* 두개 다 잘 동작한다.
           - 자바의 설계사상을 잘 따른다면 와일드 카드를 사용한 2번이 적합하다.
           - 메서드에 타입 파라미터를 지정하는데 그 타입 파라미터를 활용 않는다면 API의 의도를 들어내기 어렵다.
        */

        assertThat(max(list)).isEqualTo(5);
        assertThat(max2(list)).isEqualTo(5);

        // Collections max는 파라미터로 Comparator를 받아 max를 구현하였다.
        assertThat(Collections.max(list, (a, b) -> a - b)).isEqualTo(5);
        assertThat(Collections.max(list, (Comparator<Integer>) (a, b) -> a - b)).isEqualTo(5);

        // Comparator를 슈퍼타입인 Object로 하여도 구현이 가능하다.
        // T의 상위 타입인 Object는 T의 하위타입을 쓸 수 있기 때문에 문제가 생기지 않는것.
        assertThat(Collections.max(list,
                (Comparator<Object>) (a, b) -> a.toString().compareTo(b.toString())))
                .isEqualTo(5);


    }
}
