package sun.lee.t8_nineth;

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

import java.util.concurrent.BrokenBarrierException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex4_1MyController {

    public static final String URL1 = "http://localhost:8081/service?req={req}";
    public static final String URL2 = "http://localhost:8081/service2?req={req}";

    AsyncRestTemplate art =
            new AsyncRestTemplate(
                    new Netty4ClientHttpRequestFactory(
                            new NioEventLoopGroup(1)
                    ));


    @GetMapping("/async/rest/netty/callbackhell")
    public DeferredResult<String> restAsyncUsingNettyCallbackHell(int idx) {
        // 두번의 콜백 동작도 약 4초면 동작된다.
        // 즉 여러번의 콜백을 연달아 요청할 수 있다.
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");
        ListenableFuture<ResponseEntity<String>> f1 = art.getForEntity(URL1, String.class, "hello" + idx);
        f1.addCallback(
                s -> {
                    ListenableFuture<ResponseEntity<String>> f2 = art.getForEntity(URL2, String.class, s.getBody());
                    f2.addCallback(
                            s2 -> {
                                dr.setResult(s2.getBody());
                            },
                            e2 -> {
                                dr.setErrorResult(e2.getMessage());
                            }
                    );
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                });
        return dr;
    }

    @Autowired
    MyService myService;

    @GetMapping("/async/rest/netty/callbackhell2")
    public DeferredResult<String> restAsyncUsingNettyCallbackHell2(int idx) {
        // 외부 API2번 요청하고 내부 비동기 작업을 진행한다.
        // 이렇게 하더라도 약 4초대면 100개의 요청 동작이 완료된다.
        // 모두 내부에서 논블록킹 비동기 방식으로 동작이 일어나기 때문에 쓰레드를 많이 사용하지 않고 작업을 수행할 수 있다.
        
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");
        ListenableFuture<ResponseEntity<String>> f1 = art.getForEntity(URL1, String.class, "hello" + idx);
        f1.addCallback(
                s -> {
                    ListenableFuture<ResponseEntity<String>> f2 = art.getForEntity(URL2, String.class, s.getBody());
                    f2.addCallback(
                            s2 -> {
                                ListenableFuture<String> f3 = myService.work(s2.getBody());
                                f3.addCallback(
                                        s3 -> {
                                            dr.setResult(s3);
                                        },
                                        e3 -> {
                                            dr.setErrorResult(e3.getMessage());
                                        }
                                );
                            },
                            e2 -> {
                                dr.setErrorResult(e2.getMessage());
                            }
                    );
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                });
        return dr;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
         new LoadTest().test("/async/rest/netty/callbackhell");
         // new LoadTest().test("/async/rest/netty/callbackhell2");
    }
}
