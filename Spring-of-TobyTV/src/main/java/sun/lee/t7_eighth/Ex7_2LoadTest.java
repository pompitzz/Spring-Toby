package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex7_2LoadTest {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();

        // 요청은 계속 대기상태이다.
        // dr/count를 확인해보면 100개가 존재한다.
        // dr/event로 메시지를 설정하면 동시에 종료된다.
        String url = "http://localhost:8080/dr";

        StopWatch main = new StopWatch();
        main.start();

        for(int i = 0 ; i < 100; i++){
            es.execute(() -> {
                int idx = counter.addAndGet(1);
                log.info("Thread: {} ", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                rt.getForObject(url, String.class);

                sw.stop();
                log.info("Elapsed: {} {} ", idx, sw.getTotalTimeSeconds());
            });
        }

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total: {}", main.getTotalTimeSeconds());
    }
}
