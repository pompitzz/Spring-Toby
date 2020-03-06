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
public class Ex6_2LoadTest {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/async2";

        StopWatch main = new StopWatch();
        main.start();

        /**
         * - 톰캣 기본설정일 땐 2초면 모든 요청이 처리된다. (비동기로 처리되기 때문에)
         * - 맥스 쓰레드를 20개로 줄였을 때는 5배 정도 늘어난다.
         * - 사실 2초동안의 일은 서블릿 쓰레드가 가지고 있을 필요가 없다. 요청 url을 변경해보자
         * - 그럼 쓰레드가 20개여도 2초에 처리된다.
         * - 이는 서블릿 쓰레드는 20개지만 작업 쓰레드를 100개 만들어서 동시에 돌리면서 해당 작업 쓰레드가 끝날 때 서블릿 쓰레드가 그것을 받아 반환해준다.
         * - 서블릿 쓰레드가 한개여도 거의 같다. 왜냐하면 작업 쓰레드가 200개 생기고 그 작업을 다 처리하기 때문이다.
         * - 나는 왜 안되고 더 시간이 걸릴까? 스위칭 오버헤드가 발생하나?
         */
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
