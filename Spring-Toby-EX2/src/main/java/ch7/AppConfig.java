package ch7;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHello(mode = "mode1")
public class AppConfig implements HelloConfigurer{
    @Override
    public void configName(Hello hello) {
        hello.setName("JayDen");
    }
}
