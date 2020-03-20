package ch3;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
class HelloServletTest extends AbstractDispatcherServletTest{

    @Test
    void helloServletController() throws Exception{
        String str = setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class)
                .initRequest("/hello")
                .addParameter("name", "Spring")
                .runService()
                .getContentAsString();
        assertThat(str).isEqualTo("Hello Spring");
    }

}