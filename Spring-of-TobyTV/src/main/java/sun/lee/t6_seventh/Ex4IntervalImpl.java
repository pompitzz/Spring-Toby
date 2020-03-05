package sun.lee.t6_seventh;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex4IntervalImpl {
    public static void main(String[] args) {
        // 오퍼레이터를 이용하여 Interval을 구현할 수 있다.
        // 오퍼레이터 대표 기능 : 데이터 변환 조작, 스케줄링, 퍼블리싱 컨트롤

        Publisher<Integer> pub = sub ->
                sub.onSubscribe(
                        new Subscription() {
                            int no = 0;

                            // volatile은 변수 값을 CPU캐시가 아닌 메인 메모리에 저장한다.
                            // 이는 동기화문제에 안전하진 않지만 다른 쓰레드에서 이를 읽기만 하는 경우 최신 값을 보장해준다.
                            volatile boolean cancelled = false;

                            @Override
                            public void request(long n) {
                                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                                exec.scheduleWithFixedDelay(() -> {
                                    if (cancelled) {
                                        exec.shutdown();
                                        return;
                                    }
                                    sub.onNext(no++);
                                }, 0, 300, TimeUnit.MILLISECONDS);
                            }

                            @Override
                            public void cancel() {
                                // cancel, unsubscribe, disable 등으로 표현된다.
                                cancelled = true;
                            }
                        });


        // pub.subscribe(logSub());


        Publisher<Integer> takePub = sub -> {
            pub.subscribe(
                    new Subscriber<Integer>() {
                        int count = 0;
                        Subscription subscription;

                        @Override
                        public void onSubscribe(Subscription s) {
                            this.subscription = s;
                            sub.onSubscribe(s);
                        }

                        @Override
                        public void onNext(Integer integer) {
                            sub.onNext(integer);
                            if (++count > 10) {
                                subscription.cancel();
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            sub.onError(t);
                        }

                        @Override
                        public void onComplete() {
                            sub.onComplete();
                        }
                    });
        };

        takePub.subscribe(logSub());
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
