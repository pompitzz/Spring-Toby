package sun.lee.t8_nineth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex1_1MyController {
    public static final String URL1 = "http://localhost:8081/service?req={req}";
    RestTemplate rt = new RestTemplate();

    // 컨트롤러가 또 다른 서버에게 요청을 하는 동작이 있다
    @GetMapping("/rest")
    public String rest(int idx) {
        // 이 요청은 블록킹 동작이기 때문에 요청마다 새롭게 쓰레드를 만든다.
        // 그렇기 때문에 요청이 백개 날라오면 현재 쓰레드가 한개이기 때문에 2초에 하나씩만 처리가 가능해진다.
        // CPU는 제대로 일을하지 않고 요청을 응답받을 때 까지 놀게 된다.
        // 그렇기 때문에 현재 쓰레드를 계속 물고있지 않고 쓰레드를 반환하게 해줘야한다.
        // 이를 스프링 3.0에서 제대로 해결할 수 없었다.
        // Deferred Result는 외부에서 별개의 이벤트, Callable은 백그라운드에서 Working Thread륾 만들어야 한다.
        // 이를 해결하기 위해 스프링 4.0부터 AsyncRestTemplate가 생겨났다.
        log.info("Sync-Rest");
        String res = rt.getForObject(URL1, String.class, "hello" + idx);
        return res;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // RestTemplate는 하나당 2초이므로 2 * 100 초가걸릴것 이다.
        new LoadTest().test("/rest");
    }
}
