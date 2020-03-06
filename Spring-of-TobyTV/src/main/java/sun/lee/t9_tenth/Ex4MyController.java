package sun.lee.t9_tenth;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import sun.lee.t8_nineth.LoadTest;
import sun.lee.t8_nineth.MyService;

import java.util.concurrent.BrokenBarrierException;
import java.util.function.Consumer;
import java.util.function.Function;

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
// 내부 비동기 로직 추가
public class Ex4MyController {

    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";
    public static final String URL3 = "http://localhost:8081/service3?req={req}";
    public static final String ERROR = "http://localhost:8081/error?req={req}";


    AsyncRestTemplate art = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));


    @Autowired
    MyService myService;

    @GetMapping("/ex4/use/service")
    public DeferredResult<String> restAsync2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex4Completion
                .from(art.getForEntity(URL1, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andApply(s -> art.getForEntity(URL2, String.class, s.getBody()))
                .andApply(s -> myService.work(s.getBody()))// 내부 비동기 요청은 타입이 다르므로 제네릭으로 작성해보자.
                .andError(e -> dr.setErrorResult(e)) // andError 이전 어디서라도 에러가 나면 여기서 에러 처리를 하고 싶다, 에러가 안나면 다음으로 넘어간다.
                .andAccept(s -> dr.setResult(s)); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.

        return dr;
    }




    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        new LoadTest().test("/ex4/use/service");

        // 이러한 기능을 자바8에서 제공해준다.
    }
}
