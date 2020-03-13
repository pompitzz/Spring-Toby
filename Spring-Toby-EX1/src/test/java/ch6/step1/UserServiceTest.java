package ch6.step1;

import ch5.step2.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ch5.step2.UserLevelUpgradeDefault.MIN_LONGCOUNT_FOR_SILVER;
import static ch5.step2.UserLevelUpgradeDefault.MIN_RECCOMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/13
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/ch6/step1/applicationContext.xml")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    @Autowired
    UserLevelUpgradePolicy userLevelUpgradePolicy;

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

    // 목 오브젝트로 만들기 (1)
    @Mock
    UserDao mockUserDao;

    @Test
    void upgradeLevels() throws Exception {
        // @Mock으로 설정된 목 오브젝트를 사용한다.
        MockitoAnnotations.initMocks(this);

        // 스터빙을 이렇게 수행할 수 있다.
        when(mockUserDao.getAll()).thenReturn(users);

        // 목 오브젝트로 만들기 (2)
        MailSender mockMailSender = mock(MailSender.class);

        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, userLevelUpgradePolicy, mockMailSender);

        userServiceImpl.upgradeLevels();

        // 업그레이드가 제대로 되었는지 확인해본다.
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        // 목 오브젝트 Dao에 업그레이드 용청이 총 두번 온것을 확인할 수 있다.
        verify(mockUserDao, times(2)).update(any(User.class));

        // 각 요청들에 넘어오면 파라미터가 무었인지 확인할 수 있다.
        verify(mockUserDao).update(users.get(1));
        verify(mockUserDao).update(users.get(3));

        // Argumentcaptor를 통해 목 오브젝트에 전달받은 파라미터를 가져와서 검증할 수 있다.
        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // 목 오브젝트인 mailSender의 send는 총 두번 호출되고 각 파라미터를 capture를 통해 가져온다.
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());

        // 파라미터인 SimpleMailMessage에서 값들을 검증하여 제대로 메일이 전송되었는지 확인해본다.
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(Objects.requireNonNull(mailMessages.get(0).getTo())[0]).isEqualTo(users.get(1).getEmail());
        assertThat(Objects.requireNonNull(mailMessages.get(1).getTo())[0]).isEqualTo(users.get(3).getEmail());
    }

    private void checkLevel(User user, String id, Level level) {
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getLevel()).isEqualTo(level);
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
            if (user.getId().equals(id)) throw new UserServiceTest.TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

    @Test
    void upgradeAllOrNothing() throws Exception {
        users.forEach(userDao::add);
        String id = users.get(3).getId();
        UserService userServiceImpl = new UserServiceImpl(this.userDao, new TestUserLevelUpgradePolicy(id), mailSender);
        UserService testUserService = new UserServiceTx(userServiceImpl, transactionManager);

        assertThatThrownBy(() -> testUserService.upgradeLevels())
                .isInstanceOf(UserServiceTest.TestUserServiceException.class);

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

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        @Override
        public void update(User user) {

            updated.add(user);
        }

        @Override
        public List<User> getAll() {
            return this.users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getCount() {
            throw new UnsupportedOperationException();
        }
    }
}