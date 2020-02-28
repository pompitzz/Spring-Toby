package ch2.step4;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/junit.xml")
public class ApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    private static ApplicationContext contextObject;
    @Test
    @Order(0)
    void test1() throws Exception{
        assertThat(contextObject == null).isTrue();
        contextObject = this.context;
    }

    @Test
    @Order(1)
    void test2() throws Exception{
        assertThat(contextObject == this.context).isTrue();
        contextObject = this.context;

    }

    @Test
    @Order(2)
    void test3() throws Exception{
        assertThat(contextObject == this.context).isTrue();
        contextObject = this.context;
    }
}
