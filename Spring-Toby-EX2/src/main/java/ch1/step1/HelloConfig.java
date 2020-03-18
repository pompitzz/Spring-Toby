package ch1.step1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
@Configuration
public class HelloConfig {
    @Bean
    public Hello hello(){
        return new Hello();
    }
}
