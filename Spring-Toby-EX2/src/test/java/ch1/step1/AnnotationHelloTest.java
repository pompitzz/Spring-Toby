package ch1.step1;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
class AnnotationHelloTest {
    @Test
    void annotationBean() throws Exception{
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("ch1.step1");
        AnnotationHello hello = ctx.getBean("annotationHello", AnnotationHello.class);

        assertThat(hello).isNotNull();
    }
}