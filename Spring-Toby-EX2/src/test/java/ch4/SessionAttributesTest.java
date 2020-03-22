package ch4;

import ch3.AbstractDispatcherServletTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
public class SessionAttributesTest extends AbstractDispatcherServletTest {
    @Test
    void sessionAttr() throws Exception{
        setClasses(UserController.class);

        // ==================== GET 요청 - form() ====================
        initRequest("/user/edit")
                .addParameter("id", "1")
                .runService();
        HttpSession session = request.getSession();

        assertThat(session.getAttribute("user"))
                .as("세션의 User는 Model의 User와 동일하다.")
                .isEqualTo(getModelAndView().getModel().get("user"));

        // ==================== POST 요청 - submit() ====================
        initRequest("/user/edit", "POST")
                .addParameter("id", "1")
                .addParameter("name", "Spring2");

        // 세션을 유지시키기 위해 직접 세션을 넣어준다
        request.setSession(session);
        runService();

        assertThat(((User)getModelAndView().getModel().get("user")).getEmail())
                .as("email은 수정되지 않았지만 세션에 저장된 user의 이메일이 반영되어있다.")
                .isEqualTo("test@gmail.com");

        assertThat(session.getAttribute("user"))
                .as("SessionStatus를 통해 세션에 저장된 user가 제거된다.")
                .isNull();
    }

    @Controller
    @SessionAttributes("user")
    static class UserController {
        @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
        public User form(@RequestParam int id) {
            return new User(1, "Spring", "test@gmail.com");
        }

        @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
        public void submit(@ModelAttribute User user, SessionStatus sessionStatus){
            sessionStatus.setComplete();
        }
    }

    @Test
    void charsetEditor() throws Exception{
        CharsetEditor charsetEditor = new CharsetEditor();
        charsetEditor.setAsText("UTF-8");
        assertThat(charsetEditor.getValue()).isInstanceOf(Charset.class);
        assertThat((Charset) charsetEditor.getValue()).isEqualTo(StandardCharsets.UTF_8);
    }

@Test
void webDataBinder() throws Exception{
    WebDataBinder dataBinder = new WebDataBinder(null);
    dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
    assertThat(dataBinder.convertIfNecessary("1", Level.class)).isEqualTo(Level.BASIC);
}
}
