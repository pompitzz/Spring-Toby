package ch2.step2;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
class UserDaoTestStep2 {

    private final ApplicationContext context = new AnnotationConfigApplicationContext(ch2.step2.DaoFactory.class);
    private final UserDao userDao = context.getBean("userDao", UserDao.class);

    @Test
    void getUserFailure() throws Exception {
        assertThatThrownBy(() -> userDao.get("nwifewfwmdq"))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("해당 아이디의 유저는 존재하지 않습니다");
    }

}