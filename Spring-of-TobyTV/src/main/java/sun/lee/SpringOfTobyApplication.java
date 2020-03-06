package sun.lee;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
@SpringBootApplication
@EnableAsync
public class SpringOfTobyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(SpringOfTobyApplication.class)
//                .web(WebApplicationType.NONE)
                .run(args);
    }
}
