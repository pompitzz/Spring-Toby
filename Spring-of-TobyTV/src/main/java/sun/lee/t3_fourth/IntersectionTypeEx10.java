package sun.lee.t3_fourth;

import java.util.function.Consumer;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class IntersectionTypeEx10 {

    public static void main(String[] args) {
        run((DelegateTo<String> & Hello) () -> "Dexter", o -> {
            System.out.println("o.delegateTo() = " + o.delegateTo());
            // hello의 delegateTo는 앞 아규먼트에서 정의한 delegateTo를 사용할 수 있게 된다!
            o.hello();
        });

        // UpperCase기능을 추가하였다.
        run((DelegateTo<String> & Hello & UpperCase) () -> "This is IntersectionType Test", o -> {
            o.hello();
            // 추가적인 기능을 클래스로 정의하지 않고 확장할 수 있다.
            o.toUpperCase();
        });


    }

    interface DelegateTo<T> {
        T delegateTo();

    }

    interface Hello extends DelegateTo<String> {
        default void hello() {
            System.out.println("Hello " + delegateTo());
        }

    }

    // T가 DelegateTo이므로 새로운 파라미터를 정의 해주어야 한다.
    private static <T extends DelegateTo<R>, R> void run(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    interface UpperCase extends DelegateTo<String>{
        default void toUpperCase(){
            System.out.println(delegateTo().toUpperCase());
        }
    }
}