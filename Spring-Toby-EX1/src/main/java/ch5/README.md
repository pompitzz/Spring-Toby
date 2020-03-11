# Chapter5. 서비스 추상화
- UserDao에 트랜잭션을 적용해보면서 스프링이 어떻게 성격이 비슷한 여러 종류의 기술을 추상화하고 이를 일관된 방법으로 사용할 수 있도록 지원하는지를 살펴볼 것이다.

## 5.1 사용자 레벨 관리 기능 추가
#### 요구사항
- 사용자의 레벨은 BASIC, SILVER, GLOD 세 가지중 하나다.
- 사용자가 처음 가입하면 BASIC 레벨이 되며, 이후 활동에 따라서 한 단계식 업그레이드 될 수 있다.
- 가입 후 50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이된다.
- SILVER레벨이면서 30번이상 추천을 받으면 GOLD 레벨이 된다.
- 사용자 레벨의 변경 작업은 일정한 주기를 가지고 일괄적으로 진행된다. 변경 작업 전에는 조건을 충족하더라도 레벨의 변경이 일어나지 않는다.

### 5.1.1 필드 추가
#### Level 이늄
- 사용자 레벨을 저장할 필드를 추가하고, DB의 User테이블에는 어떤 타입으로 넣을 것인지 생각해보자.
- DB에 varchar 타입을 선언하고 레벨들을 문자로 넣을 수 있겠지만 별로 안정적이지 않아 보인다.
- 대신 각 레벨을 코드화해서 숫자로 넣는다면 범위도 작아 관리가 쉬워질 것이다.
- 이럴 때 [Enum](./step1/Level.java)을 활용하면 이늄 내부에서는 DB에 저장할 int 타입의 값을 가지고 있지만 외부에서는 Level 타입의 오브젝트이므로 안정적으로 레벨들을 관리할 수 있다.


```java
public class User {
    private String id;
    private String name;
    private String password;

    private Level level;
    int login;
    int recommend;
}
```
```sql
alter table users
    add Level     tinyint not null,
    add Login     int     not null,
    add Recommend int     Not null;
```
- User테이블에 이늄과 login, recommend를 추가해준다.
- 그리고 users 테이블에도 필드 값들을 추가해준다.
- 추가된 값들 모두 not null이므로 이전에 작성해두었던 **테스트 코드에서 유저들을 추가하는 설정의 변경이 필요하다**

#### UserDao 테스트 수정
```java
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
  }
```
- [UserDaoTest](../../../test/java/ch5/step2/UserDaoTest.java)에 변경된 사항들을 적용해가면서 테스트를 진행해본다.
- 테스트를 진행해보면 JDBC에서 사용하는 SQL문법은 컴파일 시에 잡아주지 않고 테스트를 진행하여야 확인할 수 있는 것을 알 수 있다.
- 매번 테스트를 잘 작성했더라면 테스트에서 잡을 수 있겠지만 테스트가 작성되어 있지않다면 실 서비스에서 이러한 오류가 발견될 수 있다.


### 5.1.2 사용자 수정 기능 추가
- 사용자 관리 비즈니스에 따르면 기본 키를 제외한 나머지는 수정될 가능성이 높다.
- 업데이트를 이용해 기본 키를 제외한 모든 값을 변경하는 메서드들을 만들기 전에 테스트 코드 부터 작성해보자.

```java
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
```
- 테스트 코드를 작성했다면 실제 기능을 구현해본다.

<br>

```java
@Override
public void update(User user) {
    this.jdbcTemplate.update(
            "update users set name = ?, password = ?, level = ?, Login = ?, Recommend = ? where id = ?",
            user.getName(), user.getPassword(),
            user.getLevel().intValue(), user.getLogin(),
            user.getRecommend(), user.getId()
    );
}
```
- 기능은 정상적으로 동작할 것이다. 하지만 만약 기능에 where이 없다면 어떻게 될까?
- 테스트는 동일하게 성공할 것이다. 즉 where절에 대한 테스트다 필요하다.
- 이를 가장 간단하게 테스트할 수 있는 방법은 수정할 사용자와 수정하지 않을 사용자들 둘을 비교해본다면 where에 대한 테스트를 진행할 수 있을 것이다.

```java
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

    // 해당 유저는 변경되었을 것이다.
    User user1update = userDao.get(user1.getId());
    checkSameUser(user1, user1update);

    // 해당 유저는 변경되지 않았을 것이다.
    User user2same = userDao.get(user2.getId());
    checkSameUser(user2, user2same);
}
```
- 이제 where절이 제대로 적용되었는지 확인할 수 있게 되었다.

### 5.1.3 UserService.upgradeLevels()
- 레벨을 업그레이드 시키는 것은 getAll()로 모든 유저들을 들고와서 확인 후 update해주면 된다.
- 그렇다면 사용자를 가져와서 update해야할 사용자들을 관리할 로직은 어디에 두는 것이 옳을까?
- UserDao는 데이터를 어떻게 가져오고 조작할지에 대한 곳을 다루는 곳이지 이러한 비즈니스 로직을 두기에는 적합하지 않다.
- 사용자 관리 비즈니스 로직을 담을 클래스를 하나 추가하여 거기에 담는 것이 옳을 것이다.
- UserService를 추가하고 해당 객체도 UserDao를 가져야하므로 빈으로 등록시켜 준 후 생성자로 UserDao를 주입해주자.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="ch5.step2.UserService">
        <constructor-arg ref="userDao"/>
    </bean>

    <bean id="userDao" class="ch5.step2.UserDaoJdbc">
        <constructor-arg ref="dataSource"/>
    </bean>

    <bean id="dataSource" class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
        <property name="driverClass" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/springtoby"/>
        <property name="username" value="sunlee"/>
        <property name="password" value="pass"/>
    </bean>

</beans>
```
- userService까지 빈으로 등록시켜주면 xml은 이렇게 구성될 것이다.


<br>

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/ch5/step2/applicationContext.xml")
class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    void bean() throws Exception{
        assertThat(this.userService).isNotNull();
    }
}
```
- UserService 테스트를 만든 후 제대로 빈으로 등록되었는지 테스트 해보면 빈으로 등록된 것을 확인할 수 있다.
- 이제 실제 서비스에서 비즈니스 로직을 작성해보자.

#### upgradeLevels() 메서드
```java
public class UserService {
    UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        users.forEach(user -> {
            Level level = user.getLevel();
            boolean changed = false;
            if (level == Level.BASIC && user.getLogin() >= 50){
                user.setLevel(Level.SILVER);
                changed = true;
            }
            else if(level == Level.SILVER &&user.getRecommend() >= 30){
                user.setLevel(Level.GOLD);
                changed = true;
            }

            // 변경되었다면 업데이트를 호출해준다.
            if (changed) userDao.update(user);
        });
    }
}
```
- 약간의 복잡성이 있지만 그렇게 복잡하지 않은 로직이다.
- 이런 코드도 테스트는 항상 철저하게 이루어져야 차후에 발생할 수 있는 다양한 오류들을 예방할 수 있다.

#### upgradeLevels() 테스트
- 그렇다면 어떻게 테스트하면 좋을까?
- 일단 모든 조건들을 하나씩 확인해봐야한다. BASIC, SILVER가 한단계식 레벨이 업그레이드될 때 2가지, 그리고 GOLD거나 조건이 만족하지 못해 업그레이드가 되지 않을 때 3가지해서 총 5가지는 테스트해봐야 한다.

```java
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
```
- 해당 상황들에 맞게 총 5번의 테스트를 진행하였고 이는 잘 동작하는 것을 알 수 있다.
