package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 *
 * 비동기 작업의 결과는 Futuer, Callback을 사용하여 가져올 수 있다.
 */

@Slf4j
public class Ex1Future {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        /** Future를 잘 이해하는 것이 상당히 중요하다.
         *  - Future는 비동기 작업을 수행한 결과를 가지고 있는 인터페이스이다.
         *  - 비동기 작업은 새로운 쓰레드에서 별개의 작업을 수행시키고 그 쓰레드의 결과를 또 다른 쓰레드에서 사용해야하는데 이러한 곳에서 사용할 수 있는 것이 Future이다.
         */


        ExecutorService es = Executors.newCachedThreadPool();

        // Runnable은 리턴할 수 없기 때문에 submit을 사용해야한다.
        /*
        es.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                log.info("Runnable Async");
            } catch (InterruptedException e) {
                // 오너 쓰레드가 강제로 깨울 수 있기 때문에 예외가 발생
                e.printStackTrace();
            }
        });
         */

        // Callable 리턴값이 있고 예외를 던지게 구현되어있다.
        Future<String> f = es.submit(() -> {
            TimeUnit.SECONDS.sleep(2);
            log.info("Callable Async");
            return "Hello";
        });

        // World가 가장 먼저 찍힌다.
        log.info("World");

        // Future의 get은 비동기 작업이 완료될 때 까지 블럭킹되기 때문에 Exit이 마지막에 찍힌다.
        // 블럭킹되면 뭐하러 쓰레드를 빼서 비동기를 구성할까? 프로듀서, 컨슈머 패턴등을 활용하면 유용하게 동작시킬 수 있다.
        System.out.println(f.get());

        log.info("Exit");

        es.shutdown();
    }
}
