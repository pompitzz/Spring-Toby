package ch5.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ch5.step2.UserLevelUpgradeDefault.MIN_LONGCOUNT_FOR_SILVER;
import static ch5.step2.UserLevelUpgradeDefault.MIN_RECCOMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(locations = "file:src/main/java/ch5/step2/applicationContext.xml")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
                new User("user1", "name1", "p1", Level.BASIC, MIN_LONGCOUNT_FOR_SILVER - 1, 0, "test@gmail.com"),
                new User("user2", "name2", "p1", Level.BASIC, MIN_LONGCOUNT_FOR_SILVER, 0, "test@gmail.com"),
                new User("user3", "name3", "p1", Level.SILVER, 69, MIN_RECCOMEND_FOR_GOLD - 1, "test@gmail.com"),
                new User("user4", "name4", "p1", Level.SILVER, 69, MIN_RECCOMEND_FOR_GOLD, "test@gmail.com"),
                new User("user5", "name5", "p1", Level.GOLD, 49, 5000, "test@gmail.com")
        );
        userDao.deleteAll();
    }

    @Test
    void upgradeLevels() throws Exception {
        users.forEach(userDao::add);

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();
        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
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

    static class TestUserLevelUpgradePolicy extends UserLevelUpgradeDefault {
        private String id;

        public TestUserLevelUpgradePolicy(String id) {
            this.id = id;
        }


        @Override
        public void upgradeLevel(User user) {
            if (user.getId().equals(id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

    @Test
    void upgradeAllOrNothing() throws Exception {
        users.forEach(userDao::add);
        String id = users.get(3).getId();
        UserService testUserService = new UserService(this.userDao, new TestUserLevelUpgradePolicy(id), transactionManager, mailSender);

        assertThatThrownBy(() -> testUserService.upgradeLevels())
                .isInstanceOf(TestUserServiceException.class);

        checkLevel(users.get(1), false);
        checkLevel(users.get(3), false);
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            requests.add(Objects.requireNonNull(simpleMessage.getTo())[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {

        }
    }
}