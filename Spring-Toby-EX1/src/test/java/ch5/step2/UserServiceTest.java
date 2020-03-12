package ch5.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static ch5.step2.UserLevelUpgradeDefault.MIN_LONGCOUNT_FOR_SILVER;
import static ch5.step2.UserLevelUpgradeDefault.MIN_RECCOMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;

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
    public void setUp() {
        users = Arrays.asList(
                new User("user1", "name1", "p1", Level.BASIC, MIN_LONGCOUNT_FOR_SILVER - 1, 0),
                new User("user2", "name2", "p1", Level.BASIC, MIN_LONGCOUNT_FOR_SILVER, 0),
                new User("user3", "name3", "p1", Level.SILVER, 69, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("user4", "name4", "p1", Level.SILVER, 69, MIN_RECCOMEND_FOR_GOLD),
                new User("user5", "name5", "p1", Level.GOLD, 49, 5000)
        );
        userDao.deleteAll();
    }

    @Test
    void upgradeLevels() throws Exception {
        users.forEach(userDao::add);

        userService.upgradeLevels();

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);
    }

    private void checkLevel(User user, boolean isUpgraded) {
        User updatedUser = userDao.get(user.getId());
        if (isUpgraded) {
            assertThat(updatedUser.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else {
            assertThat(updatedUser.getLevel()).isEqualTo(user.getLevel());
        }
    }

    @Test
    void add() throws Exception {
        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User findUserWithLevel = userDao.get(userWithLevel.getId());
        User findUserWithoutLevel = userDao.get(userWithoutLevel.getId());

        assertThat(findUserWithLevel.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(findUserWithoutLevel.getLevel()).isEqualTo(Level.BASIC);
    }

}