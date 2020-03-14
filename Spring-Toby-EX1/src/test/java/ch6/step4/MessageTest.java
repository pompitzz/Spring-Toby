package ch6.step4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/ch6/step4/applicationContext.xml")
class MessageTest {

    @Autowired
    ApplicationContext context;

    @Test
    void factoryBean() throws Exception{
        // &를 붙이면 FactoryBean자체를 반환해준다.
        Object factory = context.getBean("&message");
        assertThat(factory).isInstanceOf(FactoryBean.class);

        // id만 붙인다면 Message 객체를 반환해 줄 것이다.
        Object message1 = context.getBean("message");
        assertThat(message1).isInstanceOf(Message.class);
        assertThat(((Message) message1).getText()).isEqualTo("Factory Bean");

        // 싱글톤을 false로 설정하였기 때문에 서로 다른 객체 참조를 가질 것이다.
        Object message2 = context.getBean("message");
        assertThat(message1).isNotEqualTo(message2);
    }
}