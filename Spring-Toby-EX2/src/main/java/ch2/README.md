
# Chapter2. 데이터 액세스 기술
- 자바에는 JDBC API 이외에도 JDBC의 사용을 추상화해주는 iBatis, SQLMapper라든가, 하이버네이트, JPA와 같은 ORM 기술도 존재한다.
- 스프링은 자바의 주요 데이터 액세스 기술을 모두 지원한다. 스프링이 지원한다는 의미는 스프링의 철학과 일관된 프로그래밍 모델을 유지하면서, 이런 기술을 사용할 수 있다는 뜻이다.
- 2장에서는 스프링의 데이터 액세스 기술에 관한 기본 개념을 다시 정리해보고, 스프링이 지원하는 핵심 데이터 액세스 기술의 구체적인 사용 방법과 선택할 수 있는 옵션을 알아보자.

## 2.1 공통 개념
- 모든 기술에 공통적으로 적용되는 원칙과 기본 개념을 간단히 정리해보자.

### 2.1.1 DAO 패턴
- 데이터 액세스 계층은 DAO 패턴이라는 불리는 방식으로 분리하는 것이 원칙이다.
- DAO 패턴은 DTO 또는 모메인 오브젝트만을 사용하는 인터페이스를 통해 데이터 액세스 기술을 외부에 노출하지 않도록 만드는 것이다.

#### DAO 인터페이스 DI
- DAO는 인터페이스를 이용해 접근하고 DI 되도록 만들어야 한다.
- DAO 인터페이스에는 구체적인 데이터 액세스 기술과 관련된 어떤 API나 정보도 노출하지 않는다.
- 인터페이스를 만들 때에는 DAO의 모든 public 메서드가 아닌 **서비스 계층 코드에서 의미 있는 메서드만 인터페이스로 공개해야 한다.**

#### 예외처리
- 데이터 액세스 중에 발생하는 예외는 대부분 복구가 불가능하다.
- 따라서 DAO 밖을 던져질 때에는 런타임 예외여야 한다. 그러므로 DAO 내부에서 발생하는 모든 예외는 런타임 예외로 전환 해야 한다.
- 서비스 계층 코드는 DAO가 던지는 대부분의 예외를 직접 다루어야 할 이유가 없다.
- 허나 가끔식 중복키, 락킹에 대한 예외는 DAO가 던지는 에외를 잡아 비즈니스 로직에 적용하는 경우가 있다.
- 그런데 이런 의미 있는 예외처리 할 때 예외의 일관성이 없다면 이는 서비스계층이 데이터 액세스 계층에게 의존하게 된다.
- 그러므로 예외를 추상화하여 일관된 예외를 제공해야 하는데 스프링은 이를 지원해준다.
- 데이터 액세스 기술 API를 직접 사용할 때는 AOP를 이용해 예외를 전환해주는 기능을 사용하면 될 것이다.

### 2.1.2 템플릿과 API
- 스프링은 DI의 응용인 템플릿/콜백 패턴을 이용해 try/catch/finally와 같은 판에 박힌 코드를 피하고 꼭 필요한 바뀌는 내용만을 담을 수 있도록 템플릿을 제공한다.
- 미리 만들어진 작업 흐름을 담은 템플릿은 반복되는 코드를 제거해줄 뿐만아니라 예외 변환과 트랜잭션 동기화 기능도 함께 제공해준다.
- 템플릿의 단점은 데이터 액세스 기술의 API를 직접 사용하는 대신 템플릿이 제공하는 API를 이용해야 한다는 점이다.
- 그래서 스프링은 일부 데이터 액세스 기술을 템플릿 대신 해당 기술의 API를 그대로 사용하게 해주기도 한다.
- 데이터 액세스 기술이 제공하는 확장 기법과 AOP등을 이용해 예외 변환과 트랜잭션 동기화를 제공해줄 수 있기 때문이다.

### 2.1.3 DataSource
- JDBC를 통해 Db를 사용하기 위해선 Connection 타입의 DB 연결 오브젝트가 필요하다.
- Connection은 모든 데이터 액세스 기술에서 사용되는 필수 리소스 이다.
- Connection을 매번 새로 만들고 제거하는것은 비효율적이므로 DB 커넥션 풀을 사용한다.

#### 학습 테스트와 통합 테스트를 위한 DataSource
- 개발 중에 사용하던 테스트용 DataSource를 그대로 운영서버에 적용해서 애를 먹는 경우가 있으니 주의해야 한다.
- 이런 실수를 피하기 위해서는 프로파일과 설정파일들을 적극활용하는 것이 좋을 것이다.

**SimpleDriverDataSource**
- 스프링이 제공하는 가장 단순한 DataSource 구현 클래싀다.
- 매번 새로운 커넥션을 만들기 때문에 테스트에 적합하다.

