package ch1.step2;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
class HelloTest {

    @Test
    void GXAC() throws Exception {
        GenericXmlApplicationContext ac = new GenericXmlApplicationContext("resourceContext.xml");
        ac.getBean("hello", Hello.class).print();
        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }

}