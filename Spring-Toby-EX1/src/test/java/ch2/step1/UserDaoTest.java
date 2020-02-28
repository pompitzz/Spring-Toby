package ch2.step1;

import ch1.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
class UserDaoTest {

    private final ApplicationContext context = new AnnotationConfigApplicationContext(ch2.step1.DaoFactory.class);
    private final UserDao userDao = context.getBean("userDao", UserDao.class);

    @BeforeEach
    void init() throws Exception {
        userDao.deleteAll();
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
    void get() throws Exception {
        assertThatThrownBy(() -> userDao.get("1"))
                .isInstanceOf(SQLException.class);

    }

}