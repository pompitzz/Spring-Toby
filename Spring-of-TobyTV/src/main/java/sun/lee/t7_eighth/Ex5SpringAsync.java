package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex5SpringAsync {

    @Service
    public static class MyService{
        public String hello() throws InterruptedException {
            log.info("Hello()");
            TimeUnit.SECONDS.sleep(1);
            return "Hello";
        }

        /** 장시간의 배치작업을 할 때 사용하기 유용하다.
         *  - f.get()을 통해 계속받는게 아닌 결과를 디비에 넣고, 클라이언트가 원할 때 제공해준다.
         *  - 세션에 넣고 f.isDone()이 true이면 출력해주는 방식으로 구현할 수 있다.
         *  - 약 10년전에 이렇게 구현되었다.
         *  - Async는 쓰레드를 계속만들기 때문에 따로 설정이 필요하다.
         *  - 빈으로 Executor를 만들면 된다.
        */
        @Async
        public Future<String> hello2() throws InterruptedException {
            log.info("Hello()");
            // 별개의 쓰레드에서 동작된다.
            TimeUnit.SECONDS.sleep(1);
            return new AsyncResult<>("Hello");
        }

        // ListenableFuture를 이용해 콜백방식으로 구현할 수 있다.
        @Async
        public ListenableFuture<String> hello3() throws InterruptedException {
            log.info("Hello()");
            TimeUnit.SECONDS.sleep(1);
            return new AsyncResult<>("Hello");
        }
    }

    @Autowired
    MyService myService;

    @Bean @Primary
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            Future<String> f = myService.hello2();
            log.info("exit:{}", f.isDone());
            log.info("get:{}", f.get());
        };
    }

    @Bean
    ApplicationRunner run2() {
        return args -> {
            log.info("run2()");
            ListenableFuture<String> f = myService.hello3();
            f.addCallback(s -> log.info("call:{}", s), e -> log.info(e.getMessage()));
            log.info("exit()");
        };
    }

    @Bean
    ThreadPoolTaskExecutor tp(){
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10); // 첫 쓰레드 요청이 올 때 CorePoolSize까지 만듬.
        te.setMaxPoolSize(100); //
        te.setQueueCapacity(200); // 쓰레드 대기 장소
        // 10개가 먼저차고 꽉 차면 100개까지 채우고 그 다음에 200개까지는 큐에 채운다고 생각하지만
        // 코어가 가득차면 큐를 먼처 채우고 큐가 가득차면 MaxPoolSize로 늘리게되는 것이다.

        te.setThreadNamePrefix("myThread");
        te.initialize();
        return te;
    }
}
