package ch3;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
public class SimpleControllerTest extends AbstractDispatcherServletTest {
    @Test
    void helloController() throws Exception{
        setRelativeLocations("spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .initRequest("/hello", RequestMethod.GET)
                .addParameter("name", "Spring")
                .runService()
                .assertModel("message", "Hello Spring")
                .assertViewName("/WEB-INF/view/hello.jsp");

        setRelativeLocations("spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .initRequest("/hello") // 디폴트는 GET 메서드이다.
                .addParameter("name", "Spring")
                .runService()
                .assertModel("message", "Hello Spring")
                .assertViewName("/WEB-INF/view/hello.jsp");

        setRelativeLocations("spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .runService("/hello") // 추가할 파라미터가 없다면 runService에 Uri를 설정하여도 무방
                .assertModel("message", "Hello Spring")
                .assertViewName("/WEB-INF/view/hello.jsp");
    }
}
