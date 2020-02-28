package ch2.step3;

import ch1.User;
import ch2.step2.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("/applicationContext.xml")
public class UserDaoTestStep3 {

    @Autowired
    ApplicationContext context;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void init() throws Exception {
        userDao.deleteAll();
        System.out.println("this.context = " + this.context);
        System.out.println("this" + this);
    }

    @Test
    @DisplayName("test")
    void addAndGet() throws Exception {

        userDao.deleteAll();
        final long count = userDao.getCount();
        assertThat(count).isEqualTo(0);

        final User user = new User();
        final String id = "helloWorld";
        final String name = "dexter";
        final String password = "pass";

        user.setId(id);
        user.setName(name);
        user.setPassword(password);

        userDao.add(user);

        assertThat(userDao.getCount()).isEqualTo(1);

        final User findUser = userDao.get(id);

        assertThat(findUser.getName()).isEqualTo(name);
        assertThat(findUser.getPassword()).isEqualTo(password);
    }

    @Test
    void getCount() throws Exception {
        userDao.deleteAll();

        assertThat(userDao.getCount()).isEqualTo(0);

        for (int i = 1; i < 10; i++) {
            userDao.add(new User(String.valueOf(i), "Foo" + i, "Pass" + i));
            assertThat(userDao.getCount()).isEqualTo(i);
        }
    }

    @Test
    void addAndGet_upgrade() throws Exception {
        userDao.add(new User("1", "Foo1", "Bar1"));
        userDao.add(new User("2", "Foo2", "Bar2"));

        final User user1 = userDao.get("1");
        final User user2 = userDao.get("2");

        assertThat(user1.getName()).isEqualTo("Foo1");
        assertThat(user2.getName()).isEqualTo("Foo2");
    }

    @Test
    void getUserFailure() throws Exception {
        assertThatThrownBy(() -> userDao.get("nwifewfwmdq"))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("해당 아이디의 유저는 존재하지 않습니다");
    }

}