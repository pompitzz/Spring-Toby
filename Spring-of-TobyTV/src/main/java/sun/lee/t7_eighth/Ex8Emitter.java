package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
@RestController
public class Ex8Emitter {

    /* Emitter는 HTTP 하나의 요청에 데이터를 하나만 보내는게 아닌 여러번에 나누어서 보낼 수 있는 방법을 제공해준다.
    *  - 해당 url로 요청을 해보면 하나의 요청에 나뉘어서 순서대로 응답되는 것을 알 수 있다.
    */
    @GetMapping("/emitter")
    public ResponseBodyEmitter deferredResult() throws InterruptedException {
        ResponseBodyEmitter em = new ResponseBodyEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            for (int i = 0; i <= 50; i++) {
                try {
                    em.send("<p>Stream " + i + "</p>");
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return em;
    }

}
