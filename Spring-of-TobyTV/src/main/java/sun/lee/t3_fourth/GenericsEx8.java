package sun.lee.t3_fourth;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx8 {
    /* Capture에 대해 알아본다 */
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 3, 2);
        reverse(list);

        assertThat(list).isEqualTo(Arrays.asList(2, 3, 5, 4, 3, 2, 1));
    }

    static <T> void reverse(List<T> list) {
        List<T> temp = new ArrayList<>(list);
        for (int i = 0; i < temp.size(); i++) {
            list.set(i, temp.get(temp.size() - 1 - i));
        }
    }

    static void reverse2(List<?> list) {
        List<?> temp = new ArrayList<>(list);
        for (int i = 0; i < temp.size(); i++) {
            // list.set(i, temp.get(temp.size() - 1 - i));
            // capture로 인한 문제가 발생한다.
        }
    }

    // 자바에서는 이럴 경우 Helper를 만들어서 사용하라고 한다.
    // API를 사용하는 클라이언트는 내부 구현에 대해 오해를 불러일으킬 수 있으니 와이들카드를 통해 구성하고 helper를 사용한다.
    static void reverse3(List<?> list) {
        reverseHelper(list);
    }

    private static <T> void reverseHelper(List<T> list) {
        List<T> temp = new ArrayList<>(list);
        for (int i = 0; i < temp.size(); i++) {
            list.set(i, temp.get(temp.size() - 1 - i));
        }
    }

    // 이러한 방법도 있지만 hleper를 이용하는게 좋아보인다.
    static void reverse4(List<?> list) {
        List temp = new ArrayList<>(list);
        List list2 = list;
        for (int i = 0; i < temp.size(); i++) {
             list2.set(i, temp.get(temp.size() - 1 - i));
        }
    }
}
