# Chapter4. 예외
- JdbcTemplate을 대표로 하는 스프링의 데이터 엑세스 기능에 담겨 있는 예외처리와 관련된 접근 방법에 대해 알아보며 예외를 처리하는 베스트 프랙티스를 살펴본다.

## 4.1 사라진 SQLException

### 4.1.1 초난감 예외처리
- 예외를 처리할 때 catch문으로 예외를 잡고 그 예외에 대한 아무런 동작을 하지 않는것은 연습상황에도 해서는 안되는 습관이다.
- 이러한 습관이 나중에 실무에서 적용되면 매우 위험한 결과를 초래할 수 있다.
- 그렇다고 해당 예외를 printStackTrace()하는 것도 좋은 방법이 아니다.
- 로그는 금방 묻혀버리고 놓치기 쉽기 때문에 이 또한 위험한 결과를 초래할 수 있다.
- **모든 예외를 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보돼야 한다.**
- [UserDao](step1/UserDao.java)에서 jdbcTemplate를 사용하였더니 예외가 사라졌다.

<<<<<<< HEAD
---
=======
>>>>>>> 363609ada5e21b8286070b6745b03a254153e003

### 4.1.2 예외의 종류와 특징
- trhow를 통해 발생시킬 수 있는 예외는 크게 세 가지가 있다.

#### Error
- 에러는 시스템에 뭔가 비정상적인 상황이 발생했을 경우에 사용되며 주로 JVM에서 발생시키는 것이다.
- 즉 애플리케이션 코드에서 잡을 수 없고 시스템단에서 잡아야하므로 애플리케이션에서는 이 예외처리를 신경쓰지 않아도 된다.

#### UnChecked Exception
- RuntimeException의 하위 클래스들로 주로 프로그램 오류가 있을 때 발생하도록 의도된 것이다.
- NullPointerException, IllegalArgumentException 등과 같이 프로그래머의 부주의로 발생하는 것들이다.
- 그렇기 때문에 해당 예외 처리를 강제하지 않는다.

#### Checked Exception
 - 반드시 처리를해야하는 예외들로 JDK 초기에 만들어진 것들은 대부분 체크 예외로 구성되어 있다.
<<<<<<< HEAD
 
 ---
 
=======


>>>>>>> 363609ada5e21b8286070b6745b03a254153e003
### 4.1.3 예외 처리 방법
#### 예외 복구
- 예외 상황을 파악하고 문제를 해결하여 정상 상태로 놀려 놓는 기법이다.
- 사용자가 해당 파일을 이용하려고할 때 예외가 발생한다면 다른 파일을 이용하도록 자연스럽게 흐름을 유도해주는 방식으로 복구할 수 있다.
- 해당 예외가 사용자에게 그대로 던져지는 것은 예외 복구라고할 수 없다.
- 즉 예외가 처리 됐으면 비록 기능적으로는 사용자에게 예외상황으로 비칠 수 있어도 애플리케이션에서는 정상적으로 설계된 흐름을 따라 진행돼야 한다.

> 예외처리 코드를 강제하는 체크 예외들은 예외를 어떤 식으로 복구할 가능성이 있는 경우에 주로 사용된다.

#### 예외처리 회피
- 예외처리를 자신이 담당하지 않고 다른 쪽에게 던져버리는 기법이다.
- catch문으로 예외를 잡은 후 로그를 남기고 다시 예외를 던지는 식으로 처리를 회피할 수 있다.
- 예외를 회피하는 것은 예외를 복구하는 것 처럼 의도가 분명해야 한다.
- 템플릿 / 콜백 처럼 긴밀한 관계에 있는 다른 객체에게 책임을 분명히 지게하거나, 자신을 사용하는 쪽에서 예외를 처리하는게 최선이라는 확신이 있을 때 이러한 방법을 사용해야 한다.

#### 예외 전환
- 예외 회피와 비슷하게 예외를 복구해서 정상적인 상태로 만들 수 없기 때문에 예외를 메서드 밖으로 던져버리는 것이다.
- 대신 예외처리와는 다르게 다른 예외로 전환해서 던진다는 특징이 있다.

