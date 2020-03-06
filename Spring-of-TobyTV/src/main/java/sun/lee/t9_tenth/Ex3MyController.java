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
// 에러 처리 추가
public class Ex3MyController {

    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";
    public static final String URL3 = "http://localhost:8081/service3?req={req}";
    public static final String ERROR = "http://localhost:8081/error?req={req}";


    AsyncRestTemplate art = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));


    @Autowired
    MyService myService;

    // 에러가 없는 요청
   @GetMapping("/ex3/completion1")
    public DeferredResult<String> restAsync2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex3Completion
                .from(art.getForEntity(URL1, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andApply(s -> art.getForEntity(URL2, String.class, s.getBody()))
                .andError(e -> dr.setErrorResult(e.getMessage())) // andError 이전 어디서라도 에러가 나면 여기서 에러 처리를 하고 싶다, 에러가 안나면 다음으로 넘어간다.
                .andAccept(s -> dr.setResult(s.getBody())); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.

        /** 두가지의 테스트가 필요하다.
         *  1. 에러가 없을 경우 andAccept로 넘어간다.
         *  2. from or and Error에서 에러가 발생하면 andError에서 예외가 발생해야 한다.
         */
        return dr;
    }

    // from에서 에러가 발생 하는 요청
    @GetMapping("/ex3/from/error")
    public DeferredResult<String> restAsyncError1(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex3Completion
                .from(art.getForEntity(ERROR, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andApply(s -> art.getForEntity(URL2, String.class, s.getBody()))
                .andError(e -> dr.setErrorResult(e.getMessage().substring(0, 10))) // andError 이전 어디서라도 에러가 나면 여기서 에러 처리를 하고 싶다, 에러가 안나면 다음으로 넘어간다.
                .andAccept(s -> dr.setResult(s.getBody())); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.

        /** 두가지의 테스트가 필요하다.
         *  1. 에러가 없을 경우 andAccept로 넘어간다.
         *  2. from or and Error에서 에러가 발생하면 andError에서 예외가 발생해야 한다.
         */
        return dr;
    }
    // 첫번째 andApply에서 에러가 발생하는 요
    @GetMapping("/ex3/apply/error")
    public DeferredResult<String> restAsyncError2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");

        Ex3Completion
                .from(art.getForEntity(URL1, String.class, "hello" + idx)) // 비동기 작업에 대한 Completion
                .andApply(s -> art.getForEntity(ERROR, String.class, s.getBody()))
                .andError(e -> dr.setErrorResult(e.getMessage().substring(0, 10))) // andError 이전 어디서라도 에러가 나면 여기서 에러 처리를 하고 싶다, 에러가 안나면 다음으로 넘어간다.
                .andAccept(s -> dr.setResult(s.getBody())); // 앞의 결과값을 받아서 람다식 메서드에 넘겨주고 싶다.

        /** 두가지의 테스트가 필요하다.
         *  1. 에러가 없을 경우 andAccept로 넘어간다.
         *  2. from or and Error에서 에러가 발생하면 andError에서 예외가 발생해야 한다.
         */
        return dr;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

       //  에러도 잘 동작한다.?
//        new LoadTest().test("/ex3/completion1");
//        new LoadTest().test("/ex3/from/error");
        new LoadTest().test("/ex3/apply/error");
    }
}
