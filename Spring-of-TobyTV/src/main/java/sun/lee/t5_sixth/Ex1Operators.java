package sun.lee.t5_sixth;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 * <p>
 * Reactive Streams - Operators
 * <p>
 * Publisher -> Data -> Subscriber
 * 데이터를 그냥 전달하는게 아닌 적절한 한 Operator를 통해 데이터를 가공한다.
 * Publisher -> [Data1] -> Operator -> [Data2] -> Subscriber
 * Publisher -> [Data1] -> Opeator1 -> [Data2] -> -> Operator2 -> [Data3] -> Subscriber
 * <p>
 * 1. map(d1 -> f -> d2)
 * --> Down Stream,  <-- Up Stream
 * <p>
 * LogSub이 보기엔 mapPub도 Publisher이어야 한다.
 * pub -> [Data1] -> mapPub -> [Data2] -> LogSub
 */
@Slf4j
public class Ex1Operators {

    public static void main(String[] args) {

        Publisher<Integer> pub = iterPub(makeCollection(5));

        // mapPub는 Operator가 되어서 pub의 데이터를 가공할 수 있게 되었다.
        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
        mapPub.subscribe(logSub());

        // mapPub은 계속해서 재사용될 수 있다.
        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
        map2Pub.subscribe(logSub());

        // 합계를 계산하는 것은 받은 데이터를 Sub에게 다 던지는게 아닌 계산해서 합계만 던져야 한다.
        // 이는 onComplete를 활용하면 된다.
        Publisher<Integer> sumPub = sumPub(pub);
        sumPub.subscribe(logSub());

        Publisher<Integer> reducePub = reducePub(pub, 1, (a, b) -> a * b);
        reducePub.subscribe(logSub());
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return sub -> pub.subscribe(
                new Ex1DelegateSub<Integer, Integer>(sub) {
                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(f.apply(integer));
                    }
                });
    }

    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(
                        new Ex1DelegateSub<Integer, Integer>(sub) {
                            int sum = 0;

                            @Override
                            public void onNext(Integer integer) {
                                sum += integer;
                            }

                            @Override
                            public void onComplete() {
                                sub.onNext(sum);
                                super.onComplete();
                            }
                        });
            }
        };
    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub, Integer init, BiFunction<Integer, Integer, Integer> bf) {
        return sub -> pub.subscribe(
                new Ex1DelegateSub<Integer, Integer>(sub) {
                    int result = init;

                    @Override
                    public void onNext(Integer integer) {
                        result = bf.apply(result, integer);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        super.onComplete();
                    }
                }
        );
    }

    private static List<Integer> makeCollection(int limit) {
        return Stream.iterate(1, i -> i + 1)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("\n\n===================== Start !! =======================");
                log.debug("onSubscribe:");
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

    private static Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return sub -> sub.onSubscribe(
                new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(sub::onNext);
                            sub.onComplete();
                        } catch (RuntimeException e) {
                            sub.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
    }
}