**SingleConnectionDataSource**
- 하나의 물리적인 DB 커넥션만 만들고 이를 계속사용한다.
- 순차적으로 진행되는 통합 테스트에는 사용 가능하나 동시에 두 개 이상의 스레드가 동작하는 경우에는 하나의 커넥션을 공유하게 되므로 위험하다.

#### 오픈소스 또는 상용 DB 커넥션 풀
**아파치 Commons DBCP**
- 아파치의 Commons 프로젝트에서 찾을 수 있는 커넥션 풀 라이브러리이다.

**상용 DB 커넥션 풀**


#### JDNI/WAS DB 풀
- 대부분의 자바 서버는 자체적으로 DB 풀 서비스를 제공해준다.
- DB 풀 라이브러리를 사용해 애플리케이션 레벨의 전용 풀을 만드는 대신 서버가 제공하는 DB 풀을 사용해야 하는 경우 JNDI를 통해 서버의 DataSource에 접근해야 한다.


---

## 2.2 스프링 JDBC
- JDBC는 자바의 데이터 액세스 기술의 기본이 되는 로우레벨 API이다.
- JDBC는 표준 인터페이스를 제공하고 각 DB 벤더와 개발팀에서 이 인터페이스를 구현한 드라이버를 제공하는 방식으로 사용된다.
- JDBC는 모든 자바의 데이터 액세스 기술의 근간이 된다. ORM에서도 내부적으로는 DB와의 연동을 위해 JDBC를 이용한다.
- JDBC만을 사용하면 간단한 SQL을 하나 실행하는데도 매우 번잡해지고, DB에 따라 발생하는 일관성 없는 체크 예외들을 다루어야 하고, SQL은 코드내에 직접 문자로 다루워야하는등의 단점들이 존재한다.
- 스프링 JDBC는 이러한 JDBC 개발의 장점과 단순성을 그대로 유지하면서 기존의 단점들을 템플릿/콜백 패턴을 이용해 극복할 수 있게 해주며 간결한 형태의 API를 제공해준다.

### 2.2.1 스프링 JDBC기술과 동작원리
#### 스프링의 JDBC 접근 방법
**SimpleJdbcTemplate**
- JdbcTemplate와 NamedParameterJdbcTemplate에서 가장 많이 사용되는 기능을 통합하고 자바 5이상의 장점을 최대한 활용할 수 있게 만든 것이다.

**SimpleJdbcInsert, SimpleJdbcCall**
- DB가 제공해주는 메타정보를 활용해서 최소한의 코드로만 단순한 JDBC 코드를 작성하게 해준다.
- 메타정보에서 컬럼 정보와 파라미터 정보를 가져와 삽입용 SQL과 프로시저 호출작업에 사용해주기 때문에 매우 편리하다.

#### 스프링 JDBC가 해주는 작업
**Connection 열기와 닫기**
- 스프링에서는 Connection과 관련된 모든 작업을 알아서 진행해준다.
- 예외가 발생했을 때도 문제 없이 열린 모든 Connection을 닫아준다.
- 트랜잭션의 경계설정에따라 커넥션이 열리고 닫히는 시점이 달라지게 된다.

**Statement 준비와 닫기**
- SQL정보가 담긴 Statement 또는 PreparedStatement를 생성하고 필요한 준비작업 또한 스프링 JDBC가 알아서 진행한다.
- 스프링 JDBC가 Statement를 준비하는 동안에 필요한 정보인 파라미터 바인딩 정보, 오브젝트들만 개발자가 책임을 진다.

**Statement 실행**
- SQL이 담긴 Statement를 실행하는 것도 스프링 JDBC의 몫이다.

**ResultSet 루프**
- ResultSet에 담긴 쿼리 실행 결과가 한 건 이상이라면 루프를 돌면서 각각의 로우를 처리해줘야 하는데 이를 스프링 JDBC가 알아서 해준다.
- ResultSet 각 로우의 내용을 어떻게 오브젝트에 담을것인지만 루프안에서 실행되는 콜백으로 만들어 템플릿에게 전달해주면 된다.

**예외처리와 변환**
- JDBC 작업중 발생하는 모든 예외는 스프링 JDBC의 예외 변환기가 처리하여 런타임 예외인 DataAccessException로 변환해준다.
- DataAccessException 계층 내에서 의미있는 예외로 변경해준다.

**트랜잭션 처리**
- 스프링 JDBC는 트랜잭션 동기화 기법을 이용해 선언적 트랜잭션 기능과 맞물려서 돌아간다.
- 트랜잭션이 시작한 후에 JDBC의 작업을 요청하면 진행중인 트랜잭션에 참여한다.
- 스프링 JDBC를 사용하면 트랜잭션과 관련된 모든 작업에 대해서는 신경쓰지 않아도 된다.

> - 스프링 JDBC가 이런 대부분의 작업을 해주므로 개발자는 데이터 액세스 로직마다 달라지는 부분만 정의해주면 된다.
> - 그리고 DB 커넥션을 가져올 DataSoruce를 정의해주면 된다.


