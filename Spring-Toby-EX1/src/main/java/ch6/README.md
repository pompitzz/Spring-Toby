# Chapter6. AOP
- AOP는 스프링의 3대 기반기술 중 하나이지만 가장 이해하기 힘든 기술이라는 악명이 있다.
- 그렇기 때문에 더더욱 스프링이 왜 이것을 도입했으며 3대 기반기술 중 하나인지, 이를 통해 얻을 수 있는 장점이 무엇인지에 대한 충분한 이해가 필요하다.
- **스프링에 적용된 가장 인기 있는 AOP의 적용 대상은 바로 선언적 트랜잭션 기능이다.**
- 5장에서 추상화를 통해 근본적인 문제들을 해결했던 트랜잭션 경계 설정 기능을 AOP를 통해 더욱 더 깔끔하게 변경시킬 수 있다.

## 6.1 트랜잭션 코드의 분리
- 서비스 추상화를 통해 기술과 환경에 독립적인 UserService를 만들었지만 트랜잭션 경계설정을 위해 넣은 코드때문에 깔끔해지지 않은 것은 어쩔 수 없었다.
### 6.1.1 메서드 분리
```java
public void upgradeLevels() throws SQLException {
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    try {
        // ------------------ 비즈니스 로직 ---------------------- //
        List<User> users = userDao.getAll();
        users.forEach(user -> {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
                sendUpgradeEmail(user);
            }
        });
        // --------------------------------------------------- //
        transactionManager.commit(status);
    } catch (Exception e) {
        transactionManager.rollback(status);
        throw e;
    }
}
```
- UserService의 upgradeLevels 메서드를 보면 중간에 존재하는 비즈니스 로직을 제외한 부분들은 트랜잭션 경계설정을 위한 코드인것을 알 수 있다.
- 구조를 보면 비즈니스 로직 사이에 트랜잭션의 경계설정인 시작과 종료를 담당하는 코드가 위치하고 있는 것을 알 수 있다.
- 이 구조의 특징은 반드시 필요한 코드들이지만 비즈니스 로직과 트랜잭션의 경계설정은 서로간의 주고 받는 정보가 없는 독립적인 코드인것을 알 수 있다.
- 그러므로 이는 메서드를 통해 분리할 수 있을 것이다.

<br>

```java
public void upgradeLevels() throws SQLException {
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    try {
        upgradeLevelsInternal();
        transactionManager.commit(status);
    } catch (Exception e) {
        transactionManager.rollback(status);
        throw e;
    }
}

private void upgradeLevelsInternal() {
    userDao.getAll().forEach(user -> {
        if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
            userLevelUpgradePolicy.upgradeLevel(user);
            userDao.update(user);
            sendUpgradeEmail(user);
        }
    });
}
```

- 분리를 하면 이전보다는 깔끔해진것 같지만 트랜잭션이 필요한 모든 메서드들에 이러한 방식을 적용하기에는 무리가 있을 것 같다.

### 6.1.2 DI를 이용한 클래스 분리
- DI를 적용하여 트랜잭션 코드를 클래스 밖으로 뽑아낼 순 없을까?
- 현재 UserService는 UserServiceTest가 클라이언트가 되어서 사용되고 있다.
- 실제 웹서버에서는 다른 클래스나 모듈에서 UserService를 호출하여 사용할 것이다.
- 현재 UserService는 클래스로 구성되어 있으니 직접참조를 하고 있다.
- UserService를 인터페이스로 만들어 관계를 약하게 한 후 클라이언트와 UserService사이에 트랜잭션의 경계설정에 대한 책임을 갖는 클래스를 구현하여 이를 해결할 수 있을 것이다.

#### UserService 인터페이스 도입
```java
public interface UserService {
    void add(User user);
    void upgradeLevels();
}
```
- 먼저 UserService 인터페이스를 통해 핵심 기능들을 정의하자. UserService 인터페이스는 add, upgradeLevels 두가지 기능만 가지고 있다.

<br>

