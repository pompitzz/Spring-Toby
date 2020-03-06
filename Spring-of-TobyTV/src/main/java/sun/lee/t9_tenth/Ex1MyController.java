package sun.lee.t9_tenth;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import sun.lee.t8_nineth.LoadTest;
import sun.lee.t8_nineth.MyService;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */

/* 콜백헬은 구조적 복잡한 뿐만아니라 에러도 계속해도 처리해줘야하는 불편함이 있다.
 *  - 조금더 구조적으로 제공해 줄 수 있는 클래스르 정의하여 조금 더 깔끔하게 구현해보자
 *
 */
@RestController
@Slf4j
public class Ex1MyController {

    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";
    public static final String URL3 = "http://localhost:8081/service3?req={req}";

    AsyncRestTemplate art = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @Autowired
    MyService myService;

    // 외부 API와 한번의 비동기 통신을 하는 메서드
    @GetMapping("/async/rest/netty/completion1")
    public DeferredResult<String> restAsync(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex1Completion
                .from(art.getForEntity(URL1, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andAccept(s -> dr.setResult(s.getBody())); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.
        return dr;
    }

    // 외부 API와 두번의 비동기 통신을 하는 메서드
    @GetMapping("/async/rest/netty/completion2")
    public DeferredResult<String> restAsync2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex1Completion
                .from(art.getForEntity(URL1, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andApply(s -> art.getForEntity(URL2, String.class, s.getBody()))
                .andApply(s -> art.getForEntity(URL3, String.class, s.getBody()))
                .andAccept(s -> dr.setResult(s.getBody())); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.
        return dr;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // new LoadTest().test("/async/rest/netty/completion1");
        new LoadTest().test("/async/rest/netty/completion2");
    }
}
