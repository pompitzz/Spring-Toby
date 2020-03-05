package sun.lee.t3_fourth;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class IntersectionTypeEx9 {
    /* Intersection이란 인터페이스를 하나이상 정의하는 것? */

    public static void main(String[] args) {
        hello(s -> s);
        hello((Function) s -> s);

        // marker interface -> 인터페이스 아무것도 존재하지 않는 인터페이스
        // marker interface는 리플렉션이 필요없고 타입을통해 구현할 수 있다.
        // market interface는 람다식에 Intersection으로 사용할 수 있다.

        // 이렇게 정의하면 람다식은 내부적으로 클래스를 만들 때 이 세가지 인터페이스를 다 구현하는 클래스로 만든다.
        // 그러므로 타입 조건이 이 3가지를 다 받을 수 있게 된다.
        hello((Function & Serializable & Cloneable) s -> s);

        // Hello, Hi는 default 메서드만 존재하므로 람다식을 그대로 사용할 수 있다.
        // 하지만 구현된 메서드보면 하나하나 exteds로 범위를 지정해줘야한다.
        // 이를 콜백방식으로 구현할 수 있다.
        hello3((Function & Hello & Hi) s -> s);

        // 파라미터가 있고 리턴값이 없는 Consumer를 사용하면 해당 메서드에는 extends Function만 있더라도
        // 매개변수를 전달할 때 앞의 함수에 (Function & Hello & Hi)를 명시해주면 뒤의 컨슈머는 T를 해당 타입으로 캐스팅해준다.
        run((Function & Hello & Hi) s -> s, s -> {
            s.hi();
            s.hello();
        });

        // Printer Interface를 새로 만들어도 매우 간단하게 기능을 추가할 수 있다.
        // 이렇게 새롭게 기능을 추가하는게 각자 독립적인 인터페이스를 합친것이 무엇이 쓸모 있을까?
        run((Function & Hello & Hi & Printer) s -> s, s -> {
            s.hi();
            s.hello();
            s.print("Using Intersection Type");
        });

        // Hi2는 Function을 상속받아 Function의 메서드를 사용할 수 있음에도 불가하고 사용이 가능하다.
        // Function , Hi2를 합쳤을 때 최종적으로는 Function 메서드 하나만 존재하기 때문에 가능한 것이다.
        run((Function & Hi2) s -> s, Hi2::hi);
    }

    private static void hello(Function o) {
    }

    // 람다객체도 직렬화의 대상을 확인하는 코드에 넘길 수 있기때문에 이를 위해서 이런식으로 사용할 수 도 있다.
    // 이러면 굳이 새로운 클래스를 만들어서 사용할 필요가 없게 된다.
    // 사실 직렬화는 요즘에 거의 사용하지 않는다. 그렇다면 이 인터섹션 타입을 통해 무엇을 이용할 수 있을까?
    // 이를 활용하여 이름을 정하지 않고 새로운 타입을 만들어내는 익명 타입을 만들 수 있다.
    private static <T extends Function & Serializable & Cloneable> void hello2(T o) {
    }



    interface Hello {
        default void hello() {
            System.out.println("Hello");
        }
    }

    interface Hi {
        default void hi() {
            System.out.println("Hi");
        }
    }

    private static <T extends Function & Hello & Hi> void hello3(T o){
        o.hello();
        o.hi();
    }

    private static <T extends Function> void run(T t, Consumer<T> consumer){
        consumer.accept(t);
    }

    interface Printer{
        default void print(String str){
            System.out.println(str);
        }
    }

    interface Hi2 extends Function{
        default void hi() {
            System.out.println("Hi");
        }
    }
}
