package sun.lee.t6_seventh;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
@Slf4j
public class Ex1Scheduler {
    // 표준에 나와있는 프로토콜(Pub, Sub)를 잘 알아야 활용이 가능하고 확장이 가능하다!
    public static void main(String[] args) {

        // 현재는 메인 쓰레드에서 동작하지만 사실 대부분의 이벤트들은 다른 백그라운드에서 동작된다.
        // 그리고 그 이벤트들은 언제 작동하는지 알 수 없다.
        // 서버일 경우 계속 이러한 이벤트를 기다릴 경우 커넥션 풀, 쓰레드 풀이 가득차 문제가 발생할 것이다.
        // 그러므로 실제 리액티브 프로그래밍은 Pub와 Sub가 같은 쓰레드에서 돌아가는 일은 거의 없다.
        // 스케줄러를 이용하면 이러한 동작을 구현할 수 있다

        /** 스케줄러
         *  - 스케줄러는 subscribeOn, publishOn 2가지 동작 방식이 존재한다.
         *  - subscribeOn은 값을 주는곳을 다른 쓰레드로 둔다.
         *  - publishOn은 값을 받아서 사용하는 쪽에 다른 쓰레드를 둔다.
         */
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request");
                    IntStream.rangeClosed(1, 5)
                            .forEach(sub::onNext);

                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };
//        System.out.println("\n\n=================== Pub ======================");
//        pub.subscribe(logSub());


        // subscribeOn을 사용하는 방식에 대해 알아본다.
        // 값을 주는 Publisher를 다른 쓰레드에서 동작시킨다.
        // subscribeOn은 값을 전달해주는 Publisher가 블럭킹 I/O와 같이 작업을하여 수행이 느리고 이를 처리하는 쪽은 빠를때 사용하기 적합하다.
        Publisher<Integer> subOnPub = sub -> {
            // 하나의 쓰레드만 제공해주고 더 많은 쓰레드를 요청하면 큐에 집어넣어 대기시킨다.
            System.out.println("\n\n=================== subscribeOn ======================");
            ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("subscribeOn - "));
            es.execute(() -> pub.subscribe(sub));
            es.shutdown();
        };
//        subOnPub.subscribe(logSub());


        // publishOn은 subscribeOn과 반대로 값을 전달하는 Publish는 빠르나 해당 값을 소비하는 Subscriber가 느린경우 사용하기 적합하다.
        // 새로운 subscriber를 정의하고 다른쓰레드에서 subscriber 기능이 동작된다.
        // 즉 subscribeOn은 Publisher를 다른 쓰레드에서, publishOn은 subscriber를 다른 쓰레드에서 동작 시키는 것이다.
        // 그러므로 출력을확인해보면 서로 다른것을 알 수 있다, 이 둘을 모두 적용할 수도 있다.
        Publisher<Integer> pubOnPub = sub -> {
            System.out.println("\n\n=================== publishOn ======================");
            pub.subscribe(anotherThreadSub(sub));
        };
        pubOnPub.subscribe(logSub());


        // Subon, Pubon 둘 다 사용하여 적용할 수도 있다.
        Publisher<Integer> subAndPubOnPub = sub -> {
            System.out.println("\n\n=================== publishOn ======================");
            subOnPub.subscribe(anotherThreadSub(sub));
        };
//        subAndPubOnPub.subscribe(logSub());

        System.out.println("=========== Main Method Exit ============");
    }

    private static CustomizableThreadFactory customThreadName(String name) {
        return new CustomizableThreadFactory() {
            @Override
            public String getThreadNamePrefix() {
                return name;
            }
        };
    }

    private static Subscriber<Integer> anotherThreadSub(Subscriber<? super Integer> sub) {
        return new Subscriber<Integer>() {
            ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("publishOn - "));

            @Override
            public void onSubscribe(Subscription s) {
                // onSubscribe는 구독하는 것 이기때문에 굳이 다른 쓰레드에 둘 필요없다.
                sub.onSubscribe(s);
            }

            @Override
            public void onNext(Integer integer) {
                es.execute(() -> sub.onNext(integer));
            }

            @Override
            public void onError(Throwable t) {
                es.execute(() -> sub.onError(t));
                es.shutdown();
            }

            @Override
            public void onComplete() {
                es.execute(sub::onComplete);
                es.shutdown();
            }
        };
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext:{}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError:{}", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }
}
