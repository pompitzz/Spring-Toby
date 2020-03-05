package sun.lee.t3_fourth;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class IntersectionTypeEx11 {
    // 이미 만들어진 라이브러리들이 있을 때 동적으로 기능을 추가할 때 이 인터섹션 타입이 유용하다.
    public static void main(String[] args) {

        // Pair는 메서드가 많기 때문에 run에 함께 사용할 수 없다.
        // 이럴 때 포워딩 인터페이스를 이용하면 된다.
        Pair<String> name = new Name("Dongmyeong", "Lee");

        run(
                (ForwardingPair<String>) () -> name,
                o -> {
                    System.out.println("o.getFirst() = " + o.getFirst());
                    System.out.println("o.getSecond() = " + o.getSecond());
                }
        );

        // 새로운 기능을 간단하게 추가할 수 있게 되었다.
        run(
                (ForwardingPair<String> & Convertable<String>) () -> name,
                o -> {
                    print(o);
                    o.convert(s -> s.toUpperCase());
                    print(o);
                    o.convert(s -> s.substring(0, 2));
                    print(o);
                }
        );

        // 따로 메서드로 print를 만드는게 아닌 인터페이스로 기능을 추가하여 내장 메서드로 만들 수 있게되었다!
        run(
                (ForwardingPair<String> & Convertable<String> & Printable<String>) () -> name,
                o -> {
                    o.print();
                    o.convert(String::toUpperCase);
                    o.print();
                    o.convert(s -> s.substring(0, 2));
                    o.print();
                }
        );
    }

    interface Pair<T> {
        T getFirst();

        T getSecond();

        void setFirst(T first);

        void setSecond(T second);
    }

    static class Name implements Pair<String> {
        String firstName;
        String lastName;

        public Name(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String getFirst() {
            return firstName;
        }

        @Override
        public String getSecond() {
            return lastName;
        }

        @Override
        public void setFirst(String first) {
            firstName = first;
        }

        @Override
        public void setSecond(String second) {
            lastName = second;
        }
    }

    interface DelegateTo<T> {
        T delegateTo();

    }

    // Pair<T>의 메서드들을 default로 구현했기 때문에 해당 메서드들이 사라지게 된다.
    interface ForwardingPair<T> extends DelegateTo<Pair<T>>, Pair<T> {
        default T getFirst() {
            return delegateTo().getFirst();
        }

        default T getSecond() {
            return delegateTo().getSecond();
        }

        default void setFirst(T first) {
            delegateTo().setFirst(first);
        }

        default void setSecond(T second) {
            delegateTo().setSecond(second);
        }
    }

    interface Convertable<T> extends DelegateTo<Pair<T>> {
        default void convert(Function<T, T> mapper) {
            Pair<T> pair = delegateTo();
            pair.setFirst(mapper.apply(pair.getFirst()));
            pair.setSecond(mapper.apply(pair.getSecond()));
        }
    }

    static <T> void print(Pair<T> pair) {
        System.out.println(pair.getFirst() + " " + pair.getSecond());
    }

    interface Printable<T> extends DelegateTo<Pair<T>> {
        default void print() {
            final Pair<T> pair = delegateTo();
            System.out.println(pair.getFirst() + " " + pair.getSecond());
        }
    }

    private static <T extends DelegateTo<R>, R> void run(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }
}