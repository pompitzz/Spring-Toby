package third;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx2 {

    <T> void print(T t){
        System.out.println(t.toString());
    }

    static <T> void staticPrint(T t){
        System.out.println(t.toString());
    }

    static class Generics<T>{
//        static void print(T t){
//        static method는 class의 type parameter를 사용할 수 없다.
        // -> 클래스를 만들 때 type parameter가 정해지기 때문이다.
//            System.out.println(t.toString());
//        }
    }

    public static void main(String[] args) {
        new GenericsEx2().print("Hello");
        new GenericsEx2().print(1);

        GenericsEx2.staticPrint("World");
    }
}