```java
@Setter
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    private MailSender mailSender;

    public UserServiceImpl(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy, MailSender mailSender) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
        this.mailSender = mailSender;
    }

    @Override
    public void upgradeLevels() {
        userDao.getAll().forEach(user -> {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
                sendUpgradeEmail(user);
            }
        });
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@gmail.com");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자 님의 등급이" + user.getLevel().name() + "로 업그레이드 되었습니다.");

        this.mailSender.send(mailMessage);
    }

    @Override
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
```
- UserService를 실체화하는 UserServiceImpl에는 트랜잭션 경계설정 관련 코드는 모두 삭제하고 실제 비즈니스 로직인 유저들을 업그레이드 시키는 로직만을 담는다.
- 그러므로 transactionManager는 필요 없어지기 때문에 의존성에서 제외시켜주었다.

<br>

```java
public class UserServiceTx implements UserService {
    private UserService userService;
    private PlatformTransactionManager transactionManager;

    public UserServiceTx(UserService userService, PlatformTransactionManager transactionManager) {
        this.userService = userService;
        this.transactionManager = transactionManager;
    }

    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userService.upgradeLevels();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
```
- 또 다른 UserService를 실체화하는 UserServiceTx에서는 트랜잭션 경계설정 관련 코드를 작성하고 UserService를 의존하고 있다.
- 여기서는 트랜잭션 경계설정 사이 비즈니스 로직이 들어가는자리에 의존하는 userService에게 요청을 위임하여 전달한다.
- 즉 UserServiceTx의 멤버변수의 UserService에는 UserServiceImpl이 주입될 것이고 UserServiceTx에서는 트랜잰셕 설정 후 UserServiceImpl에게 요청을 넘겨 실제 비즈니스 로직을 처리하게 될 것이다.
- 해당 비즈니스 로직이 처리된 후 다시 UserServiceTx에게 넘어와 트랜잭션 설정이 마무리될 것이다.

<br>

```xml
<bean id="userService" class="ch6.step1.UserServiceTx">
        <constructor-arg ref="userServiceImpl"/>
        <constructor-arg ref="transactionManager"/>
</bean>

<bean id="userServiceImpl" class="ch6.step1.UserServiceImpl">
    <constructor-arg ref="userDao"/>
    <constructor-arg ref="userLevelUpgradePolicy"/>
    <constructor-arg ref="mailSender"/>
</bean>
```
- userServiceImpl를 따로 빈을 등록시켜주고 UserServiceTx의 생성자에 빈으로 주입시켜준다.

<br>

```java
@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(locations = "file:src/main/java/ch6/step1/applicationContext.xml")
class UserServiceTest {
    // UserServiceImpl, UserServiceTx 이렇게 UserService의 하위 구현체인 두개가 빈으로 등록되지만 userService와 더 구체적으로 비슷한 UserServiceTx가 사용된다.
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    // userServiceImpl을 사용하기 때문에 주입받아준다.
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;


    @Test
    void upgradeLevels() throws Exception {
        users.forEach(userDao::add);

        MockMailSender mockMailSender = new MockMailSender();

        // userService는 인터페이스 타입이므로 스터빙이 불가능하다.
        // 빈으로 등록한 userServiceImpl에 직접 mockMailSender를 설정해준다.
        userServiceImpl.setMailSender(mockMailSender);

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

    @Test
    void upgradeAllOrNothing() throws Exception {
        users.forEach(userDao::add);
        String id = users.get(3).getId();

        // UserServiceTx에 UserServiceImpl을 넣어준다.
        UserService userServiceImpl = new UserServiceImpl(this.userDao, new TestUserLevelUpgradePolicy(id), mailSender);
        UserService testUserService = new UserServiceTx(userServiceImpl, transactionManager);

        assertThatThrownBy(() -> testUserService.upgradeLevels())
                .isInstanceOf(UserServiceTest.TestUserServiceException.class);

        checkLevel(users.get(1), false);
        checkLevel(users.get(3), false);
    }
}
```
- UserService를 인터페이스로 변경한 후 약간의 변경이 필요하지만 해당 변경사항만 적용하면 테스트는 그대로 성공하는 것을 알 수 있다.
- 자세한 변경사항은 주석에 표시되어 있다.

#### 트랜잭션 경계설정 코드 분리의 장점
- **첫째, 비즈니스 로직을 담당하고 있는 UserServiceImpl의 코드를 작성할 때는 트랜잭션과 같은 기술적인 내용에는 전혀 신경 쓰지 않아도 된다.**
  - 트랜잭션 같은 기술적인 내용떄문에 잘 만들어진 비즈니스 로직 코드가 엉망이 될 불상사가 없을 것이다.
  - 스프링 트랜잭션과 같이 로우레벨에 뛰어난 개발자와, 비즈니스의 흐름을 잘 이해하고 비즈니스 로직을 잘 작성하는 개발자가 있을 때 이를 서로 분리하였다면 훨씬 더 효과적으로 개발이 가능할 것이다.
