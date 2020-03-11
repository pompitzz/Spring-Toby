package ch5.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/ch5/step2/applicationContext.xml")
class UserDaoTest {

    @Autowired
    UserDao userDao;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void setup() {
        userDao.deleteAll();
        this.user1 = new User("korea", "이동명", "password", Level.SILVER, 55, 10);
        this.user2 = new User("Hehe", "김뚜깡", "password", Level.BASIC, 1, 0);
        this.user3 = new User("HelloWorld", "홍길동", "password", Level.GOLD, 100, 40);
    }

    @Test
    @DisplayName("test")
    void addAndGet() throws Exception {
        userDao.add(this.user1);
        userDao.add(this.user2);
        User user1 = userDao.get(this.user1.getId());
        User user2 = userDao.get(this.user2.getId());

        checkSameUser(user1, this.user1);
        checkSameUser(user2, this.user2);
    }

    private void checkSameUser(User user1, User user2){
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }

    @Test
    void update() throws Exception{
        userDao.add(this.user1);

        user1.setName("Changed Name");
        user1.setPassword("cpass");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        userDao.update(user1);

        // user1은 매 테스트 케이스마다 UserDaoTest의 새로운 오브젝트가 만들어지므로 바로 수정을하더라도 문제가 없다.
        User user1update = userDao.get(user1.getId());
        checkSameUser(user1, user1update);
    }

    @Test
    void updateWithWhereAssertion() throws Exception{
        userDao.add(this.user1);
        userDao.add(this.user2);

        user1.setName("Changed Name");
        user1.setPassword("cpass");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        userDao.update(user1);

        User user1update = userDao.get(user1.getId());
        checkSameUser(user1, user1update);

        User user2same = userDao.get(user2.getId());
        checkSameUser(user2, user2same);
    }
}