**예외 전환은 보통 두가지 목적으로 사용된다.**
- 내부에서 발생한 예외를 그대로 던지는 것이 그 상황에 맞게 적절한 의미를 부여해주지 못하였을 때 의미를 분명히 전달하기 위함이다.
- 사용자가 회원가입을할 때 중복아이디가 있을 때 JDBC는 SQLException이 발생하지만 DAO가 이를 그대로 던져버리면 서비스레이어에서는 이를 이해하기 어려워 진다.
- 그러므로 해당 정보에 알맞는 예외로 전환하여 던져야 더욱 분명하게 예외를 처리할 수 있게 된다.
- **보통 예외 ㅈ전환은 이전의 예외를 담아서 중첩 예외로 던지는 것이 근본적인 원인을 알릴 수 있어 유용하다.**

```java
catch(SQLException e){
    throw DuplicationUserIdException(e);
}
```

> - 대부분 서버환경에서는 애플리케이션 코드에서 처리하기 않고 전달된 예외들을 일괄적으로 다룰 수 있는 기능을 제공한다.
> - 어차피 복구하지 못할 예외라면 애플리케이션 코드에서는 런타임 예외로 포장해서 던져버리고, 예외처리 서비스 등을 이용해 자세한 로그를 남기고, 관리자에게 통보할 수 있게하는것이 바람직하다.
<<<<<<< HEAD
=======

### 4.1.4 예외처리 전략
#### 런타임 예외의 보편화
- AWT, 스윙과 같은 독립형 애플리케이션은 어떤 예외가 발생하더라도 해당 예외를 복구해야할 사항이 많다.
- 하지만 서버 특성상 수많은 요청에 따라 예외가 발생했을 때 작업을 일시 중지하고 사용자와 바로 커뮤니케이션하면서 예외상황을 복구할 수 있는 방법이 없다.
- 그렇기 때문에 서버 애플리케이션에서는 예외상황을 미리 파악하고, 예외가 발생하지 않도록 차단하는 것이 좋다.

#### add()
```java
public void add(final User user) throws DuplicateUserIdException {
      try {
          jdbcTemplate.update("insert into users values(?, ?, ?)"
                  , user.getId(), user.getName(), user.getPassword());
          throw new SQLException();
      } catch (SQLException e) {
          if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
              throw new DuplicateUserIdException(e);
          else
              throw new RuntimeException(e);
      }
  }
```
- SQLException에서 상황에 맞게 예외를 변경하여 던져줄 수 있다.
- 회원 아이디가 중복이라면 DuplicationUserIdException으로 전환해주고 그 이외에는 RuntimeException으로 전환해준다.
- 어딘가에서 DuplicationUserIdException를 잡아서 처리할 수 있다면 이는 체크예외보다는 런타임 예외로 만드는 것이 좋다.
- 그 외에도 RuntimeException으로 포장하여 예외를 던저주었다.
- 이제 이 메서드를 사용하는 오브젝트는 SQLException을 처리하기 위해 trows 선언을 할 필요가 없으며 필요한 경우에 DuplicationUserIdException로 예외를 잡으면 된다.
- 이렇게 런타임 예외를 일반화해서 사용하는 방법은 여러모로 장점이 많으나 런타임예외로 만들게 되면 더 컴파일러가 처리를 강제하지 않으므로 더 많은 주의가 필요하다.


#### 애플리케이션 예외
- 런타임 에외 중심의 전략은 낙관적인 예외처리 기법이라고 할 수 있다.
- 반면 시스템 또는 외부의 예외상황이 원인이 아니라 애플리케이션 자체 로직에 의해 의도적으로 발생시키고 반드 시 처리해야하도록 하는 예외들이 존재한다.
- 이를 흔히 **애플리케이션 예외라고** 한다.
- 사용자가 요청한 금액을 자신의 계좌에서 출금한다고 할 때 잔고가 부족하다면 -1과 같은 값을 리턴받아 검증을 한 후 처리를 할 수 있다
- 하지만 그러한 방법보다 정상적인 흐름을 따르는 코드는 그대로 둔 후, 잔고 부족과 같은 예외를 만들고 비즈니스적인 의미를 띤 예외를 던지도록 하는 것이 더 유연할 것이다.
- 이러한 예외들은 **체크 예외로** 만든다. 그래서 개발자가 잊지 않고 해당 예외를 의도적으로 체크 예외로 만든다.