---

## 2.4 JPA
- EJB3.0과 함께 등장한 JavaEE와 JavaSE를 위한 영속성 관리와 ORM을 위한 표준 기술이다.
- 자바 ORM기술은 오래전부터 많은 프레임워크와 기술을 통해 발전해왔다.
- 포준 기술의 한 가지인 JDO는 엔티티빈 처럼 특정 컨테이너에서 동작하는 오브젝트를 사용하는 대신 POJO를 사용하는 ORM 기술이었다.
- 상용 ORM 프레임워크인 TopLink 제품도 오래전에 등장해서 많은 사용자를 확보하고 있었고 오픈소스 ORM으로는 하이버네이트가 큰 인기를 끌고 있었다.
- 그러던 중 ORM 전문가들이 대거 참여한 EJB3.0의 스펙 작업에서 기존 엔티티빈을 JPA라는 이름으로 바꾸고 표준 자바 영속성 관리와 ORM 기능을 제공하는 범용 ORM 기술로 발전시켰다.
- JPA구현체로는 JBoss의 하이버네이트, 아파치의 OpenJPA, 이클립스의 EclipseLink, 오라클의 TopLink Essentials등이 있다.

### 2.4.1 EntityManagerFactory 등장
- JPA 퍼시스턴스 컨텍스트에 접근하고 엔티티 인스턴스를 관리하려면 JPA의 핵심 인터페이스인 EntityManager를 구현한 오브젝트가 필요하다.
- EntityManager는 JPA에서 두 가지 방식으로 관리된다.
- 하나는 애플리케이션이 관리하는 EntityManager이고, 다른 하나는 컨테이너가 관리하는 EntityManager이다.
- 컨테이너가 관리하는 EntityManager를 위해선 JavaEE 환경과 서버가 필요하다.
- 애플리케이션이 관리하는 EntityManager는 JavaEE와 JavaSE에서 모두 사용 가능하다.
- 스프링은 JPA 컨테이너가 포함된 JavaEE 5 이상의 서버에 배치될 수도 있고, JPA를 지원하지 않는 J2EE 서버나 톰캣 같은 서블릿 컨테이너에 배치될 수도 있다.
- 전자의 경우 JavaEE 서버가 직접 관리하는 EntityManager를 활용할 수 있으며 반면에 JPA를 지원하지 않는 서버 환경이라면 스프링이 직접 EntityManager를 관리하는 컨테이너의 역할을 해줄 수도 있다.
- 혹은 애플리케이션이 관리하는 EntityManager를 이용하게 할 수 있다.

> 어떤 방식으로든 반드시 EntityManagerFactory를 빈으로 등록해야 된다. 스프링에서는 세 가지 방법을 통해 EntityManagerFactory 타입의 빈을 등록할 수 있다.

#### LocalEntityManagerFactoryBean
- LocalEntityManagerFactoryBean은 JPA 스펙의 JavaSE 기동 방식을 이용해 EntityManagerFactory를 생성해준다.
- LocalEntityManagerFactoryBean을 빈으로 등록하면 PersistentProvider 자동 감지 기능을 통해 프로퍼티더를 찾고 META-INF/persistence.xml 에 담긴 퍼시스턴스 유닛의 정보를 활용해서 EntityManagerFactory를 생성한다.
- 이 방식은 JPA만을 사용하는 단순한 환경에는 적용할 수있으나 스프링의 빈으로 등록한 DataSoruce를 사용할 수 없어 큰 제약사항이 존재한다.
- 이외에도 많은 제약이 존재해서 실전에서는 사용하지 않는다.

#### JavaEE 5 서버가 제공하는 EntityManagerFactory
- JPA는 JavaSE 환경보다는 JavaEE에서 서버가 제공하는 JPA 프로바이더를 통해 사용하는 것이 일반적이다.
- 스프링 애플리케이션에서는 JNDI를 통해 서버가 제공하는 EntityManager와 EntityManagerFactory를 제공받을 수 있다.
- 이 방식은 JavaEE 5 이상의 서버에 배치하고 JPA 프로바이더와 서버가 요구하는 설정을 해뒀을 경우의 전제이다.
- 스프링에서는 특별히 관여할 수 있는 작업은 없으며 모든 JPA기능은 서버와 JPA의 퍼시스턴스 유닛 설정에 따르게 된다.

#### LocalContainerEntityManagerFactoryBean
- LocalEntityManagerFactoryBean은 스프링이 직접 제공하는 컨테이너 관리 EntityManager를 위한 EntityManagerFactory를 만들어준다.
- 이 방법을 이용하면 JavaEE 서버에 배치하지 않아도 컨테이너에서 동작하는 JPA의 기능을 활용할 수 있으며, 스프링이 제공하는 일관성 있는 데이터 액세스 기술의 접근 방법을 적용할 수 있고 스프링의 JPA 확장 기능도 활용할 수 있다.
- 빈으로 등록시 DataSource를 프로퍼티에 넣어주면 생성할 수 있다.

