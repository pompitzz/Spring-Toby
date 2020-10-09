package sun.lee.t8_nineth;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class LoadTest {

    public static void main(String[] args) {
        new LoadTest().test("");
    }

    @SneakyThrows
    public void test(String mapping) {
        String url = "http://localhost:8080" + mapping + "?idx={idx}";

        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();
        CyclicBarrier barrier = new CyclicBarrier(101);
        AtomicInteger counter = new AtomicInteger(0);
        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0; i < 100; i++) {
            // callable를 사용하게 하기위해 submit으로 변경하여 예외처리를 안해도된다.
            es.submit(() -> {
                int idx = counter.addAndGet(1);

                // 생성시 정해놓은 숫자에 쓰레드가 도달할 때 까지 멈추었다가 그 순간에 모든 순간에 쓰레드가 동작된다.
                barrier.await();

                log.info("Thread: {} ", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed: {} {} / {}", idx, sw.getTotalTimeSeconds(), res);
                return null;
            });
        }

        // 메인 쓰레드도 함께 멈추게한다.
        barrier.await();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }


}