### 4.1.5 SQLException은 어떻게 됐나?
- JdbcTemplate를 사용하면 sq을 날려도 SQLException을 처리하지 않도록 해준다.
- 이는 스프링의 예외 처리 전략과 원칙을 잘 알고 있어야 한다.
- 그전에 UserDao에서 SQLException이 과연 복구가 가능한 예외인지에 대해 알아볼 필요가 있다.
- 99%의 SQLException은 코드 레벨에서 복구할 방법이 없다.
- 즉 SQL문법을 잘못 작성했거나, DB서버가 다운되었거나 등의 프로그램의 오류 혹은 개발자의 부주의로 인해 발생하는 경우이거나, 통제할 수 없는 외부상황 때문에 발생한 것이다.
- 이러한 예외들은 애플리케이션 레벨에서 복구할 방법이 없으니 관리자나 개발자에게 빨리 예외가 발생했다는 사실을 알려지도록 전달하는 방법밖에 없다.
- 그렇기 때문에 **예외 처리 전략을** 잘 활용하여 이러한 예외를 언체크 예외로 전환해준다면 개발 시 계속해서 예외를 throws할 필요가 없어질 것이다.
- **스프링의 JdbcTemplate는 바로 이 예외처리 전략을 따르고 있는데 JdbcTemplate은 SQLException를 런타임 예외인 DataAccessException으로 포장해서 던저주기 때문에 따로 예외를 처리할 필요가 없어 진 것이다.**

---

## 4.2 예외 전환
- **예외를 다른 것을 바꿔서 던지는 예외 전환의 목적은 총 두가지 이다.**
- **첫번째는** 4.1에서 살펴본 것처럼 런타임 예외로 포장해서 굳이 필요하지 않은 catch/truews를 줄여주는 것이다.
- **두번쨰는** 로우레벨의 예외를 좀 더 의미있고 추상화된 예외로 바꿔서 던져주는 것이다.

### 4.2.1 JDBC의 한계
- JDBC는 자바를 이용해 DB에 접근하는 방법을 추상화된 API 형태로 정의해놓고, 각 DB 업체가 JDBC 표준을 따라 만들어진 드라이버를 제공하게 해준다.
- 내부 구현은 DB마다 다르겠지만 JDBC의 Connection, Statement, ResultSet 등의 표준 인터페이스를 통해 그 기능으로 추상화하여 제공해주기 때문에 JDBC API만 알고 있다면 DB의 종류에 상관없이 일관된 방법으로 프로그램을 개발할 수 있다.
- 하지만 DB 종류에 상관없이 사용할 수 있는 데이터 엑세스 코드를 작성하는 일은 쉽지 않다. JDBC를 통해 다양한 DB를 사용할 순 있지만 DB가 변경될 때 코드의 변경없이 계속해서 사용하는 것을 보장해주지 못한다.
- 이러한 문제점들에 대해 한번 알아보자.

#### 비표준 SQL
- 첫 번째는 JDBC 코드에서 사용하는 SQL이다. SQL은 어느 정도 표준화된 언어이고 몇 가지 표준 규약이 있긴 하지만 대부분 DB별 서로다른 문법들이 존재하기 때문에 문제가 발생할 수 있다.
- 즉 DB별 특정 문법을 사용한다면 이러한 SQL문장은 해당 DB에 종속이 된다.
- 이러한 문제의 해결책으로는 DB별로 DAO를 만들거나 SQL문을 따로 분리하여 디비에 맞게 사용할 수 있도록 할 수 있을것이다. 혹은 표준 SQL만을 사용하도록 강제하면 된다.
- 표준 SQL을 사용하도록 강제하는 것은 현실성이 없으니 DAO를 DB별로 만들어 사용하여 SQL을 외부에서 독립시켜 바꿔쓸 수 있게하는 것이 좋은 방법인데 이는 7장에서 직접 구현해보록 한다.

