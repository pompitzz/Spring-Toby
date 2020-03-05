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
 */
@Slf4j
public class Ex2OperatorsGenerics {

    // mapPub을 제네릭으로 변경하고 타입을 두개 넘기는 메서드들을 만들어보
    public static void main(String[] args) {

        Publisher<Integer> pub = iterPub(makeCollection(5));

        Publisher<String> mapPub = mapPub(pub, i -> "[ " + i + " ]");
        mapPub.subscribe(logSub());

        Publisher<StringBuilder> redecePub =
                reducePub(pub, new StringBuilder(), (s, i) -> s.append(i).append(", "));
        redecePub.subscribe(logSub());
    }


    // T타입을 R타입으로 변환
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return sub -> pub.subscribe(new Ex1DelegateSub<T, R>(sub) {
            @Override
            public void onNext(T t) {
                sub.onNext(f.apply(t));
            }
        });
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return sub -> pub.subscribe(
                new Ex1DelegateSub<T, R>(sub) {
                    R result = init;

                    @Override
                    public void onNext(T t) {
                        result = bf.apply(result, t);
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

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("\n\n===================== Start !! =======================");
                log.debug("onSubscribe:");
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
