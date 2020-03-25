## 7.1 스프링 기술과 API를 효과적으로 학습하는 방법
- 스프링은 일관된 방식으로 개발된 프레임워크이므로 모든 코드와 API가 동일한 원리에 기반을 두고 만들어져 있다.
- 스프링의 모든 것은 DI라고 해도 과언이 아니다. 스프링 자신도 DI를 이용해 만들어 졌다.
- 스프링의 핵심 엔진이라고 할 수 있는 애플리케이션 컨텍스트/컨테이너와 웹 기술의 중심인 DispatcherServlet도 모두 DI 원리를 이용해서 확장할 수 있도록 만들어져 있다.

### 7.1.1 빈으로 등록되는 스프링 클래스와 DI
- 어떤 오브젝트가 빈으로 사용된다는 건 다른 빈에 의해 DI 돼서 사용되는 서비스라는 의미이다.
- 즉 클라이언트를 가지고 해당 클라이언트와 의존관계를 갖게 된다.
- 그리고 다른 빈이나 정보에 의존하고 있다는 의미를 가지고 있다.
- 대부분의 빈은 자신이 클라이언트가 돼어 다른 빈 오브젝트를 사용하도록 만들어져 있다.
- 그러므로 스프링의 기능을 파악하고 사용하기 위해선 이 두가지의 관점으로 스프링이 제공하는 빈 클래스를 살펴보는 것이 좋은 스프링 학습 방법이다.
- 스프링이 모든 기능을 DI를 이용해서 제공하기 때문이다.
- JdbcTemplate를 사용하고 트랜잭션 AOP를 사용하기 위해 DataSourceTransactionManager를 빈으로 등록해야한다.

#### 구현 인터페이스 분석
- IDE로 DataSourceTransactionManager을 찾아들어가 해당 구현체들을 하나씩 분석해보면 DataSourceTransactionManager이 무엇을 구현하고 어떤 용도로 쓰이는지 있는지 이해할 수 있다.
- 더해서 해당 인터페이스들의 구현체가 무엇이 있고 어떤 구조로 클래스가 구성되고 있는지 확인하면 좋을 것이다.

#### 프로퍼티 분석
- DataSourceTransactionManager의 프로퍼티 수정자 메서드를 확인해보면 어떤 대상에게 의존하고 있는지를 알 수 있다.
- 각 수정자 메서드들의 설명을 보면서 설정 방법 및 디폴트 구현체들을 확인할 수 있을 것이다.

#### DI/확장 포인트 분석
- 빈 클래스의 프로퍼티 중 인터페이스 타입의 프로퍼티를 보면 해당 구현 클래스의 확장 포인트라고 생각하면 된다.
- DataSourceTransactionManager에서 의존대상인 DataSource는 인터페이스로 구성되어 있어 다양한 구현체 뿐만아니라 직접 확장하여 기능을 구현할 수 있다.
- 대표적으로 DelegatingDataSource를 이용하여 다양한 기능들을 추가할 수 있다.

**LazyConnectionDataSourceProxy**
- 트랜잭션 매니저와 실제 DataSource사이에서 DB 커넥션 생성을 최대한 지연 시켜주는 기능을 제공한다.
- 공유해서 사용되는 리소스를 최대한 늦게 이용하게 하는 전형적인 프록시 패턴이다.
- 트랜잭션 시작시 getconnection()을 통해 커넥션을 가져오더라도 실제 트랜잭션을 열지 않는다.
- Statement를 통해 실제 SQL을 날릴 때 까지는 가짜 커넥션을 돌려주고 SQL을 날릴 때 트랜잭션을 실행하여 커넥션 리소스 효율을 극대화 시킨다.
- 이는 JPA와 같은 ORM에서 2차 캐시를 활용할 때 매우 효율적이다.
- 2차 캐시를 사용할 때 DAO 메서드가 실행되면 실제 DB를 조회하는 대신 메모리나, 파일에 저장된 캐시 결과를 가져올 경우가 있는데 이런 경우 DB 커넥션을 가져오고 트랜잭션을 시작하는 건 자원 낭비가 된다.
- 이럴때 LazyConnectionDataSourceProxy를 활용하면 때에따라 커넥션을 아예 사용하지 않고 캐싱만을 이용하여 리소스를 조회할 수 있을 것이다.

**AbstractRoutingDataSource**
- 추상 클래스로 상속을 통해 기능을 추가하고 사용해야 한다. 스프링은 AbstractRoutingDataSource의 서브 클래스로 IsolationLevelDataSourceRouter를 제공하고 있다.
- AbstractRoutingDataSource는 다중 DataSoruce에 대한 라우팅을 제공하는 프록시다.
- 여러개의 DataSoruce가 존재하지만 DAO나 트랜잭션 매니저에는 하나의 DataSource만 존재하는 것처럼 사용하도록 만들어야 할 때가 있다.
- 에를들어 DB를 두 개 만들어 하나는 데이터 조작과 조회가 모두 가능한 마스터 DB로 두고, 다른 DB는 마스터 DB를 실시간으로 복제해서 동일한 정보를 갖게 하지만, 수정 트랜잭션은 허용하지 않고 조회전용으로만 사용하는 구성을 만들 수 있다. 쿼리에 대한 부하를 분산시키기 위해 사용되는 기법이다.
- 조회의 경우에는 읽기 전용 DB를 사용하고 수정도 일어나는 경우는 마스터 이비를 사용하도록 할 때 AbstractRoutingDataSource를 확장해서 클라이언트와 타깃 사이의 접근을 제어하는 프록시를 두어 사용할 수 있다.
- AbstractRoutingDataSource는 룩업키와 DataSource 맵을 정의한 후 룩업키를 어떻게 결정할 지는 서브클래스에서 구현하도록 추상메서드로 정의되어 있다.

```Java
public class ReadOnlyRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return readOnly ? "READONLY" : "READWRITE";
    }
}
```
- 현재 트랜잭션이 READONLY로 설정되어 있는지를 확인 후 룩업 키를 반환해준다.
- 이제 이를 빈으로 등록하고 DataSource 빈을 지정해주면 된다.

```xml
<bean id="dataSourceRouter" class="ch6.ReadOnlyRoutingDataSource">
    <property name="targetDataSources">
        <map>
            <entry key="READWRITE" value-ref="masterDataSource"/>
            <entry key="READONLY" value-ref="readOnlyDataSource"/>
        </map>
    </property>
    <property name="defaultTargetDataSource" ref="masterDataSource"/>
</bean>
```
- 기존 코드는 전혀 손댈 필요도 없이 단지 설정을 통해 구 개의 DB를 동시에 활용하면서 필요에 따라 사용할 DB로 바꿀 수 있게 되었다.
- 스프링이 직접제공하는 IsolationLevelDataSourceRouter는 트랜잭션 격리수준에 따라 다른 DataSource를 사용하게 한다.
- 이 외에도 사용자 레벨에 따라 다른 DB를 사용하도록 하거나, 시간대에 따라 DB를 변경하거나하는 등 DataSource 라우팅 기법을 손 쉽게 적용할 수 있다.

> DataSourceTransactionManager에 DataSource라는 인터페이스 타입의 프로퍼티가 있는 것을 보면 자연스럽게 스프링에는 DataSource를 구현하는 클래스들이 어떤게 있는지 찾아보는 습관을 들이는 일이 스프링 학습에 많은 도움이 될 것이다.