- **둘째, 비즈니스 로직에 대한 테스트를 손쉽게 만들어 낼 수 있다. 이는 이제부터 알아보도록 하자.**

---
## 6.2 고립된 단위 테스트
- 가장 편하고 좋은 테스트 방법은 가능한 작은 단윌 쪼개서 테스트 하는 것이다.
- 테스트의 단위가 작을 수록 테스트 실패시 원인 파악이 쉬워지고, 만들기가 쉬워질 뿐만아니라 의도나 내용이 명확해진다.
- 처음부터 작은 단위로 테스트를 진행한다면 나중에 덩치가 커지더라도 어렵지 않게 오류를 찾을 수 있을 것이며, 작은 단위의 테스트로 검증한 부분은 제외하고 접근할 수 있을 것이다.
- **하지만 만약 태스트 대상이 다른 오브젝트와 환경에 의존하고 있다면 작은 단위의 테스트가 주는 장점을 얻기가 힘들어진다.**

### 6.2.1 복잡한 의존관계 속의 테스트
- 인터페이스로 분리하기 전의 UserService는 DB와의 데이터 연동을 위해 UserDao를 의존하고, 메일 통신을 위해 MailSender를 의존하고, 트랜잭션 처리를 위해 PlatfromTansactionManager를 의존해야 하고, 업그레이드 정책을 위해 UserLevelUpgradePolicy를 의존해야 한다.
- UserServiceTest가 테스트하고자 하는 대상인 UserService는 사용자 정보를 관리하는 비즈니스 로직의 구현코드이다.
- 따라서 userService의 코드가 바르게 작성되어 있으면 성공하고, 아니라면 실패하면된다. 즉 테스트이 단위는 UserService 클래스여야 한다.
- 하지만 UserService는 위에서 언급한것처럼 4개의 의존관계를 가지고 있다. 이 의존관계들은 테스트가 진행되는 동안 같이 실행될 것이다.
- 게다가 각 의존관계의 구현체들도 또 다른 의존 관계를 가지고 있을 수도 있다.
- 따라서 userService를 테스트하는 것처럼 보이지만 실상은 그 뒤에 존재하는 훨씬 더 많은 객체들과 환경, 서비스, 서버, 네트워크등을 테스트하게 된 셈이다.
- **이 들중 하나라도 제대로 설정되어 있지 않거나, 오류가 발생했다면 그로인해 UserService의 테스트는 실패하고 말 것이다.**
- 따라서 이런 경우의 테스트는 준비가 힘들고, 환경에 매우 의존적이며 그 때문에 수행속도가 느려지게되므로 테스트 작성이 꺼려질 것이다.
- 그러므로 테스트의 대상이 환경이나, 외부 서버, 다른 클래스의 코드에 종속되고 영향을 받지 않도록 고립시킬 필요가 있다.
- 테스트를 의존 대상으로 부터 고립시키는 방법은 테스트를 위한 대역을 사용하거나, 테스트 스텁 혹은 목 객체를 이용하면 된다.

### 6.2.2 테스트 대상 오브젝트 고립시키기
#### 테스트를 위한 UserServiceImpl 고립
- 현재 UserServiceImpl에는 UserDao, UserLevelUpgradePolicy, MailSender를 의존하고 있다.
- 이 중 UserLevelUpgradePolicy는 직접 구현한 것이고 UserDao, MailSender는 외부의 API를 사용하고 있다.
- 그러므로 UserDao와 MailSender는 고립시키는 것이 좋을 것이다.
- 이전의 테스트에서 MailSender는 MockMailSender를 통해 목 객체로 만들어 테스트를 진행하였다.
- MockMailSender가 어떻게 활용되었는지 다시 한 번 확인해보자.

<br>

```java
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
```
- 테스트 클래스 내부에 static class로 mockMailSender를 구현하였다.
- 내부에는 컬렉션으로 requests를 가지고 이 requests는 send시 수신자의 메일주소를 하나씩 담게된다.
- 이를 통해 메일 발송 요청이 나갔는지 확인할 수 있을 것이다.
- 해당 테스트코드를 확인해보자

<br>

