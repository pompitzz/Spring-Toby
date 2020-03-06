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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex3_1MyController {


    public static final String URL1 = "http://localhost:8081/service?req={req}";
    RestTemplate rt = new RestTemplate();
    AsyncRestTemplate art =
            new AsyncRestTemplate(
                    new Netty4ClientHttpRequestFactory(
                            new NioEventLoopGroup(1)
                    ));


    @GetMapping("/async/rest/netty")
    public ListenableFuture<ResponseEntity<String>> restAsyncUsingNetty(int idx) {
        // 쓰레드를 한개만 가지는 ART를 만들었다.
        // ART를 쓰면 내부적으로도 쓰레드를 만들지 않기 때문에 실제 서버의 자원을 더 효율적으로 사용할 수 있게 된다.
        log.info("Async-Rest-Netty");
        return art.getForEntity(URL1, String.class, "hello" + idx);
    }

    @GetMapping("/async/rest/netty2")
    public DeferredResult<String> restAsyncUsingNetty2(int idx) {
        // DeferredResult로 추가적인 작업이 가능하다.
        DeferredResult<String> dr = new DeferredResult<>();
        log.info("Async-Rest-Netty");
        ListenableFuture<ResponseEntity<String>> f1 = art.getForEntity(URL1, String.class, "hello" + idx);
        f1.addCallback(
                s -> {
                    // 추가작업
                    dr.setResult(s.getBody() + "/work");
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                });
        return dr;
    }


    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // 네티를 사용하면 내부 쓰레드를 만들지 않는다.
//        new LoadTest().test("/async/rest/netty");

        // DeferredResult로 추가적인 작업이 가능하다.
         new LoadTest().test("/async/rest/netty2");
    }
}