**loadtimeWeaver**
- JPA는 POJO 클래스를 ORM의 엔티티로 사용한다.
- POJO 방식의 단점은 한번 오브젝트가 만들어지면 그 뒤에는 컨테이너가 직접 관리할 수 없다.
- 엔티티 사이의 관계도 JPA의 인터페이스를 이용해 접근하는게 아닌 POJO 오브젝트끼리 연결되어 있다.
- 따라서 JPA 프로바이더나 컨테이너가 특별한 기능을 제공하기 위해 끼어들 여지가 없다.
- JPA는 그래서 단순한 자바 코드로 만들어진 엔티티 클래스의 바이트코드를 직접 조작해서 확장된 기능을 추가하는 방식을 이용한다.
- 이를 통해 엔티티 오브젝트 사이에 지연된 로딩이 가능하고, 엔티티 값의 변화를 추적할 수 있으며, 최적화와 그룹 페칭등의 고급 기능을 적용할 수 있다.
- 이렇게 이미 컴파일된 클래스 바이트코드를 조작해서 새로운 기능을 추가하는것을 **바이트 코드 향상 기법** 이라고 한다.
- 바이트코드를 향상시키는 방법은 두 가지가 존재한다.
- 첫째는 바이트코드를 빌드중에 변경하는 것이다. JPA 벤더가 제공하는 바이트코드 컴파일러가 필요하며 매번 빌드 작업 중에 이 과정을 반드시 거쳐야 한다.
- 두번째는 런타임 시에 클래스 바이트코드를 메모리에 로딩하면서 다이내믹하게 바이트코드를 변경해서 기능을 추가하는 방법이다.
- 보통 두번째 방법이 더 간단하여 많이 사용한다.
- 런타임시에 클래스를 로딩하면서 기능을 추가하는 것을 **로드타임 위빙** 이라고하고, 이런 기능을 가진 클래스를 **로드타입 위버** 라고 한다.
- 자바 5이상에서는 JVM을 가동할 때 javaagent 옵션을 줘 JVM이 로딩하는 모든 자바 클래스를 조작할 수 있는 기능을 넣을 수 있다.
- 대부분의 JPA의 구현 제품은 자바 에이전트를 이용해 로드타입 위버를 적용할 수 있다.
- 자바에이전트를 사용하는 방법은 간단해 보이지만 단점이 많이있다.
- 자바 에이전트는 JVM을 통해 로딩되는 모든 클래스를 일일이 다 확인한다. 실제 향상시켜야할 클래스는 많아야 엔티티 클래스 몇십 개 뿐인데 JVM이 로딩하는 모든 클래스를 확인하는 작업은 성능에 영향을 줄 수 있다.
- 서버환경에서는 서버를 가동하는 JVM에 자바 에이전트를 설정해줘야하는데 이는 서버 운영 정책과 관리 측면에서 부담을 줄 수 있다.
- 그래서 스프링은 자바 에이전트를 대신할 수 있는 특별한 클래스 로더를 이용해서 로드타임 위빙 기능을 적용할 수 있는 방법을 제공한다.
- 스프링은 JPA의 엔티티 클래스 향상 외에도 바이트코드 조작이 필요한 기능이있다.
- 스프링의 로드타입 위버 기능과 사용방법은 5장에서 다루게 된다.

#### 트랜잭션 매니저
- 스프링의 EntityManager를 사용하려면 적절한 트랜잭션 매니저가 등록되어야 한다.
- 스프링 JDBC는 자체가 자동으로 트랜잭션 모드를 가지고 있기 때문에 트랜잭션 매니저가 없어도되지만 JPA는 반드시 트랜잭션 안에서 동작해야하므로 필요하다.
- JpaTransactionManager를 등록하고 EntityManagerFactory빈을 프로퍼티에 등록해주면 된다.

### 2.4.2 EntityManager, JpaTemplate
#### @PersistenceUnit
```java
@PersistenceUnit EntityManagerFactory emf;
```
- @PersistenceUnit은 JPA 표준 스펙에 나온 DI 애노테이션이다.
- @PersistenceUnit으로 주입받게되면 스프링에 의존성이 없는 순수 JPA코드가 될 수 있다.

##### @PersistenceContext
- DAO가 컨테이너로부터 JPA의 EntityManager를 @PersistenceContext를 통해 주입받을 수 있다.
- EntityManager는 스프링의 빈으로 등록되지 않고 EntityManagerFactory 타입의 빈을 생성하는 LocalEntityManagerFactoryBean이 빈으로 등록된다.
- 따라서 @Autowired와 같은 스프링의 DI 방법으로 EntityManager를 주입받을 수 없다.
- EntityManager는 Connection과 같이 멀티스레드에서 공유해서 사용할 수 없다.
- 사용자의 요청에 따라 만들어지는 스레드별로 독립적인 EntityManager가 만들어져 사용돼야 한다.
- 즉 EntityManager는 트랜잭션 마다 하나씩 만들어져 사용되고 트랜잭션과 함께 사라져야 한다.
- 그러면 어떻게 DI를 받고 여러 스레드가 동시에 사용할 수 있을까?
- 그것은 @PersistenceContext로 주입받은 EntityManager는 실제 EntityManager가 아닌 현재 진행중인 트랜잭션에 연결되는 퍼시스턴스 컨텍스트를 갖는 일종의 프록시이기 때문이다.