```java
@Test
void upgradeLevels() throws Exception {
    // 1. 테스트를 위한 DB 데이터 준비
    users.forEach(userDao::add);

    // 2. 목 객체 생성 후 주입
    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    // 3. 테스트 실행
    userService.upgradeLevels();

    // 4. 테스트 후 DB에 저장된 결과 확인
    checkLevel(users.get(0), false);
    checkLevel(users.get(1), true);
    checkLevel(users.get(2), false);
    checkLevel(users.get(3), true);
    checkLevel(users.get(4), false);

    // 5. 목 객체를 이용한 결과 확인
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
```
- 주석을 보면 확인할 수 있듯이 테스트는 순서대로 진행될 것이다.
- 목 객체를 이용한 MailSender는 목 클래스를 정의할 때 생성한 requests 컬렉션을 통해 메일 전송이 수행되었는지 확인할 수 있다.
- 현재 코드에서 1, 4번은 userDao를 사용하여 실제 DB에 의존하고 있다. 이를 어떻게 목 오브젝트로 만들 수 있을까?

#### UserDao 목 오브젝트
- 목 오브젝트는 기본적으로 스텁과 같은 방식으로 테스트 대상을 통해 사용될 때 필요한 기능을 지원해주어야 한다.
- 이를 위해 upgradeLevels() 메서드가 실행될 때 어떻게 UserDao를 사용하는지 확인할 필요가 있다.

```java
@Override
public void upgradeLevels() {
    // userDao.getAll()을 통해 모든 유저를 가져온다.
    userDao.getAll().forEach(user -> {
        if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
            userLevelUpgradePolicy.upgradeLevel(user);
            // 조건에 충족한다면 유저들을 업데이트 해준다.
            userDao.update(user);
            sendUpgradeEmail(user);
        }
    });
}
```
- upgradeLevels에서는 총 두번 userDao를 사용하는것을 알 수 있다.
- 그러므로 목 오브젝트는 getAll(), update()를 구현해주어야 할 것이다.
- getAll()에 대해서는 스텁으로서, update()에 대해서는 변경 검증을 위한 목 오브젝트로서 동작하는 MockUserDao라는 테스트 대역을 정의해보자.

```java
static class MockUserDao implements UserDao{
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
    public void add(User user) { throw new UnsupportedOperationException(); }
    @Override
    public User get(String id) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll() { throw new UnsupportedOperationException(); }
    @Override
    public long getCount() { throw new UnsupportedOperationException(); }
}
```
- 우선 사용되지 않는 기능들을 사용할 수 없게 예외를 던져준다.
- 그리고 수정전 유저와 수정된 유저를 담을 컬렉션을 두개 만든 후 각 기능에 알맞게 정의해준다.

```java
class UserServiceTest {
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

        // 1. 목 오브젝트들을 생성한다.
        MockUserDao mockUserDao = new MockUserDao(users);
        MockMailSender mockMailSender = new MockMailSender();

        // 2. 목 오브젝트의 의존성을 갖는 UserServiceImpl을 생성한다.
        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, new UserLevelUpgradeDefault(), mockMailSender);

        // 3. 목 오브젝트를 의존성으로 갖는 userServiceImpl로 테스트를 진행한다.
        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkLevel(updated.get(0), users.get(1).getId(), Level.SILVER);
        checkLevel(updated.get(1), users.get(3).getId(), Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(this.users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(this.users.get(3).getEmail());
    }

    private void checkLevel(User user, String id, Level level) {
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getLevel()).isEqualTo(level);
    }
}
```
- 이렇게 만든 MockUserDao와 MockMailSender를 통해 UserService를 테스트할 수 있게 되었다.
- 해당 테스트에서는 그 어떤것도 스프링 빈에서 가져오지 않고 고립된 테스트로 동작시킬 수 있게 되었다.
- 이 테스트는 외부 디비에 문제가 생기거나, 메일 서버에 문제가 생겨도 UserService자체의 문제가 아니라면 정상적으로 동작할 것이다.

