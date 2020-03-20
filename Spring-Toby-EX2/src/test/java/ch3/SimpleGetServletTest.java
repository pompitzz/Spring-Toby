package ch3;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/20
 */
class SimpleGetServletTest {

    @Test
    void mockServlet() throws Exception{
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        req.addParameter("name", "Spring");

        MockHttpServletResponse res = new MockHttpServletResponse();

        SimpleGetServlet simpleGetServlet = new SimpleGetServlet();
        simpleGetServlet.service(req, res);

        assertThat(res.getContentAsString()).isEqualTo("<HTML><BODY>Hello Spring</BODY></HTML>");
        assertThat(res.getContentAsString().contains("Hello Spring")).isTrue();
    }

@Test
void controller() throws Exception{
    ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();

    // 서블릿 컨텍스트를 지정하고 클래스들도 직접 지정해준다.
    servlet.setRelativeLocations(getClass(), "spring-servlet.xml");
    servlet.setClasses(HelloSpring.class);

    // mockConfig로 초기화 해준다.
    servlet.init(new MockServletConfig("spring"));

    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
    req.addParameter("name", "Spring");
    MockHttpServletResponse res = new MockHttpServletResponse();

    // /hello로 요청을 보내면 HelloController가 실행될 것이다.
    servlet.service(req, res);

    // 컨트롤러가 실행될 떄의 ModelAndView이다
    ModelAndView modelAndView = servlet.getModelAndView();
    assertThat((String) modelAndView.getModel().get("message")).isEqualTo("Hello Spring");
}

}