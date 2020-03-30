package me.sun.tobyreview.non_spring.reactive.ch3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class Ex1Operators {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Arrays.asList(1, 2, 3, 4, 5));
        Publisher<Integer> mapPub = mapPub(pub, i -> i * 3);
        mapPub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return s -> pub.subscribe(
                new Ex1DelegateSub<T, R>(s) {
                    R result = init;

                    @Override
                    public void onNext(T t) {
                        log.debug("onNext:{}", t);
                        result = bf.apply(result, t);
                    }

                    @Override
                    public void onComplete() {
                        s.onNext(result);
                        super.onComplete();
                    }
                }
        );
    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> fun) {
        return s -> pub.subscribe(
                new Ex1DelegateSub<T, R>(s) {
                    @Override
                    public void onNext(T t) {
                        s.onNext(fun.apply(t));
                    }
                }
        );
    }

    private static Publisher<Integer> iterPub(Iterable<Integer> iterable) {
        return s ->
                s.onSubscribe(
                        new Subscription() {
                            @Override
                            public void request(long n) {
                                try {
                                    iterable.forEach(s::onNext);
                                    s.onComplete();
                                } catch (RuntimeException e) {
                                    s.onError(e);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        });
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("=================== Start!! ===================");
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T t) {
                log.debug("onNext:{}", t);
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
