package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex7_1SpringDeferredResult {

    Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

    /**
     * DeferredResult 큐
     * - 외부의 이벤트, 클라이언트 요청의해서 기존에 지연되어있는 HTTP 응답을 나중에 쓸 수 있게 해주는 기술
     * - setResult. setException이 호출될 때 까지 응답을 보내지 않고 대기하지만 써블릿 쓰레드는 반환이된다.
     * - 가장 큰 특징은 워커 쓰레드를 따로 만드는게 아닌 Defeered Object만 메모리에 유지되면 그 Object를 불러와 사용할 수 있게된다.
     * - 서블릿 자원을 최소한으로 하면서 동시에 수많은 요청을 처리하는데 편리하게 사용할 수 있다.
     */

    /** 여기로 요청이올 때는 계속 로딩중이였다가 /dr/event?msg=Result를 보내서 setResult가 호출되면 결과가 나타난다.
     *  - 채팅방을 구현할 수 있게 된다.
     *  -
     */
    @GetMapping("/dr")
    public DeferredResult<String> deferredResult() throws InterruptedException {
        log.info("dr");
        DeferredResult<String> dr = new DeferredResult<>();
        results.add(dr);
        return dr;
    }

    @GetMapping("/dr/count")
    public String drcount(){
        return String.valueOf(results.size());
    }

    @GetMapping("/dr/event")
    public String drevent(String msg){
        for(DeferredResult<String> dr : results){
            dr.setResult("Hello " + msg);
            results.remove(dr);
        }
        return "OK";
    }
}
