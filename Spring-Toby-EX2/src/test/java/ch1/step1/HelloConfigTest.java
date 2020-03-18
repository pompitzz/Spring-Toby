package ch1.step1;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
class HelloConfigTest {
    @Test
    void config() throws Exception{
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(HelloConfig.class);
        HelloConfig helloConfig = ctx.getBean("helloConfig", HelloConfig.class);
        Hello hello = ctx.getBean("hello", Hello.class);

        assertThat(hello == helloConfig.hello()).isTrue();
    }
}