```java
@PersistenceContext(type=PersistenceContextType.TRANSACTION) EntityManager em;
```
- 트랜잭션과 생명주기를 같이하는 PersistenceContextType.TRANSACTION이 디폴트 값이다.

#### JPA 예외변환 AOP
- JpaTemplate를 사용하지 않고 JPA를 직접 이용하는 경우에 JPA 예외를 스프링의 DataAccessException 예외로 전환시킬 수 있다.
- 스프링의 AOP를 이용하여 JPA 예외를 스프링 예외로 전환시켜주는 부가기능을 추가할 수 있다.
- @Repository 애노테이션을 붙이면 해당 DAO 클래스의 메서드는 AOP를 이용한 예외 변환 기능이 부가될 빈으로 선정된다.
- PersistenceExceptionTranslationPostProcessor를 빈으로 등록하여야 @Repository 애노테이션이 붙은 빈을 찾아 예외를 변환해준다.

---

## 2.5 하이버네이트
- 하이버네이트는 가장 크게 성공한 오픈소스 ORM 프레임워크이다.

### 2.5.1 SessionFactory 등록
- 하이버네이트에넌 JPA의 EntityManagerFactory처럼 핵심 엔진 역할을 하는 SessionFactory가 있다.
- SessionFactory는 엔티티 매핑정보와 설정 프로퍼티 등을 이용해 초기화한 뒤에 애플리케이션에서 사용해야 한다.
- 스프링에서는 SessionFactory를 빈으로 등록하고 초기화할 수 있도록 두가지 팩토리빈을 제공한다.

#### LocalSessionFactoryBean
- LocalSessionFactoryBean은 빈으로 등록된 DataSource를 이용해서 스프링이 제공하는 트랜잭션 매니저와 연동할 수 있도록 설정된 SessionFactory를 만들어주는 팩토리 빈이다.
- DB연결은 항상 스프링의 빈으로 등록한 DataSoruce를 이용하는 것이 모든 데이터 액세스 기술의 공통점이다.

#### AnnotationSessionFactoryBean
- 하이버네이트는 JPA처럼 엔티티 클래스에 애노테이션을 부여하고 이를 매핑정보로 사용하는 방법을 제공한다.
- 기본적으로 JPA에 정의된 매핑용 애노테이션을 그대로 사용할 수 있으며 추가로 하이버네이트가 제공하는 확장 애노테이션을 이용하면 하이버네이트의 고급 매핑정보를 애노테이션을 이용해 정의해줄 수 있다.

---

## 2.6 트랜잭션
- 스프링은 EJB등에서나 제공하던 엔터프라이즈드 서비스 중 하나인 트랜잭션 서비스를 POJO의 장점을 유지한채로 사용할 수 있게 만들어준다.
- DI로 대표되는, 스프링의 객체지향 설계 원칙에 충실한 핵심 기술이 이를 가능하게 해준다.

### 2.6.1 트랜잭션 추상화와 동기화
- 스프링 제공하는 트랜잭션 서비스는 트랜잭션 추상화와 트랜잭션 동기화 두 가지로 생각해볼 수 있다.
- 스프링의 트랜잭션 동기화는 트랜잭션을 일정 범위 안에서 유지해주고, 어디서든 자유롭게 접근할 수 있게 만들어준다.
- 트랜잭션 동기화는 트랜잭션 추상화, 데이터 액세스 기술을 위한 템플릿과 더불어 선언적 트랜잭션을 가능하게 해주는 핵심기술이다.

#### PlatformTransactionManager
- 스프링 트랜잭션 추상화의 핵심 인터페이스는 PlatformTransactionManager이다.
- 모든 스프링의 트랜잭션 기능과 코드는 이 인터페이스를 통해서 로우레벨의 트랜잭션 서비스를 이용할 수 있다.

```java
public interface PlatformTransactionManager extends TransactionManager {

	TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
			throws TransactionException;

	void commit(TransactionStatus status) throws TransactionException;

	void rollback(TransactionStatus status) throws TransactionException;
}
```
- PlatformTransactionManager은 세가지 메서드를 가지고 있다.
- 트랜잭션이 어디서 시작되고 종료하는지, 종료할 때 정상 종료인지 비정상 종료인지를 결정한다.
- 트랜잭션은 전파 수준에 따라 자유롭게 시작과 종료가 결정되기 때문에 begin()같은 메서드가 아닌 getTransaction으로 정의되어 있다.
- getTransaction의 반환타입인 TransactionStatus에는 현재 참여하고 있는 트랜잭션의 ID와 구분정보를 가지고 있다.
- 커밋 혹은 롤백 시 이 TransactionStatus를 이용하여 진행된다.

