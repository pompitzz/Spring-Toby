package ch7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloConfig{
    @Autowired(required = false)
    HelloConfigurer helloConfigurer;

    @Bean
    public Hello hello(){
        Hello hello = new Hello();
        hello.setName("Spring");
        if (helloConfigurer != null) helloConfigurer.configName(hello);
        return hello;
    }
}

class HelloConfig1{

}
class HelloConfig2{

}
