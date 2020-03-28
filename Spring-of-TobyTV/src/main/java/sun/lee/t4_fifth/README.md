# \# 5. Reactive Streams (1) 리액티브 스트림의 특징
- 리액티브 스트림은 논 블록킹 백프레셔를 통해 비동기 스트림 처리의 기준을 제공해주는 API이다.

> - 새로나온 기술의 사용법을 익혀서 적용해서 개발하는것은 어렵지 않다.
> - 이렇게 복잡한 기능들도 사실 처음에는 매우 단순하고 분명한 목표에서 시작했을 것이다.
> - 어떠한 기술을 응용하기위해선 해당 기술의 코어의 기반이되는 분명한 목표를 반드시 이해하는것이 좋다!

### Reactive Programing?
- 외부의 이벤트가 발생하면 그에 대응하는 방식으로 코드를 작성하는 것

---

> 리액티브 프로그래밍에 대해 알아보기 전에 Iterable과 Observer Pattern에 대해 알아본다.

### [1. 이터러블과 옵저버패턴](./Ex1IterableAndOb.java)
- 흔히 이터러블은 Pull으로 사용하는 측에서 원하는 값을 가져오는 방식으로 구현된다.
- 그와 반대로 옵저버 배턴은 Subject에서 Push하여 옵저버들에게 값을 전달해주게 된다.
- 이러한 특징으로 인해 옵저버 패턴은 다른 쓰레드에서도 값을 push해주는 등의 활용처가 많다.
- 하지만 옵저버 패턴은 큰 2가지의 단점이 존재한다.

#### 단점
- 첫번째로 옵저버 패턴에는 Complete, 마지막 push에 대한 개념이 없다.
- 그렇기 때문에 Subject가 마지막으로 push하였다고 하더라고 옵저버 패턴은 이를 알지 못한다.
- 실 서버에서 사용하게 된다면 커넥션을 계속 물고 있지 않을까??라는 생각이 든다.(맞는지는 모르겠다).
- 그리고 두번째로는 Error를 처리하는 방식이 규정되어 있지않다.
- 이러한 단점을 극복하여 나온것이 MS개발자가 만든 리액티브 익스텐션이며 이가 점차 발전하여 리액티브 스트림이 만들어 졌다.

### [2. 리액티브 스트림](./Ex2Main.java)
- 자바 9부터는 표준 라이브러리로 리액티브 스트림을 제공해준다.
- [API](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/package-summary.html)를 보면 4가지 인터페이스만 존재하는 것을 알 수 있다.

#### [Publisher](./Ex2MyPub.java)
- Publisher에는 프로바이더, 즉 옵저버 패턴에서 Subject라고 할 수 있으며 subscribe라는 하나의 메서드만 존재한다.
- 파라미터에는 Subscriber가 존재하며 onSubscribe를 통해 Sub에게 이벤트를 발생시킬 수 있다.
- 옵저버 패턴과 다르게 onSubscribe에는 리액티브 스트림의 API중 하나인 Subscription를 파라미터로 가지고 있다.

#### [Subscription](./Ex2MyPub.java)
- Subscription은 Subscriber, Publisher의 중간에서 서로간의 속도차이를 해결하는 역할을 해준다.
- Subscription에는 request 메서드가 존재하는데 이 메서드를 통해 요청의 개수를 조정하여 속도를 조정할 수 있다.

![img](./flow.png)
- 해당 이미지를 잘 이해하면 Publisher와 Subscriber의 동작흐름을 잘 이해할 수 있다.

#### [Subsriber](./Ex2MySub1.java)
- Subscriber는 옵저버 패턴에서 옵저버에서 조금 더 기능이 확장된 API이다.
- Subscriber에는 onSubscribe(), onNext(), onError(), onComplete() 메서드가 존재한다.
- onSubsribe는 아까 Publisher의 subscribe메서드에서 호출하는 그 메서드이다. 즉 처음 호출을 시작할 때 호출되는 메서드이다.
- onNext()는 옵저버의 update()와 동일하게 해당 결과를 받아오는 메서드이다. 이는 Subscription의 request 수에 맞게 조정할 수 있다.
- onComplete는 요청이 더 이상없어 완료된 상태를 알려주며 이 메서드가 호출되면 관계는 종료될 것이다.
- onError는 에러가 발생했을 때 호출되는 메서드로 이 메서드 또한 호출시 관계는 종료 된다.  
- 이 메서드들은 Publisher에서 onSubscribe()가 호출된 후 해당 파라미터의 Subscription에 의해 적절하게 실행된다.
- request의 파라미터의 숫자만큼 onNext()를 호출하여 요청을 하고, 해당 숫자까지 도달하면 onComplete()를 통해 완료를 알려주며, 에러가 발생하면 onError()를 통해 에러를 전달해준다.
