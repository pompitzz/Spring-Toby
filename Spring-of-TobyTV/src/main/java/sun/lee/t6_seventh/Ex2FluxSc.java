package sun.lee.t6_seventh;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2FluxSc {
    public static void main(String[] args) throws InterruptedException {
        // 메인 쓰레드에서 돌아간다.
        /*
        Flux.range(1, 10)
            .log()
            .subscribe(System.out::println);
         */

        // subscribeOn을 간단하게 설정할 수 있다.
        /*
        Flux.range(1, 10)
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
         */

        /*
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribe(System.out::println);
         */

        /*
        Flux.range(1, 10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
         */

    }

}
