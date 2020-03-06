package sun.lee.t10_eleventh;

import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex2CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * - CompletableFuture에다가 백그라운드에서 돌아가는 쓰레드를 생성하고 작업을 완료하는 CompletableFuture를 생성해보자
         * - 컴플이션 스테이션의 장점은 디버깅 시 기존의 thread 코딩에 비해 core의 성능을 덜 잡아 먹는다.
         * - CompletableFuture은 Future, CompletionStage를 구현하고 있다.
         * - CompletionStage는 하나의 비동기 작업을 수행하고 그 작업에 의존적으로 수행할 수 있는 명령을 가지고 있는 기능
         * - CompletableFuture의 문서 혹은 자료들을 한번 보자.
         */

//        test1();

        test2();

        //test3();
        //test4();
//        test5();

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }

    // 다양한 쓰레드에서 처리를 하게 하여 자원을 관리하는 방법이 있을까?
    private static void test5() {
        ExecutorService es = Executors.newFixedThreadPool(10);

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                }, es) // 사용할 쓰레드를 명시할 수 있다.
                .thenCompose(s -> {
                    log.info("thenRun:{}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApplyAsync(s -> { // 쓰레드 풀의 전략에 따라 새로운 쓰레드로 만들어준다.
                    log.info("thenRun:{}", s);
                    return s + 5;
                }, es)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s -> log.info("thenAccept:{}", s), es);
                // 총 3번 쓰레드가 넘어가는 것을 알 수 있다.
    }

    // 예외가 발생하면 계속 넘기는 방식과, 의미있는 데이터로 변경하여 넘길 수 있다.
    private static void test4() {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    if (true) throw new RuntimeException();
                    return 1;
                })
                .thenCompose(s -> {
                    log.info("thenRun:{}", s);
                    return CompletableFuture.completedFuture(s + 1);
                })
                .thenApply(s -> {
                    log.info("thenRun:{}", s);
                    return s + 5;
                })
                .exceptionally(e -> -10)
                // 출력을 보면 supplyAsync에서 바로 thenAccept로 넘어가는데 반환값이 -10이다
                // 즉 계속해서 예외를 처리할 필요가 없다.
                .thenAccept(s -> log.info("thenAccept:{}", s));
    }

    private static void test3() {
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                })
                .thenCompose(s -> {
                    log.info("thenRun:{}", s);
                    return CompletableFuture.completedFuture(s + 1);
                    // CompletableFuture를 리턴일 경우 CompletableFuture안에 CompletableFuture가 들어가있고 값이 들어가 있다.
                    // 이는 thenCompose르 사용하면 된다. FlatMap과 유사하다.
                })
                .thenApply(s -> {
                    log.info("thenRun:{}", s);
                    return s + 5;
                })
                .thenAccept(s -> log.info("thenAccept:{}", s));
    }

    private static void test2() {

        // 각 메서드들은 계속해서 비동기 작업의 결과와 상태를 가지고 있는 CompletableFuture를 리턴한다.
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("supplyAsync");
                    return 1;
                })
                .thenApply(s -> { // 앞에서 리턴한 값을 받아올 수 있다.
                    log.info("thenRun:{}", s);
                    return s + 1;
                })
                .thenApply(s -> { // 앞에서 리턴한 값을 받아올 수 있다.
                    log.info("thenRun:{}", s);
                    return s + 5;
                })
                .thenAccept(s -> log.info("thenAccept:{}", s));
    }

    private static void test1() {
        // 풀을 설정하지 않으면 ForkJoinPool.commonPool이 동작한다.
        CompletableFuture
                .runAsync(() -> log.info("runAsync"))
                .thenRun(() -> log.info("thenRunAsync")) // 하나의 비동기 작업에 대한 의존적인 코드를 간단하게 만들 수 있다.
                .thenRun(() -> log.info("thenRunAsync"))
        ;
        // 먼저 출력
        log.info("exit");
    }
}