#### 트랜잭션 매니저의 종류
**DataSourceTransactionManager**
- Connection의 트랜잭션 API를 이용해서 트랜잭션을 관리해주는 트랜잭션 매니저다.
- 이 트랜잭션 매니저를 사용하려면 트랜잭션을 적용할 DataSoruce가 스프링의 빈으로 적용되어야 한다.
- JDBC API를 이용해서 트랜잭션을 관리하는 데이터 액세스 기술인 JDBC와 iBatis SqlMap으로 만든 DAO에 적용가능하다.

**JpaTransactionManager**
- JPA를 이용하는 DAO에는 JpaTransactionManager를 사용한다.
- 물론 JTA로 트랜잭션 서비스를 사용하는 경우에는 JpaTransactionManager가 필요없다.
- JpaTransactionManager에는 EntityManagerFactory를 등록해주어야 한다.
- JpaTransactionManager는 DataSourceTransactionManager가 제공하는 DataSoruce 레벨의 트랜잭션 관리 기능도 동시에 제공한다.
- 따라서 JDBC DAO를 사용할 수도 있다.

**HibernateTransactionManager**
- 하이버네이트 DAO에서는 HibernateTransactionManager를 사용한다.
- SessionFactory 타입의 빈을 프로퍼티로 넣어주면 된다.
- JpaTransactionManager과 마찬가지로 DataSoruce 트랜잭션 기능도 동시에 제공한다.

**JmsTransactionManager, CciTransactionManager**

**JtaTransactionManager**
- 하나 이상의 DB 또는 트랜잭션 리소스가 참여하는 글로벌 트랜잭션을 적용하려면 JTA를 이용해야 한다.
- JTA는 여러 개의 트랜잭션 리소스(DB, JMS등)에 대한 작업을 하나의 트랜잭션으로 묶을 수 있고, 여러 대의 서버에 분산되어 진행되는 작업을 트랜잭션으로 연결해주기도 한다.
- JTA 트랜잭션을 이용하려면 트랜잭션 서비스를 제공하는 WAS를 이용하거나 독립 JTA 서비스를 제공해주는 프레임워크를 사용해야 한다.
- JtaTransactionManager는 따로 프로퍼티 설정없이 빈을 등록 시 디폴트로 등록된 JNDI 이름을 통해 서버의 TransactionManager와 UserTransaction을 찾는다.

> - 디비가 하나라면 트랜잭션 매니저 또한 하나만 등록돼야하며 DB가 여러개라도 JTA를 이용해 글로벌 트랜잭션을 적용하면 JtaTransactionManager 하나만 등록돼야 한다.
> - 단 두개의 완전히 독립된 디비를 사용할 경우 두 개 이상의 트랜잭션 매니저를 등록할 수는 있다.

### 2.6.2 트랜잭션 경계설정 전략
- 트랜잭션 매니저를 빈으로 등록하고 JdbcTemplate이나 스프링 트랜잭션과 연동되는 EntityManager또는 Session을 사용하도록 DAO 코드를 작성했다면 다음은 트랜잭션의 경계를 설정하는 작업을 할 차례다.
- 트랜잭션 경계 설정방법은 코드에 의한 프로그램적인 방법과 AOP를 이용한 선언적인 방법을 구분된다.
- 전자는 트랜잭션을 다루는 코드를 직접만들고 후자는 AOP를 이용해 기존 코드에 트랜잭션 경계설정 기능을 부여해준다.

#### 코드에 의한 트랜잭션 경게설정
- 스프링 트랜잭션 매니저는 모두 PlatformTransactionManager를 구현하고 있다.
- 따라서 이 인터페이스로 현재 등록되어있는 트랜잭션 매니저 빈을 가져올 수 있다면 일관된 방식으로 트랜잭션을 제어할 수 있다.
- 하지만 PlatformTransactionManager를 직적쓰면 try/catch를 사용하여 트랜잭션을 직접 제어해야하는 불편함이 존재한다.
- 템플릿/콜백 방식의 TransactionTemplate를 사용하면 편리하게 사용할 수 있다.
- PlatformTransactionManager는 실무에선 잘 사용되지 않지만 PlatformTransactionManager를 이해하고 있어야 오류발생시 해결이 편리해질 것이다.

