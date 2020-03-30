package me.sun.tobyreview.non_spring.reactive.ch4;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Slf4j
public class Ex1Scheduler {
    public static void main(String[] args) {
        Publisher<Integer> pub = s -> {
            s.onSubscribe(
                    new Subscription() {
                        @Override
                        public void request(long n) {
                            log.debug("request");
                            IntStream.rangeClosed(1, 5).forEach(s::onNext);
                        }

                        @Override
                        public void cancel() {

                        }
                    }
            );
        };

        Publisher<Integer> subOnPub = sub -> {
            System.out.println("\n===================== subscribeOn ======================");
            ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("subscribeOn -"));
            es.execute(() -> pub.subscribe(sub));
            es.shutdown();
        };

        Publisher<Integer> pubOnSub = s -> {
            System.out.println("\n====================== publishOn ===========================");
            pub.subscribe(anotherThreadSub(s));
        };


        Publisher<Integer> pubOnSubOn = s -> {
            ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("SubscribeOn -"));
            es.execute(() -> pub.subscribe(anotherThreadSub(s)));
            es.shutdown();
        };
        pubOnSubOn.subscribe(logSub());
    }

    private static Subscriber<? super Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext:{}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }

    private static Subscriber<? super Integer> anotherThreadSub(Subscriber<? super Integer> sub) {
        return new Subscriber<Integer>() {
            ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("PublishOn - "));

            @Override
            public void onSubscribe(Subscription s) {
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

    private static ThreadFactory customThreadName(String name) {
        return new CustomizableThreadFactory() {
            @Override
            public String getThreadNamePrefix() {
                return name;
            }
        };
    }
}
