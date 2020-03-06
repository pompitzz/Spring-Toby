package sun.lee.t10_eleventh;

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
public class MyComService {

    // CompletableFuture를 쓰면 @Async를 쓸 필요 없다.
    public String work(String req) {
        return req + "/asyncwork";
    }
}
