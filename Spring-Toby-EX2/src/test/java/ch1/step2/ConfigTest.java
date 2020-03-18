package ch1.step2;


import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
class ConfigTest {

    @Test
    void config() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        Hello hello = ctx.getBean("hello", Hello.class);
        assertThat(hello.getPrinter()).isNotNull();
    }
}