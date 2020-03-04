package third;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx5 {

    public static void main(String[] args) {
        // List<T> list2; // T -> 지금은 모르지만 이 타입을 알고 사용할 것이다.

        List<?> list; // ? -> wildcards : 무엇인지 모르고 알 필요, 관심도 없다.
        list = new ArrayList<>();
        // list.add(1); ?로 정의하면 type parameter가 필요한 메서드를 사용하지 못한다. 즉 해당 메서드들이 필요 없을 때 사용하자

        final List<Integer> list3 = Arrays.asList(1, 2, 3);
        // printList(list3); Integer extends Object이지만 List<Integer> extends List<Object>가 아니다.
        printList2(list3); // ?는 해당 type parameter를 신경쓰지 않는다.
        // ? == ? extends Object -> 사실상 같은 의미
    }

    static void printList(List<Object> list){
        list.forEach(System.out::println);
    }

    static void printList2(List<?> list){
        list.forEach(System.out::println);
    }
}
