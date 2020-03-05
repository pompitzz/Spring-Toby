package sun.lee.t3_fourth;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx6 {
    static class A {
    }

    static class B extends A {
    }

    public static void main(String[] args) {
        List<B> listB = new ArrayList<>();
        // List<A> la = listB; // Error 상속 문제
        List<? extends A> la2 = listB; // 이것은 가능해진다.
        List<? super B> la3 = listB;
        // List<? super A> la4 = listB;

        // la2.add(new A()); 둘 다 들어갈 수 없고 오직 null만 가능하다.
        // la2.add(new B());
        la2.add(null);
    }
}
