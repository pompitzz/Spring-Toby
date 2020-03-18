package ch1.step2;

import ch1.step1.Printer;
import ch1.step1.StringPrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
@Configuration
public class Config {
    @Bean
    public Hello hello(Printer printer) {
        Hello hello = new Hello();
        hello.setPrinter(printer);
        return hello;
    }

    @Bean
    public Printer printer(){
        return new StringPrinter();
    }
}
