package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex2Future {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        // Callable 리턴값이 있고 예외를 던지게 구현되어있다.
        Future<String> f = es.submit(() -> {
            TimeUnit.SECONDS.sleep(2);
            log.info("Callable Async");
            return "Hello";
        });

        System.out.println(f.isDone());
        TimeUnit.MILLISECONDS.sleep(2100);
        log.info("Exit");

        System.out.println(f.isDone());
        // f.isDone()를 계속검증하면서 true일때 f.get()을 하면된다.
        // Callback을 활용하야 FutureTask를 활용하면 더 간단하게 구현이 가능하다.
        if(f.isDone()) System.out.println(f.get());

        es.shutdown();
    }
}