#### 선언적 트랜잭션 경계설정
- Vol.1에서 살펴본것 처럼 데코레이터 패턴을 적용한 트랜잭션 프록시 빈을 사용하여 트랜잭션을 제어한다.
- 프록시 AOP를 이용하여 해당 기능들을 적용시켜준다.
- 스프링 AOP는 JDK 다이내믹 프록시로 만들어 진것이므로 포인트컷은 기본적으로 인터페이스에 적용된다.
- 하지만 클래스만으로 프록시를 만들 수도 있다.
- 포인트컷은 가능한한 인터페이스에게 적용하는것이 쓸데없응 메서드에게 적용되는것을 막을 수 있을 것이다.
- 선언적 트랜잭션 경계설정은 xml혹은 @Transactional을 이용한다.
- Vol.1에서 자세히 다뤘으니 넘어가자.

#### 프록시 모드: 인터페이스와 클래스
- 스프링 AOP는 기본적으로 다이내믹 프록시 기법을 이용해 동작한다.
- 다이내믹 프록시를 적용하려면 인터페이스가 있어야 한다.
- 하지만 인터페이스를 구현하지 않은 클래스에 트랜잭션을 적용해야할 수 있다.
- 이때는 CGLib 라이브러리가 제공해주는 클래스 레벨의 프록시를 사용하는 클래스 프록시 모드를 사용하면된다.

**@Transactional의 클래스 프록시 설정**
- xml에서 \<tx:annotation-driven proxy-target-class="true"/>로 설정하면 된다.

**@Transactional은 클래스에 부여해야 한다.**
- 클래스 프록시모드에서는 인터페이스에 붙인 @Transactional 애노테이션은 구현 클래스로 그 정보가 전달되지 않는다.

**클래스 프록시의 제약사항을 알아야 한다.**
- 클래스 프록시는 타깃 클래스를 상속해서 프록시를 만드는 방법을 사용하므로 fianl 클래스에는 적용이 불가능하다.
- 클래스 프록시를 적용하면 클래스의 생성자가 두 번 호출된다. 상속을 통해 프록시를 만들기 때문에 발생하는 현상인데, 이 때문에 생성자에서 리소스를 할당하는 것 같은 중요한 작업은 피해야한다.
- (현재는 생성자가 한 번 호출된다.(?))

> 프록시 AOP는 메서드 레벨에서만 AOP를 적용할 수 있고 타켓 오브젝트에서 자기 자신을 호출할 때에는 AOP가 적용되지 않기 때문에 그럴 경우 트랜잭션이 제대로 동작되지 않을 수 있다.

#### 트랜잭션 속성 및, 전파, 고립은 Vol.1에 정리했으니 생략

### 2.6.4 데이터 액세스 기술 트랜잭션의 통합
- 스프링은 자바의 다양한 데이터 액세스 기술을 위한 트랜잭션 매니저를 제공해준다.
- 여러개의 DB를 사용하지 않는 한 트랜잭션 매니저는 한 개만 사용할 수 있다.
- 만약 DB는 하나이나 두 가지 이상의 데이터 액세스 기술을 동시에 사용하는 경우는 어떨까?
- JPA DAO로 일부 엔티티-테이블을 업데이트하는 것과 JDBC DAO로는 복잡한 DB전용 쿼리를 사용해 데이터를 가져오는 것을 하나의 트랝개션 안에서 진행시키고 싶을 수 있다.
- JPA를 사용하지만 때에따라 MyBatis같은 SQL 매퍼를 사용하고 싶을 때도 있을 것이다.
- 스프링은 두 개 이상의 데이터 액세스 기술로 만든 DAO를 하나의 트랜잭션으로 묶어서 사용하는 방법을 제공한다.

#### 트랜잭션 매니저별 조합 가능 기술
**DataSourceTransactionManager**
- DataSourceTransactionManager를 트랜잭션 매니저로 등록하면 JDBC와 iBatis 두 가지 기술을 함께 사용할 수 있다.
- 트랜잭션을 통합하려면 항상 동일한 DataSource를 사용해야 한다는 점을 잊지말자.
- JDBC와 iBatis Dao가 같은 DataSource로 부터 커넥션을 가져와 사용한다고 한다면 DataSourceTransactionManager이 트랜잭션 동기화를 진행해줄 것이다.

**JpaTransactionManager**
- JPA의 트랜잭션은 JPA API를 이용해 처리된다. 따라서 기본적으로는 JPA 단독으로 트랜잭션을 관리하게 된다.
- 그런데 스프링에서는 JPA의 EntityManagerFactory가 스프링 빈으로 등록된 DataSource를 사용할 수 있다.
- 그리고 이 DataSource를 JDBC DAO나 iBatis DAO에서도 사용할 수 있다.
- 이렇게 DataSoruce를 공유하면 JpaTransactionManager에 의해 세 가지 기술을 사용한 DAO 작업을 하나의 트랜잭션으로 관리해줄 수 있다.
- JpaTransactionManager가 같은 DataSoruce를 사용하는 Dao들에 대해 트랜잭션 동기화를 진행해준다.

**HibernateTransactionManager**
- JpaTransactionManager과 동일한 방식으로 트랜잭션 동기화를 진행해준다.