#### 호화성 없는 SQLException의 DB 에러 정보
- 두 번째는 바로 SQLExcpetion이다. DB를 사용하다 발생하는 예외의 원인은 매우 다양하다.
- 문제는 DB마다 SQL만 다른 것이아닌 에러의 종루와 원인도 모두 제각각이라는 것이다. 그렇기 때문에 JDBC는 SQLException하나에 이러한 모든 에러를 담아버린다.
- JDBC는 SQLException예외 하나만을 던지도록 설계되어 있고 예외가 발생한 원인은 SQLException 안에 담긴 에러 코드와 SQL 상태정보를 참조해봐야 한다.
- **하지만 이러한 에러코드도 DB 벤더마다 고유의 에러코드를 사용하므로 서로 다를 수 있다.**
- getSQLState()메서드로 상태정보를 잡아 특정 스펙에 정의된 상태코드를 사용할 수 있지만 JDBC 드라이버에서 제대로된 상태코드를 만들어 주지 않는다.
- 이러한 문제들은 SQLException만으로 DB에 독립적인 유연한 코드를 작성하는 것은 불가능에 가깝다.

### 4.2.2 DB에러 코드 매핑을 통한 전환
- DB종류가 바뀌더라도 DAO를 수정하지 않으려면 위에서 언급한 두 가지 문제를 해결해야 한다.
- SQL에 대해서는 뒷 부분에서 다룰 것이며 현재는 SQLException의 비표준 에러코드와 SQL 상태정보에 대한 해결책을 알아본다.
- 믿을 수 없는 상태코드는 고려하지 않고 DB별로라도 정확하게 명시되는 에러코드를 통해 해결할 수 있다.
- 해결 방법은 먼저 DB별 에러 코드를 참고해서 발생한 예외의 원인이 무엇인지 해석해 주는 기능을 만드는 것이다.
- 키 값이 중복될 경우 catch문에서 디비별 에러코드를 확인하고 DuplicateKeyExcpetion과 같은 예외로 전환하여 제공해준 다면 DB가 달라지더라도 이 예외를 처리할 수 있게 될 것이다.
- **스프링은 DataAccessException이라는 런타임 예외에서 서브클래스에 더 세분화된 예외 클래스들을 정의하여 SQLException을 다양한 상황에 알맞게 변환해 준다.**
- 여기서의 가장 큰 문제는 디비별로 에러코드가 제각각이라는 점이다. 그래서 스프링은 이를 해결하기 위해 DB별 에러 코드를 분류해서 스프링이 정의한 예외 클래스와 매핑해놓은 **에러 코드 매핑 정보 테이블을 이용한다.**
- 이렇게 DB별로 알아서 에러코드별 예외를 전환해주기 때문에 DB가 달라져도 같은 종류의 에러라면 동일한 예외를 받을 수 있다.
- 그렇기 때문에 JdbcTemplate을 이용한다면 JDBC에서 발생하는 DB관련 예외는 거의 신경쓰지 않아도 가능하다.

### 4.2.3 DAO 인터페이스와 DataAccessException 계층구조
- DataAccessException은 JDBC의 SQLException을 전환하는 용도로만 만들어진 건 아니다.
- JDBC 외의 자바 데이터 액세스 기술에서 발생하는 예외에도 적용된다.
- 자바에는 JDBC이외에도 JPA와 같은 데이터 엑세스 표준 기술이 존재한다.
- 즉 DataAccessException은 의미가 같은 예외라면 데이터 엑세스 기술의 종류와 상관없이 일관된 예외가 발생하도록 만들어준다.
- 데이터 엑세스 기술에 독립적인 추상화예외를 제공해주는 것이다. 스프링은 왜 이렇게 했을까?

#### DAO인터페이스와 구현의 분리
- DAO를 사용하는 가장 중요한 이유는 데이터 엑세스 로직을 담은 코드를 성격이 다른 코드와 분리하기 위해서이다.
- 또한 DAO는 전략 패턴을 적용해 구현 방법을 변경해서 사용할 수 있게 만들기 위함도 존재한다.
- 그런데 DAO의 사용 기술과 구현 코드는 전략 패턴과 DI르 통해서 DAO를 사용하는 클라이언트에게 감출 수 있지만, 메서드 선언에 나타나는 예외정보에 문제가 발생할 수 있다.

