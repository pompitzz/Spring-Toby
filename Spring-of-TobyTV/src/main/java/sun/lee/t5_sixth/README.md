# \# 6. Reactive Streams (2) Operators
- Publisher, Subsriber사이에 데이터를 조작하거나, 스케줄링 하거나, 퍼블리싱을 컨트롤할 새로운 Publisher를 만드는 것이다.
- Publisher -> Data -> Subscriber
- 이렇게 동작이 이루어질 때 Operator를 통해 데이터를 가공하여 
- Publisher -> [Data1] -> Operator -> [Data2] -> Subscriber
- 가공된 데이터를 Subscriber에게 넘겨줄 수 있다.
- map, reduce Operator를 구현하면서 원리에 대해 알아본다.


### [V1](Ex1Operators.java)
- 각 메서드 호출들을 로깅하는 LogSubscriber를 정의하였다.
- 그리고 이터러블을 받아 Sub들에게 Push해주는 Publisher는 정의한다.
- Pub - Sub사이에 연산을 해줄 map, reduce, sum Operator들을 정의한다.
- Operator는 또 다른 하나의 Pub가 되며 람다식을 활용하여 구현할 수 있다.
- Operator에서 사용하는 Sub는 Delegate형식으로 구현하여 onNext만 오버라이딩해서 해당 기능만 정의한다.
- 이런식으로 간단하게 Operator를 구현할 수 있다.
- map의 경우 Operator는 모든 연산값을 Pub에게 넘겨주지만 reduce의 경우 초기값을 활용하여 마지막 한번의 값만 넘기기 때문에 OnComplete를 호출할때 한번만 넘겨 준다.

### [V2](Ex2OperatorsGenerics.java)
- V1에서 구현했던 기능들을 제네릭스로 구현할 수 있다.

### [V3](Ex3Reactor.java)
- 스프링 5의 리액터를 사용하여 이전에 구현하였던 Pub, Sub시스템을 간단하게 구현할 수 있음을 확인해본다.
- 동작과정을 log를 찍어 map에서와 reduce에서 어떻게 다른지를 확인하고 V2에서 구현한 동작과정과 같은것을 확인하였다.

### [V4](Ex4Controller.java)
- Spring Web Controller에서 Publisher로 반환하더라도 요청본문으로 만들어 주는것을 확인할 수 있다.  
- Publisher만 정의하면 스프링에서 알아서 Subscriber를 제공해주는 것을 알아보았다.