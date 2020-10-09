# \# 7. Reactive Streams (3) Scheduler
- 비동기 동작을 구현할 때 한 쓰레드 내에서 기능을 구현한다면 서버일 경우 커넥션이 계속 유지되기 때문에 문제가 발생할 것이다.
- 즉 실제 리액티브 프로그래밍에서 Publisher와 Subscriber는 같은 쓰레드내에서 동작되는 일이 거의 없다.
- 서로 다른 쓰레드에서 동작을 시킬 때 스케줄러를 활용하면 간단하게 구현할 수 있다.


### [1. Scheduler](Ex1Scheduler.java)
- 스케줄러에는 subscribeOn, publishOn 두 가지 동작방식이 대표적으로 존재한다.
- subscribeOn의 경우 값을 전달해주는 Publisher가 블럭킹 I/O같은 작업을 하여 느리게 동작할 경우 **Publisher를 다른 쓰레드에서 동작시키는 방법이다.**
- publishOn은 반대로 값을 소모하는 Subscriber의 속도가 느리게 동작할 경우 **Subscriber를 다른 쓰레드에서 동작시키는 방법이다.**
- 쓰레드는 아래와 같이 Executors를 통해 생성할 수 있고 스프링에서 제공해주는 Customize를 통해 쓰레드 이름을 간단하게 설정할 수 있다.
- 이 쓰레드를 이용하여 subscribeOn, publishOn, 그리고 이 둘을 동시에 적용한 방식을 구현하였다.
- subscribeOn의 쓰레드내역을 보면 publisher가 다른 쓰레드에서 동작되기 때문에 모든 동작이 새로 생긴 쓰레드에서 동작된다.
- publishOn의 경우 subscriber가 다른 쓰레드에서 동작되기 때문에 onSubscribe 호출 후에 다른 쓰레드에서 동작되게 하였다.
- 따로 생성한 쓰레드는 적절한 시점에 종료시켜주었다. 

```java
public class Temp{
    public static void main(String[] args){
      ExecutorService es = Executors.newSingleThreadExecutor(customThreadName("subscribeOn - "));
    }
    private static CustomizableThreadFactory customThreadName(String name) {
            return new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return name;
                }
            };
        }
}
```

### [2. 리액터의 Scheduler](Ex2FluxSc.java)
- 위에서 만든 스케줄러들을 리액터로는 매우 간단하게 구현할 수 있다.
- 그냥 subscribeOn, publishOn 메서드에 Scehdulers로 쓰레드를 만들어 넘겨주면 된다.

### [3. 리액터의 Interval](Ex3Interval.java)
- 데이터를 주기적으로 샘플링하거나 통계를 낼 때 유용한 Interval에 대해 알아보았다.
- 인터벌은 **데몬 쓰레드**로 구현되어 있기 때문에 일반 쓰레드가 종료되면 함께 종료된다.
- 인터벌은 주기를 정하고 무한정 그 일을 수행하는데 take를 통해 개수를 제한할 수 있다.

#### 데몬쓰레드
- 일반 쓰레드의 작업을 돕는 보조 쓰레드로 일반 쓰레드가 종료되면 함께 종료된다.
- 대표적으로 가비지 컬렉터가 있다.

### [4. Interval 구현](Ex4IntervalImpl.java)
- 인터벌을 직접 구현해보았다.
- Executors.newSingleThreadScheduledExecutor();를 통해 간단하게 주기적으로 동작하는 쓰레드를 만들 수 있다.
- 이를 활용하여 Pub에서 주기적으로 onNext를 호출해주면 인터벌처럼 동작시킬 수 있다.
- 그리고 오퍼레이터를 두어서 onNext가 호출되는 카운트를 세고 10이 넘어가면 Subscripton의 cancel을 호출하여 쓰레드를 멈추게 하였다.
- 이를 통해 리액터의 Interval처럼 해당 작업의 개수를 제한할 수 있었다.