```java
public interface UserDao{
  public void add(User user) // 이렇게 선언이 불가능하다. DAO에서 사용하는 데이터 액세스 기술의 예외를 던져야 하기 떄문이다.

  public void add(User user) throws SQLExcpetion;
  // SQLException을 던진다면 JDBC가 아닌 데이터 엑세스 기술로 DAO 구현을 전환하면 사용할 수 없다.


  public void add(User user) throws PersistentExcpetion;
  public void add(User user) throws HibernateExcpetion;
  // 이렇게 데이터 액세스 기술 변 예외가 다르기 때문이다.
}
```
- 인터페이스로 메서드의 구현은 추상화할 수 있지만 구현 기술마다 던지는 에외의 차이로 인해 제대로된 추상화가 어렵다.
- 가장 단순한 방법으로 throws Expceiton을 활용하면 되겠지만 이 방법은 너무 무책임하다.
- 다행히도 JDBC이후에 나온 기술은 런타임 예외이므로 따로 선언을 해주지 않아도되기 때문에 SQLException에 대해서만 잘 처리하면 된다.
- 이는 JDBC를 사용하는 DAO에서 SQLException를 RuntimeException으로 포장해서 던져주면된다.
- 그렇다면 interface로 공통되는 기능을 뽑아내는 것이 가능해 질 것이다.

> - 이제 DAO에서 사용하는 기술에 완전히 독립적인 인터페이스로 선언이 가능해졌다.
> - 하지만 이것으로 충분할지는 생각해봐야한다.
> - 대부분의 데이터 엑세스 예외는 애플리케이션 단에서 복구가 불가능하거나 할 필요가 없는 것들이지만 **모두가 그런 것은 아니다.**
> - 중복키 에러와 같은 예외들은 비즈니스 로직에서 의미 있게 처리할 수 있는 예외이기 때문이다.
> - 문제는 데이터 엑세스 기술에 따라 같은 상황에서 서로 다른 예외를 던진다는 것이다.
> - 각각 SQLException, PersistentExcpetion, HibernateExcpetion등이 던져질 것이다.
> - 이는 클라이언트가 DAO의 사용 기술에 따라 예외처리 방법이 달라져야 하기 때문에 DAO의 기술에 의존적이 된다.
> - **단지 DAO 인터페이스 추상화하고, 일부 기술에서 발생하는 체크 예외를 런타임 예외로 전환하는 것만으로 불충분하다.**

#### 데이터 엑세스 예외 추상하와 DataAccessException 계층구조
- **위와 같은 이유들로 인해 스프링은 자바의 다양한 데이터 액세스 기술을 사용할 때 발생하는 예외들을 추상화해서 DataAccessException 계층구조 안에 정리해놓았다.**
- 스프링은 JdbcTemplate의 SQLException를 DB별 에러코드에 맞게 매핑하여 DataAccessException의 서브 클래스 중 하나로 전환해서 던져준다.
- 뿐만아니라 하이버네이트, JPA와 같은 기술을 사용할 때에도 동이랗게 DataAccessException을 사용할 수 있으며 기술마다 발생할 수 있는 예외들의 성격이 다른 것들도 공통되는 성격들을 추상화시켜 계층구조로 분류 해놓았다.
- 만약 JDBC에서 ORM의 기술인 낙관적 락을 구현하였고 이러한 예외들을 처리하고 싶을 때 DataAccessException를 상속받은 OptimisticLockingFailureException을 상속받아 예외를 구현하게 되면 JPA, 하이버네이트에서 발생하는 낙관적락에 대한 예외와 동일한 방식으로 처리할 수 있게 된다.

### 4.2.4 기술에 독립적인 UserDao 만들기
#### 인터페이스 적용
- 이제 UserDao 클래스를 인터페이스와 구현으로 분리해보도록 하자.

