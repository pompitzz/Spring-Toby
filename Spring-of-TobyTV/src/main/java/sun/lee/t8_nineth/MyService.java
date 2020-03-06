package sun.lee.t8_nineth;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Service
public class MyService {
    @Async
    public ListenableFuture<String> work(String req) {
        return new AsyncResult<>(req + "/asyncwork");
    }

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10);
        te.setMaxPoolSize(10);
        te.initialize();
        return te;
    }
}
