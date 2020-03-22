package ch3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
class SimpleHandlerAdapterTest extends AbstractDispatcherServletTest{
    @Test
    void simpleHandler() throws Exception{
        setClasses(SimpleHandlerAdapter.class, HelloController.class);
        initRequest("/hello").addParameter("name", "Spring").runService();
        assertViewName("/WEB-INF/view/hello.jsp");
        assertModel("message", "Hello Spriœng");
    }
}