**JtaTransactionManager**
- 서버가 제공하는 트랜잭션 서비스를 JTA를 통해 이용하면 모든 종류의 데이터 액세스 기술의 DAO가 같은 트랜잭션 안에서 동작하게 만들 수 있다.
- JTA는 같은 DataSoruce를 사용하지 않더라도 하나의 트랜잭션으로 묻어줄 수 있다.
- 대신 이를 사용하기 위해서는 JTA 서버환경 구성이 필요하다.

#### ORM과 비 ORM DAO를 함께 사용할 때의 주의 사항
- 서로의 기술을 사용하는 것은 문제가 없으나 각 기술의 특징을 잘 이해하지 못하면 예상치 못한 오류가 발생할 수 있다.
- JDBC의 경우 메서드 호출 시 바로 SQL을 날리지만 JPA는 쓰기지연이 있기 때문에 이러한 기술적 차이로 인해 문제가 발생할 수 있다.
- 이를 해결하기 위해서는 의도적으로 flush를 날리거나, AOP같은 것을 이용해 JDBC의 DAO가 호출될 때마다 flush를 날리게하면 될 것이다.

### 2.6.5 JTA를 이용한 글로벌/분산 트랜잭션
- 한 개 이상의 DB나 JMS의 작업을 하나의 트랜잭션 안에서 동작하게 하려면 서버가 제공하는 트랜잭션 매니저를 JTA를 통해 사용해야 한다.
- JTA와 글로벌/분산 트랜잭션을 사용하기 위한 설정은 자바 서버마다 다르므로 해당 서버의 매뉴얼을 참고해서 등록하는 방법을 알아둬야 한다.
- 이건 진짜 사용할 때 보도록하자..

---

## 2.7 스프링3.1의 데이터 액세스 기술
### 2.7.1 persistence.xml 없이 JPA 사용하기
- JPA 엔티티 클래스가 담긴 패키지리스트를 LocalEntityManagerFactoryBean 빈의 packagesToScan 프로퍼티에 넣어주면 persistence.xml이 필요 없다.

```xml
<bean id="emf"
      class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="packagesToScan" value="ch2.step5.jpa"/>
    <property name="jpaProperties">
        <props>
            <prop key="eclipselink.weaving">false</prop>
        </props>
    </property>
    <property name="jpaVendorAdapter">
        <bean class="org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter">
            <property name="generateDdl" value="true"/>
            <property name="showSql" value="true"/>
        </bean>
    </property>
</bean>
```

### 2.7.2 하이버네이트 4 지원
**LocalSessionFactoryBean**
- 하이버네이트3의 LocalSessionFactoryBean과 이름은 같지만 AnnotationSessionFactoryBean과 유사한 특징을 가지고 있다.
- 이는 애노테이션 설정 정보가 보편화되고 있다는 것을 알 수 있다.

**LocalSessionFactoryBuilder**
- LocalSessionFactoryBuilder는 @Configuration 클래스에서 세션 팩토리 빈을 등록할 때 편리하게 사용할 수 있도록 만들어진 빌더 클래스이다.
- 하이버네이트용 트랜잭션 매니저 클래스나 OpenSessionInViewInterceptor 등도 하이버네이트4 패키지에 있는것을 사용해야 한다.

### 2.7.3 @EnableTransactionManager
- \<tx:annotation-driven/>과 동일한 컨테이너 인프라 빈을 등록해주는 자바 코드 설정용 애노테이션이다.
- @Transactional 애노테이션을 이용한 트랜잭션 설정을 가능하게 해준다.

#### 2.7.4 JdbcTemplate 사용 권장
- SimpleJdbcTemplate은 @Deprecated되어 버리고 자바 5의 기능을 적극 활용한 JdbcTemplate 사용을 권장한다.

---

## 2.8 정리
- DAO 패턴을 이용하면 데이터 액세스 계층과 서비스 게층을 깔끔하게 분리하고 데이터 액세스 기술을 자유롭게 변경해서 사용할 수 있다.
- 스프링 JDBC는 JDBC DAO를 템플릿/콜백 방식을 이용해 편리하게 작성할 수 있게 해준다.
- SQL 매핑 기능을 제공하는 iBatis로 DAO를 만들 때도 스프링의 템플릿/콜백 지원능을 사용할 수 있다.
- JPA와 하이버네이트를 이용하는 DAO에서는 템플릿/콜백과 자체적인 API를 선택적으로 사용할 수 있다.
- 트랜잭션 경계설정은 XML의 스키마 태그와 애노테이션을 이용해 정의할 수 있다. 또한 트랜잭션 AOP를 적용할 때 프록시와 AspectJ를 사용할 수 있다.
- 스프링은 하나 이상의 데이터 액세스기술로 만들어진 DAO를 같은 트랜잭션 안에서 동작하도록 만들어주며 하나 이상의 DB를 사용할때는 JTA를 이용하면 된다.