#### 테스트 수행 성능의 향상
- 간단한 테스트라 테스트 시간은 실감나지 않겠지만 실제 DB와의 연동도 없고 메일 서버와의 연동도 없기 때문에 테스트 시간은 매우 짧아졌을 것이다.
- 고립된 테스트를 하면 테스트가 다른 의존 대상에 영향을 받을 경우를 대비해 복잡하게 준비할 필요가 없을 뿐만아니라, 테스트 수행성능도 크게 향상된다.
- 테스트가 빨리 돌아가면 부담 없이 자주 테스트를 돌려볼 수 있게 될 것이다.
- **고립된 테스트를 만들려면 목 오브젝트 작성과 같은 약간의 수고가 더 필요할지 모르겠지만, 그 보상은 충분히 기대할 만하다.**
- Mokito와 같은 라이브러리를 사용하면 더욱 쉽게 목 오브젝트를 만들 수 있을 것이다.

### 6.2.3 단위 테스트와 통합 테스트
- 단위 테스트의 단위는 정하기 나름이다. 사용자 관리 기능 전체를 하나의 단위로 볼 수도 있고 하나의 클래스나 하나의 메서드를 단위로 볼 수도 있다.
- 중요한 것은 하나의 단위에 초점을 맞춘 테스트라는 것이다.
- **테스트 대상 클래스를 목 오브젝트 등의 테스트 대역을 이용해 의존 오브젝트나 외부 리소스를 사용하지 않도록 고립시켜서 테스트 하는 것을** **단위 테스트라고 할 수 있다.**
- 반면 두개 이상의 성격이나 계층이 다른 오브젝트가 연동하도록 만들어 테스트하거나, 또는 외부의 DB나 파일 서비스등의 리소스가 참여하는 테스트는 **통합 테스트라고** 할 수 있다.

#### 단위 테스트와 통합 테스트 가이드라인
- 항상 단위 테스트를 먼저 고려한다.
  - 하나의 클래스나, 성격과 목적이 같은 긴밀한 클래스 몇개를 모아 외부와의 의존관계를 모두 차단하고 필요에 따라 스텁이나 목 오브젝트 등의 테스트 대역을 이용하도록 테스트를 만든다.
  - 단위 테스트는 테스트 작성도 간단해지며, 속도도 빠르고, 외부의 환경으로부터 테스트 결과에 영향을 받지 않아도 되기 때문에 효율적인 테스트 작성이 가능해진다.
- 외부 리소스를 사용해야만 가능한 테스트는 통합 테스트로 만든다.
  - DAO와 같은 경우 단위 테스트로 만들기 어렵다.
  - 그 자체로 로직을 담기보다는 DB를 통해 로직을 수행하는 매개체(인터페이스)와 같은 역할을 하기 때문이다.
  - 이런 DAO만을 테스트하는 경우 DB라는 외부 리소스를 사용하니 통합 테스트라고 할 수 있지만, 또 하나의 기능 단위를 테스트하는 것이기 때문에 단위 테스트라고도 할 수 있을 것이다.
  - DAO를 테스트를 통해 충분히 검증하였다면 DAO를 이용하는 다른 클래스의 테스트 코드는 목 객체로 DAO를 사용하더라도 DAO의 기능을 믿을 수 있을 것이다.
  - 물론 전체를 테스트하는 통합테스트는 반드시 필요할 것이다.
- 여러 개의 단위가 의존관계를 가지고 동작할 때 통합 테스트는 필요하다. 다만, 단위 테스트를 충분히 거쳤다면 통합 테스트의 부담은 상대적으로 줄어든다.
- 단위 테스트를 만들기가 너무 복잡하다고 판단되는 코드는 처음부터 통합 테스트를 고려해본다. 이때도 통합 테스트에 참여하는 코드 중에서 가능한 한 많은 부분을 미리 단위 테스트 해두는게 유리하다.
- 스프링 테스트 컨텍스트 프레임워크를 이용하는 테스트는 통합 테스트라고할 수 있다.

### 6.2.4 목 프레임워크
- 목 프레임워크는 Mockito가 가장 유명하며 유용한 기능을 많이 가지고 있다.
- Mockito를 사용하여 테스트 코드들을 변경해보자.

```groovy
testImplementation 'org.mockito:mockito-core:3.3.0'
```
- 우선 mockito 의존성을 추가해준다.

```java
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
```
- 이전에 직접 클래스를 만들어 구현하였던 Mock 오브젝트들을 애노테이션이나 메서드하나로 매우 간단하게 호출할 수 있다.
- 스터빙과 검증작업은 위의 주석을 확인보면 정말 간단한 것을 알 수 있다.
- mockito와 같은 목 오브젝트 프레임워크는 단위 테스트시 매우 유용한것을 확인할 수있다.
