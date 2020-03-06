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
public class Ex2_1MyController {


    public static final String URL1 = "http://localhost:8081/service?req={req}";
    AsyncRestTemplate art = new AsyncRestTemplate();

    @GetMapping("/async/rest")
    public ListenableFuture<ResponseEntity<String>> restAsync(int idx) {
        // 콜백을 만들지 않고 바로 반환해도 스프링이 해결해준다.
        // 사실은 백그라운드에 쓰레드를 100개 만들어서 기능을 구현하기 때문에 서버의 자원을 낭비시키는것은 동일하다.
        log.info("Async-Rest");
        return art.getForEntity(URL1, String.class, "hello" + idx);
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // asyncRestTemplate는 약 2초가 걸린다, 사용쓰레드는 1개로 보이지만 내부에서는 쓰레드100개를 만든다.
        new LoadTest().test("/async/rest");
    }

}
