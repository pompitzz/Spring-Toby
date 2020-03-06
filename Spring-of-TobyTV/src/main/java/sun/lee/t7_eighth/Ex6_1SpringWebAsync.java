package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class Ex6_1SpringWebAsync {

    @GetMapping("/async")
    public String async() throws InterruptedException{
        log.info("async");
        TimeUnit.SECONDS.sleep(2);
        return "Hello";
    }

    @GetMapping("/callable")
    public Callable<String> callable() {
        log.info("Callable");
        return () -> {
            log.info("async");
            TimeUnit.SECONDS.sleep(2);
            return "Hello";
        };
    }
}
