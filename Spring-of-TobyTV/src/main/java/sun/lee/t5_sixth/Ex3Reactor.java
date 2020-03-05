package sun.lee.t5_sixth;

import reactor.core.publisher.Flux;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
// 리액터에서는 이전에 직접 구현했던 Pub, Sub보다 훨씬 더 다양한 기능들을 제공해준다.
public class Ex3Reactor {
    public static void main(String[] args) {
        // Flux는 Publisher의 일종이다.
        Flux.create(
                e -> {
                    e.next(1);
                    e.next(2);
                    e.next(3);
                    e.complete();
                })
                .log()
                .subscribe(System.out::println);
        // 로그를 확인해보면 onSubscribe -> request -> onNext * 3 -> onComplete 순으로 호출되는 것을 알 수 있다.
        // 즉 이전에 구현하였던 Publisher, Suplier와 동일하게 구성된다.

        System.out.println("\n =================================== \n");
        Flux.<Integer>create(
                e -> {
                    e.next(1);
                    e.next(2);
                    e.next(3);
                    e.complete();
                })
                .log()
                .map(i -> i * 10)
                .log()
                .subscribe(System.out::println);
        // 로그를 확인해보면 Create -> Map 순으로 진행되는 것을 알 수 있다.

        System.out.println("\n =================================== \n");
        Flux.<Integer>create(
                e -> {
                    e.next(1);
                    e.next(2);
                    e.next(3);
                    e.complete();
                })
                .log()
                .reduce((a, b) -> a + b)
                .log()
                .subscribe(System.out::println);
        // 로그를 확인해보면 Create -> Reduce 순으로 진행되는 것을 알 수 있다.
        // 그리고 아까 구현한거처럼 onComplete()에서 마지막 한번 Reduce에서 onNext()가 호출되는 것을 알 수 있다.
    }
}
