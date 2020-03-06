package sun.lee.t8_nineth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
@SpringBootApplication
public class RemoteService {
    @RestController
    public class MyController {
        // 컨트롤러가 또 다른 서버에게 요청을 하는 동작이 있다
        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
            log.info("Service");
            return req + "/service";
        }

        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
            log.info("Service2");
            return req + "/service2";
        }

        @GetMapping("/service3")
        public String service3(String req) throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
            log.info("Service3");
            return req + "/service3";
        }

        @GetMapping("/error")
        public String error(String req) {
            if(true) throw new RuntimeException();
            return req;
        }
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.max-threads", "1000");
        SpringApplication.run(RemoteService.class, args);
    }
}
