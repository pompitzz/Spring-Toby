package sun.lee.t6_seventh;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex3Interval {
    public static void main(String[] args) throws InterruptedException {

        // 주기를 가지고 동작하는 interval과 같은것은 따로 지정하지 않아도 다른 쓰레드에서 동작된다.
        // 하지만 유저 쓰레드가 아닌 데몬 쓰레드를 만들기 때문에 메인 쓰레드가 종료되면 함께 종료되어 동작되지 않는다.
        // 데몬쓰레드: 일반쓰레드의 작업을 돕는 쓰레드로 일반 쓰레드가 종료되면 함께 종료된다.(대표: GC)

        // 데이터를 샘플링해서 받거나 선별적으로 통계낼 때 interval이 유용하다.
        Flux.interval(Duration.ofMillis(200))
            .take(10) // 10개만 받고 끝냄
            .subscribe(s -> log.debug("onNext:{}", s));

        TimeUnit.SECONDS.sleep(5);

        // 유저가 만든 유저쓰레드는 메인 쓰레드가 종료되어도 종료되지 않는다.
        /*
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("==== new Single Thread Exit ====");
        });
         */
    }
}
