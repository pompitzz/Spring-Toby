package sun.lee.t11_twelfth;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sun.lee.t10_eleventh.MyComService;
import sun.lee.t8_nineth.LoadTest;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex1WebFluxController {
    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";
    public static final String URL3 = "http://localhost:8081/service3?req={req}";
    public static final String ERROR = "http://localhost:8081/error?req={req}";


    @Autowired
    MyComService myComService;

    WebClient client = WebClient.create();

    @GetMapping("/webflux")
    public Mono<String> webflux(int idx) {
        // 반환타입이 DeferredResult, Cllable였지만 리액티브에서는 Mono, Flux 두가지로 리턴한다.
        // Mono는 파라미터를 한번에 던지고 리턴값을 한번에 받을때 사용하기 적합하다.
        // 웹플럭스는 서블릿 기반이 아니므로 톰캣을 사용하지 않아도 된다. 그러므로 웹플럭스의 경우 기본이 네티이다.
        // 언더토우와 네티는 서블릿 컨테이너가 아닌 http서버 or 클라이언트 서비스를 제공하는 네트워크 라이브러리 or Http 서비스 라이브러리이다.

        // List, Optional과 같이 컨테이너에 담아놓으면 다양한 기능들을 사용할 수 있듯이 Mono에 값들을 넣어놓으면 다양한 기능들을 제공받을 수 있다.

        // 이렇게 정의하는 것만으로는 호출이 되지 않는다.
        // 왜? Mono는 CorePublisher를 구현하고 있다. 즉 Mono는 Publisher의 일종이므로 Subsriber가 Subscribe해야 실행이 된다.
        // Subscribe작업은 스프링 알아서 처리해준다.
        Mono<ClientResponse> res = client.get()
                                        .uri(URL1, idx)
                                        .exchange();

        // map을하면 Mono<Mono<>>가 된다.
        // 왜냐하면 bodyToMono는 Mono<String>으로 반환한다. 그리고 map이기 때문에 또 Mono를 감싸게 된다.
        // 그러므로 flatMap을 사용하면 된다!

        Mono<String> body = res.flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
        return body;
        // 아래와 같이 매우 간단하게 동작된다.
    }

    @GetMapping("/webflux2") // API 한번 연결하
    public Mono<String> webflux2(int idx) {
        // AsyncRestTemplate, DeferredResult로 비동기 방식으로 구현한거 처럼 비동기로 동작된다.
        return client.get().uri(URL1, idx).exchange()
                     .flatMap(c -> c.bodyToMono(String.class));
    }

    @GetMapping("/webflux3") // API 두번 연결하기
    public Mono<String> webflux3(int idx) {
        // AsyncRestTemplate와 동일하게 4초대에 동작된다.
        return client.get().uri(URL1, idx).exchange()
                     .flatMap(c -> c.bodyToMono(String.class))
                     .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                     .flatMap(c -> c.bodyToMono(String.class));
    }

    @Autowired
    MyTempService myTempService;

    @GetMapping("/webflux4") // API 두번 연결하기, 내부 비동기 작업
    public Mono<String> webflux4(int idx) {
        // AsyncRestTemplate와 동일하게 4초대에 동작된다.
        return client.get().uri(URL1, idx).exchange()
                     .flatMap(c -> c.bodyToMono(String.class))
                     .doOnNext(c -> log.info("First : {}", c.toString()))
                     .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                     .flatMap(c -> c.bodyToMono(String.class))
                     .doOnNext(c -> log.info("Second : {}", c.toString()))
                     .flatMap(res2 -> Mono.fromCompletionStage(myTempService.work(res2)))
                ;
                // myComService가 복잡한 로직이라면 I/O를 처리하는 쓰레드를 반납하지 못하니깐 또 다른 쓰레드에서 작업하도록 비동기 설정을해야 한다.
                // @Async로 사용하면 CompletableFuture, ListenableFuture로 반환해야 할 것이다.
                // fromCompletionStage는 CompletableFuture를 Mono로 변경해준다.
    }

    @GetMapping("/webflux5") // API 두번 연결하기, 내부 동기 작업
    public Mono<String> webflux5(int idx) {
        // AsyncRestTemplate와 동일하게 4초대에 동작된다.
        return client.get().uri(URL1, idx).exchange()
                     .flatMap(c -> c.bodyToMono(String.class))
                     .doOnNext(c -> log.info("First : {}", c.toString()))
                     .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                     .flatMap(c -> c.bodyToMono(String.class))
                     .doOnNext(c -> log.info("Second : {}", c.toString()))
                     .map(res2 -> myTempService.work2(res2));

        // myComService가 복잡한 로직이라면 I/O를 처리하는 쓰레드를 반납하지 못하니깐 또 다른 쓰레드에서 작업하도록 비동기 설정을해야 한다.
        // @Async로 사용하면 CompletableFuture, ListenableFuture로 반환해야 할 것이다.
        // fromCompletionStage는 CompletableFuture를 Mono로 변경해준다.
    }

    @Service
    public static class MyTempService{
        @Async
        public CompletableFuture<String> work(String req) {
            log.info("work : {}", req);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(req + "/asyncwork");
        }

        public String work2(String req) {
            log.info("work2 : {}", req);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return req + "/asyncwork";
        }
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        new LoadTest().test("/webflux5");
    }
}
