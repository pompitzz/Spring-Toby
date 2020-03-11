package ch5.step2;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/ch5/step2/applicationContext.xml")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    List<User> users;

    @BeforeEach
    public void setUp(){
        users = Arrays.asList(
                new User("user1", "name1", "p1", Level.BASIC, 49, 0),
                new User("user2", "name2", "p1", Level.BASIC, 50, 0),
                new User("user3", "name3", "p1", Level.SILVER, 69, 29),
                new User("user4", "name4", "p1", Level.SILVER, 69, 30),
                new User("user5", "name5", "p1", Level.GOLD, 49, 0)
        );
    }

    @Test
    void upgradeLevels() throws Exception{
        userDao.deleteAll();
        users.forEach(userDao::add);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level level){
        User findUser = userDao.get(user.getId());
        assertThat(findUser.getLevel()).isEqualByComparingTo(level);
    }
}