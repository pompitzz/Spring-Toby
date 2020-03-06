package sun.lee.t10_eleventh;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.WebClient;
import sun.lee.t8_nineth.LoadTest;
import sun.lee.t8_nineth.MyService;
import sun.lee.t9_tenth.Ex4Completion;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;

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
public class Ex3CompotableFutureController {

    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";
    public static final String URL3 = "http://localhost:8081/service3?req={req}";
    public static final String ERROR = "http://localhost:8081/error?req={req}";


    AsyncRestTemplate art = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));


    @Autowired
    MyComService myComService;

    @GetMapping("/completable/future")
    public DeferredResult<String> restAsync2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");
        // ListenableFuture<ResponseEntity<String>> f1 = art.getForEntity(URL1, String.class, "hello" + idx);
        // 이 부분을 CompletableFuture를 사용할 수 없다.
        // CompletableFuture로 감싸게 만들어서 사용하면된다.


        // 이 코드자체는 한번에 호출되고 끝난다.
        toCF(art.getForEntity(ERROR, String.class, "hello" + idx))
                // 또 CompotableFuture로 리턴해야하기 때문에 Compose를 사용한다.
                .thenCompose(s -> toCF(art.getForEntity(URL2, String.class, s.getBody())))
                //.thenApply(s -> myComService.work(s.getBody())) // 계속 쓰레드를 물고 있게되므로 독립적인 쓰레드풀에서 동작하도록 정의하자.
                .thenApplyAsync(s -> myComService.work(s.getBody()))
                .thenAccept(s -> dr.setResult(s))
                .exceptionally(e -> {dr.setErrorResult(e); return (Void)null;});

        /** 스프링 4.0부터 비동기 통신 방식은 이렇게 구현하면 된다.
         *  - 여기서 프로듀서, 컨슈머의 속도차륿 백프레셔로 완화시키고 더욱 더 세밀하고 기능이 많게 구현하고 싶다면 리액티브 프로그래밍을 이용하면된다.
         */

        return dr;
    }

    private <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<>(); // 이 자체가 비동기 작업이 아니고 결과를 나타내는 인터페이스이다.
        // CompletableFuture로 간단하게 전환할 수 있다.
        lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
        return cf;
    }


    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        new LoadTest().test("/completable/future");
    }
}
