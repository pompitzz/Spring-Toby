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

### 5.1.4 UserService.add()
- 기본 로직은 구현하였지만 처음 가입하는 사용자가 BASIC 레벨이 되어야 하는 설정은 아직 구현되지 않았다.
- 이러한 로직은 어디에 담는것이 적합할까?
- UserDaoJdbc의 add() 메서드에는 이러한 메서드를 담는것이 옳지 않아보인다.
- UserDao는 주어진 오브젝트를 DB에 정보를 넣고 읽는 방법에만 관심을 가져야 한다.
- 그러므로 UserService에도 add()를 만들어 두고 사용자가 등륵될 때 적용할 만한 비즈니스 로직을 담당하게 하면될 것이다.
- 레벨이 이미 정해진 경우와 레벨이 비어있을 경우에 대해 각각 테스트를 진행하면 될 것이다.

```java
@Test
void add() throws Exception{
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

public void add(User user) {
    if(user.getLevel() == null) user.setLevel(Level.BASIC);
    userDao.add(user);
}
```
- 해당 기능을 테스트해보면 정상적으로 동작한다.
- 하지만 간단한 비즈니스 로직을 담은 코드를 테스트하기 위해 DAO와 DB까지 모두 동원되어야 하는 점이 조금 불편하다.
- 이런 테스트는 깔끔하고 간단히 만드는 방법이 존재하는데, 뒤에서 다루게 될 것이다.

### 5.1.5 코드 개선
- 비즈니스 로직은 성공적으로 끝마쳣지만 해당 코드들에 대해서 생각해볼 시간이 필요하다.
- 코드에 중복된 부분은 없는가?
- 코드가 무엇을 하는지 이해하기 불편하지 않는가?
- 코드가 자신이 있어야 할 자리게 있는가?
- 앞으로 변경이 일어난다면 어떤 것이 있을 수 있고, 그 변화에 쉽게 대응할 수 있게 작성되어 있는가?

#### upgradeLevels() 메서드 코드의 문제점
- upgradeLevels() 메서드를 살펴보면 몇가지 문제점들이 존재한다.
- 우선 for 루프속에 있는 if, elseif 블록들은 레벨의 변화 단계와 업그레이드 조건, 조건이 충족됐을 때 해야 할 직업이 한데 섞여 있어서 로직을 이해하기 쉽지 않다.

```java
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
```
- 레벨을 파악하는 로직, 업그레이드 조건을 담는 로직, 레벨를 업그레이드 하는 로직, flag를 통해 user를 업데이트 하는 로직이 존재한다.
- 이는 서로 관련있어 보이지만 사실은 성격이 조금씩 다른 것들이 섞여 있거나 분리돼서 나타나는 구조이다.
- 그리고 이런 if 조건 블록이 레벨 개수만큼 반복되기 때문에 새로운 레벨이 추가된다면 Level 이늄과 if블록들을 수정해줘야할 것이다.
- 레벨들이 점점 추가되고 업그레이드 조건이 복잡해질 수록 이 메서드는 점점 더 복잡해질 것이다.

#### upgradeLevels() 리팩토링
- 이를 리팩토링 하기 위해 먼저 추상적인 레벨에서 로직을 작성해보자.
- 기존은 upgradeLevels() 메서드는 자주 변경될 가능성이 있는 구체적인 내용이 추상적인 로직의 흐름과 함께 섞여 있다.
- 구체적인 구현에서 외부에 노출할 인터페이스만 보여주듯이 기본적인 흐름만을 upgradeLevels에 정의해보자.

```java
public void upgradeLevels() {
    List<User> users = userDao.getAll();
    users.forEach(user -> {
        if(canUpgradeLevel(user)){
            upgradeLevel(user);
        }
    });
}
```
- 이제 이 메서드는 매우 단순 명료하게 어떤 작업을 하는지 쉽게 이해할 수 있게 되었다.
- 이제 이 메서드에서 호출하는 메서드들을 만들면 된다.

```java
private void upgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    if (currentLevel == Level.BASIC) user.setLevel(Level.SILVER);
    else if(currentLevel == Level.SILVER) user.setLevel(Level.GOLD);
    userDao.update(user);
    
}

private boolean canUpgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    switch (currentLevel){
        case BASIC: return (user.getLogin() >= 50);
        case SILVER: return (user.getRecommend() >= 30);
        case GOLD: return false;
        default: throw new IllegalArgumentException("Unknown Level" + currentLevel);
    }
}
```
- 테스트를 돌려보면 정상적으로 동작하는 것을 알 수 있다.
- 하지만 upgradeLevel을 보면 다음 단계가 무엇인가 하는 로직과, 그때 사용자 오브젝트의 level 필드를 변경해준다는 로직이 같이 있으며, 너무 노골적으로 드러나 있다.
- 게다가 예외 상황에 대한 처리가 없어 만약 GOLD유저가 upgradeLevel메서드를 호출하게 되면 의미없이 update 쿼리를 날리게 될 것이다.
- 그리고 레벨이 많아질 수록 if문은 계속해서 커질 것이고 변경되는 이유도 많아질 것이다.
- 이를 더 분리하여 레벨의 순서와 다음 단게 레벨이 무엇인지를 결정하는 일을 Level에게 맡겨보자

