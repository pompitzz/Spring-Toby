package sun.lee.t3_fourth;

import java.util.function.Consumer;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Mine {
    interface Pair<T> {
        T getName();

        void setName(T name);
    }

    static class Name<T> implements Pair<T> {
        private T name;

        public Name(T name) {
            this.name = name;
        }

        @Override
        public T getName() {
            return this.name;
        }

        @Override
        public void setName(T name) {
            this.name = name;
        }
    }

    interface DelegateTo<T> {
        T delegate();
    }

    interface FowardingTo<T> extends DelegateTo<Pair<T>>, Pair<T> {
        default T getName() {
            return delegate().getName();
        }

        default void setName(T name) {
            delegate().setName(name);
        }
    }

    public static void main(String[] args) {
        run(
                (FowardingTo<String> & Printable<String>) () -> new Name("Dexter"),
                c -> {
                    System.out.println("c.getName() = " + c.getName());
                    c.print();
                }
        );
    }

    private static <T extends DelegateTo<R>, R> void run(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    interface Printable<T> extends DelegateTo<Pair<T>> {
        default void print() {
            System.out.println("delegate().getName() = " + delegate().getName());
        }
    }
}
