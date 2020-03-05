package sun.lee.t4_fifth;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
// Duality와 Observer Pattern을 알아보고 Reactive Streams(표준 스펙)에 대해 알아본다.
@SuppressWarnings("deprecated")
public class Ex1IterableAndOb {

    public static void main(String[] args) {
        // 여러개의 데이터가 존재할 때 사실 이러한 데이터들은 하나씩 사용된다.
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

        // 해당 리스트는 상위 타입인 Iterable(foreach 루프를 가능하게 해주는 인터페이스)로 정의할 수 있다.
        Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);
        // Iterator는 요소들을 순회할 수 있는 하나의 도구이다.

        for (Integer i : iterable) { // -> for-each
        }


        // Iterable은 iterator()라는 추상메서드가 하나가 존재한다.
        // 이는 Iterator를 반환한다.
        // 그러므로 Iterator를 하나 정의하면 foreach문으 사용할 수 있다.
        Iterable<Integer> iter = () ->
                new Iterator<Integer>() {
                    int i = 0;
                    final static int MAX = 10;

                    @Override
                    public boolean hasNext() {
                        return i < MAX;
                    }

                    @Override
                    public Integer next() {
                        return ++i;
                    }
                };
        System.out.println("============= Custom Iterator ==============");
        // 내가 만든 iterable로도 foreach를 사용할 수 있다.
        for (Integer i : iter) {
            System.out.print(i + " ");
        }


        System.out.println("\n\n=============== Using Iterator Before Java 1.5 ===============");
        for (Iterator<Integer> it = iter.iterator(); it.hasNext(); ) {
            System.out.print(it.next() + " ");
        }


        System.out.println("\n\n==================== Observer =========================");
        // Iterable <- -> Observable (duality(쌍대성))
        // Iterable은 Pull 즉 사용하는 측에서 끌어온다.
        // Observable은 Push 즉 주객체에서 Push해주는 것이다.

        // Observer에 대해 알아보자.
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if((int)arg == 1) System.out.println("Start: Thread Name is --> " + Thread.currentThread().getName());
                System.out.print(arg + " ");
            }
        };

        // Observer로 구현해도 Iterator와 같이 동일하게 구현할 수 있다
        // Iterable, Observerable이 서로 Pull, Push로 반대이지만 결과는 동일하다.
        // Observer는 더 활용할 수 있는 기능들이 있다.
        IntObservable io = new IntObservable();
        io.addObserver(ob);
        io.run();


        // Observer는 서로 다른 쓰레드에서 notify도 가능하다.
        // Iterable도 만들 수 있겠지만 구현이 복잡하다.

        /** 리액티브 익스텐션을 처음 만든 MS 개발자들은 현재 옵저버 패턴는 몇가지 단점이 존재하며 더욱더 좋게 만들 수 있다고 제안하였다.
         * 단점 1. 끝(complete)라는 개념이 없다. 더 이상 notify가 오지 않는다면 어떻게 할 것인지에 대한 구현이 없다.
         * 단점 2. Error or Exception를 처리하는 방식이 일반화 되어있지 않다.
         */
        ExecutorService es = Executors.newSingleThreadExecutor();
        System.out.println("\n\n================ Other Thread ==================");
        es.execute(io);
        es.shutdown();

        // 기본 옵저버 패턴의 단점을 극복하여 확장된 옵저버 패턴이 리액티브 프로그래밍의 한 축이다.
    }

    static class IntObservable extends Observable implements Runnable{
        @Override
        public void run() {
            for(int i = 1; i <= 10; i++){
                setChanged();
                notifyObservers(i); // push
                // int i = it.next() // pull
            }
        }
    }

}