```java
public enum Level {
    GOLD(3, null),  SILVER(2, GOLD), BASIC(1, SILVER); 

    private final int value;
    private final Level next;

    Level(int value, Level next) {
        this.value = value;
        this.next = next;
    }
    public Level nextLevel(){
        return this.next;
    }
}
```
- 이제 nextLevel을 통해 간단하게 다음 레벨의 정보를 가져올 수 있다.
- 그러므로 다름 단계의 레벨이 무었인지 if 조건식을 만들어가며 비즈니스 로직에 담아둘 필요가 ㅇ벗다.
- 이번엔 사용자 정보가 바뀌는 부분을 UserService 메서드에서 User로 옮겨본다.
- User의 내부 정보가 변경되는 것은 UserService보다는 user가 스스로 다루는게 적절하다.
- User에 업그레이드 하는 기능을 추가해보자

```java
public void upgradeLevel(){
   Level nextLevel = this.level.nextLevel();
   if (nextLevel == null){
       throw new IllegalArgumentException(this.level + "은 업그레이드가 불가능합니다.");
   }
   else{
       this.level = nextLevel;
   }
}
```

- 레벨을 업그레이드 하는것을 User자체가 담당하고 있으므로 이제 Service단의 upgradeLevel 메서드는 아래와 같이 매우 간단하게 이루어지게 된다.

```java
private void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
}
```
- 테스트를 돌려보면 정상적을 동작되는것을 알 수 있다.
- 개선된 코드들을 보면 각 객체와 메서드가 각각 자기 몫의 책임을 맡아 일을 하는 구조로 만들어 졌음을 알 수 있다.
- **UserService, User, Level이 내 부정보를 다루는 자신의 책임에 충실한 기능을 가지고 있으면서 필요가 생기면 이런 작업을 수행해달라고 서로 요청하는 구조이다.**
- 각자 자기 책임에 충실한 작업만 하고 있으니 코드를 이해하기도 간단해졌다.

> - 객체지향적인 코드는 다른 객체의 데이터를 가져와서 작업하는 대신 데이터를 갖고 있는 다른 객체에게 작업을 요청한다.
> - 객체에게 데이터를 요구하지 말고 작업을 요청하라는 것이 객체지향 프래그래밍의 가장 기본이 되는 원리이기도 하다.
> - 이렇게 코드를 개선하여 객체지향적인 설계로 구현된다면 BASIC, SILVER 사이에 새로운 레벨을 추가하고 각 조건들을 변경하더라도 필요한 수정사항들을 어디에서 찾아야하는지가 명확해 졌고, 변경 후에도 코드는 여전히 깔끔하고 코드를 이해하는데 어려움이 없을 것이다.

#### User 테스트
- 기능이 추가된 User도 간단한 기능이지만 계속해서 새로운 로직과 기능이 추가될 수 있으므로 테스트를 만들어 두는 것이 도움이 될 것이다.

```java
class UserTest {

    User user;

    @BeforeEach
    void setUp(){
        user = new User();
    }

    @Test
    void upgradeLevel() throws Exception{
        Level[] levels =  Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    void cannotUpgradeLevel() throws Exception{
        assertThatThrownBy(() -> {
            Level[] levels = Level.values();
            for (Level level : levels) {
                if(level.nextLevel() != null) continue;
                user.setLevel(level);
                user.upgradeLevel();
            }
        })
        .isInstanceOf(IllegalArgumentException.class);

    }
}
```
- 이렇게 다음 레벨이 존재하지 않아 예외가 발생하는 테스트와 다음 레벨이 존재해 제대로 업그레이드 되는 테스트들을 만들어 확인해볼 수 있다.


#### UserServiceTest 개선

```java
@Test
void upgradeLevels() throws Exception{
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
    assertThat(findUser.getLevel()).isEqualTo(level);
}
```
- 기존의 UserServiceTest에서 upgradeLevels을 테스트하는 테스트 케이스이다.
- 해당 테스트는 checkLevel에게 전달인자로 테스트할 레벨들을 하나하나 넘겨주었다.
- 이는 해발 파라미터만으로 업그레이드되었는지 되지 않았는지 명시적으로 알 수 없다.
- 아래와 같이 boolean와 nextLevel을 이용하여 테스트를 작성한다면 더욱 더 명시적은 테스트가 가능할 것이다.
- 뿐만아니라 숫자로 추가해줬던 유저들을 상수를 이용하여 깔끔하고 명시적으로 코드를 작성할 수 있다.

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
}
```
- UserService의 Test는 더욱 깔끔해진것을 확인할 수 있다.
- 여기서 추가적인 개선사항을 알아보자.
- 현재 user들을 업그레이드 하는 정책들이 UserService의 내부에 구현되어 있다.
- 만약 특정 이벤트로 인해 업그레이드 기준들이 변경된다고할 때 현재의 경우에는 UserService를 직접 수정해야하고 이벤트가 끝나면 다시 원상복구해야하는 불편함이 존재한다.
- 이는 DI를 통해 더 유연하게 만들 수 있을 것이다.


#### DI를 이용해서 한번 만들어 보자.
```java
public interface UserLevelUpgradePolicy{
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user); 
}

public class UserLevelUpgradeDefault implements UserLevelUpgradePolicy {
    public static final int MIN_LONGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LONGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("허용되지 않은 레벨입니다. CurrentLevel : " + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
    }
}

public class UserService {
    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;


    public UserService(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() {
        userDao.getAll().forEach(user -> {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
            }
        });
    }
}
```
- 우선 업그레이드 정책을 가지는 인터페이스를 정의하고 이를 구현한다.
- 그리고 이 인터페이스를 생성자를 통해 주입받아 UserService에서는 이 정책으로 해당 유저들의 레벨을 업그레이드 할지 결정할 수 있다.
- 이렇게 구성된다면, 정책이 변경되어도 빈으로 등록된 구현체를 바꿔 끼워주면 UserService는 변경이 필요없어진다.