```java
public interface UserDao {
    void add(User user);

    User get(String id);

    List<User> getAll();

    void deleteAll();

    int getCount();

// setDataSource() 메서드는 구현에 따라 달라 질 수 있고 userDao를 사용하는
// 클라이언트가 알 필요 없는 정보이므로 인터페이스에 정의하지 않는다.
}
```
- 이제 이를 구현하는 UserDao를 만들 고 빈 설정시 해당 구현체를 빈으로 등록 해주면된다.

#### 테스트 보완
- 이전에 작성했던 테스트를 그대로 사용하더라도 빈으로 등록된 구현체를 주입해 주기 때문에 무방하다.

#### DataAccessException 활용 시 주의사항
- 스프링을 활용하면 DB 종류나 데이터 액세스 기술에 상관없이 키 값이 중복이 되는 상황에서는 동일한 예외가 발생하리라 생각할 수 있지만 **그렇지 않다.**
- DuplicateKeyExcpetion은 JDBC를 이용하는 경우에만 발생하며 JPA나 하이버 네이트에서는 에러 코드들이 세분화되어 있지 않아 다른 예외로 던저진다.
- 하이버네이트는 ConstraintViolationException을 던지고 스프링은 이를 보고 좀 더 포괄적인 DataIntegrityViolationException으로 변환할 수 밖에 없다.
- 물론 DuplicateKeyExcpetion도 DataIntegrityViolationException의 한 종류이므로 DataIntegrityViolationException으로 처리를 하면 되지만 더 포괄적이므로 세분화된 정보를 얻기가 어려울 수 있다.
- **DataAccessException이 기술에 상관없이 어느 정도 추상화된 공통 예외로 변환해 주지만 근본적인 한계 때문에 완벽하지는 않다.**
- 그러므로 사용시 주의를 기울여야 한다. DataAccessException을 잡아서 처리하는 코드를 만들려고 한다면 학습테스트를 만들어서 실제로 전환되는 예외의 종류를 확인해둘 필요가 있다.

```java
public class UserDaoTest{
  @Autowired
  Userdao userdao;

  @Autowired
  DataSource datasource;

  @Test
  void sqlExcpetionTranslate(){
    userdao.deleteAll();

    try{
      userdao.add(user1);
      userdao.add(user1);
    }
    catch(DuplicateKeyExcpetion e){
      // 현재 예외에서 중첩되어 있는 SQLException을 가져온다.
      SQLException sqlE = (SQLException)e.getRootCause();

      // dataSource를 이용해 SQLExcetpionTanslator를 생성한다.
      SQLExcetpionTanslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

      // 가져온 SQLException이 DuplicateKeyExcpetion으로 제대로 변환되는지 확인해본다.
      assertThat(set.translate(null, null, sqlE)).isInstanceOf(DuplicateKeyExcpetion.class);

    }
  }
}
```

---

## 4.3 정리
- 예외를 잡아서 아무런 조치를 취하지 않거나 의미 없는 throws 선언을 남발하는 것은 위험하다.
- 예외는 복구하거나 예외처리 오브젝트로 의도적으로 전달하거나 적절한 예외로 전환해야 한다.
- 좀 더 의미 있는 예외로 변경하거나, 불필요한 cath.trhows를 피하기 위해 런타임 예외로 포장하는 두 가지 방법의 예외 전환이 있다.
- 복구할 수 없는 예외는 가능한 한 빨리 런타임 예외로 전환하는 것이 바람직하다.
- 애플리케이션의 로직을 담기 위한 예외는 체크 예외로 만든다.
- JDBC의 SQLException은 대부분 복구가 불가능한 예외이므로 런타임 예외로 포장해야 한다.
- SQLException의 에러 코드는 DB에 종속되기 때문에 DB별 독립적인 예외로 전활할 필요가 있다.
- 스프링은 DataAccessException을 통해 DB에 독립적으로 적용 가능한 추상화된 런타임 예외 계층을 제공한다.
- DAO를 데이터 액세스 기술에서 독립시키려면 인터페이스 도입과 런타임 예외 전환, 기술에 독립적인 추상화된 예외로의 전환이 필요하다.
>>>>>>> 363609ada5e21b8286070b6745b03a254153e003