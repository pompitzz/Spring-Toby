package sun.lee.t4_fifth;

import java.util.Arrays;

import static java.util.concurrent.Flow.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2Main {
    public static void main(String[] args) {
        // Publisher가 Observer Pattern의 Observable과 동일하다.
        // Subscriber가 Observer Pattern의 Observer를 조금더 확장한 것과 동일하다.
        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);

        // publisher는 subscribe 메서드 하나만 존재한다.
        Publisher<Integer> p = new Ex2MyPub(itr);

        Subscriber<Integer> s = new Ex2MySub1();
        Subscriber<Integer> s2 = new Ex2MySub2();

        p.subscribe(s);
        p.subscribe(s2);

    }
}
