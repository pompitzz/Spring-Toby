# \# 10. Reactive Streams (6) AsyncRestTemplate의 콜백 헬과 중복 작업 문제 해결하기
- 이전에 본 연속되는 비동기 통신의 콜백 헬은 구조적으로 매우 복잡하다.
- 그리고 에러 처리도 계속해서 해줘야하는 불편함이 존재한다.
- 이를 극복하기 위해 클래스를 구조적으로 설계하여 간단하게 비동기 처리를 가능하게 해보자.

> 테스트를 위한 설정은 저번과 동일하게 진행한다.
>
### 테스트를 위한 설정들..
#### [RemoteService](../t8_nineth/RemoteService.java)
- 요청을 받은 서버에서 한번 더 요청을 할 수 있는 서버를 정의한다.
- 핸들러들을 보면 단순 String을 반환하지만 2초동안 딜레이가 있다.

#### [Test](../t8_nineth/LoadTest.java)
- Url 매핑값을 파라미터로 가지는 test메서드가 존재한다.
- 해당 테스트는 100개의 쓰레드풀을 만들고 각 쓰레드별로 서버에게 요청을 보내 총 100번의 요청을 보낸다.
- CyclicBarrier를 ㅎ이용하여 쓰레드들을 담아놓고 한번에 동작시키게 정의하였다.
- 각 요청이 처리되는 시간과 요청이 다 처리된 후의 시간을 로깅한다. 
- 각 Controller에 main 메서드를 생성하고 main 메서드에서 테스트를 수행한다.

#### 쓰레드 설정
- 클라이언트의 요청을 받는 서버의 쓰레드는 1개로 설정한다.
- 서버에서 또 API를 요청하는 서버는 쓰레드 개수를 1000개로 설정한다.
---

### [V1](Ex1Completion.java)
- Completion클래스를 만들어 구조적으로 단순한 비동기 처리를 가능하게 할 것이다.
- Completion클래스는 비동기 작업의 콜백 결과를 complete(), error()로 호출 받을 수 있다.
- 그리고 Completion는 다음 작업을 넘겨받을 next Completion과 각 작업에 필요한 Consumer, Function을 가진다.
- 비동기 작업을 특징을 보면 첫번째 작업은 의존하는 대상이 없지만 두번째 작업부터는 앞의 작업에 의존하게 된다.

#### from(ListenableFuture<ResponseEntity<String>> lf)
- 첫번째 작업은 static 생성메서드(from())로 구현한다. 첫번째 API 호출이므로 파라미터는 API의 반환값이 되며 해당 메서드에서는 Comletion을 생성하고 콜백으로 complete(), error()를 호출하게 설정한다.

#### andAccept(Consumer<ResponseEntity<String>> con)
- 두번째로는 최종 결과를 처리하는 andAccept를 구현한다. 최종 결과를 처리하기 때문에 따로 반환값은 없기때문에 Consumer를 파라미터로 가진다.
- 해당 Consumer를 가지는 Completion를 생성하고 현재의 next에 생성된 Completion를 설정한다.
- 결과를 넘겨야 하므로 생성된 Completion을 반환한다.

#### andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func)
- 세번째로 비동기의 결과를 받아 새로운 비동기를 호출하는 andApply 메서드를 구현한다. 결과를 받아 새로운 비동기 호출을 하기 때문에 파라미터는 Function이 되어야 한다.
- andApply도 andAccept와 동일하게 Function을 가지는 Comletion을 생성하고 현재의 next에 생성된 Completion을 설정한다.
- 결과를 넘겨야 하므로 생성된 Completion을 반환한다.

#### complete(ResponseEntity<String> s)
- 네번째로 비동기 호출의 콜백이 발동했을 때 호출되는 complete메서드를 구현한다. comlete는 ListenableFuture에서 비동기 결과를 전달인자로 넘겨줄 것이다.
- 이 메서드는 만약 next가 존재한다면 next이 run() 메서드를 파라미터와 함께 호출하면된다.

#### run(ResponseEntity<String> res)
- 마지막으로 complete에서 호출될 때 결과를 처리하는 run메서드를 구현한다.
- 이 메서드는 만약 현재 Completion에 Consumer가 있다면 Consumer를 파라미터를 이용하여 결과르 최종적으로 처리하면된다.
- 그리고 Consumer가 아닌 Function이 존재한다면 이는 비동기 동작이 추가로 존재하는 것이므로 리턴값의 ListenableFuture를 통해 또 한번 콜백을 추가해준다.

#### [테스트](Ex1MyController.java)
- 한번의 비동기 요청을 통해 결과를 받아오는 테스트와 여러번의 비동기 요청을 하는 테스트 모두 정상적으로 동작한다.


> 여기서의 문제점은 run메서드에서 if문을 통해 Consumer와 Function을 비교하였다는 것이다. if문으로 하나하나 처리하는 것은 클래스의 상속을 이용하여 깔끔하게 분리할 수 있다.

---

### [V2](Ex2Completion.java)
- run을 분리하기위해 Completion을 상속받는 AcceptCompletion, ApplyCompletion을 만든다.
- AcceptCompletion는 최종연산을 담당하는 클래스이므로 Consumer를 가지고 있다.
- ApplyCompletion는 중간연산을 담당하므로 Function을 가지고 있다.
- Completion의 run은 정의만 해놓고 AcceptCompletion, ApplyCompletion에서 run이 호출될 때 각 Consumer와 Function을 처리한다.
- AcceptCompletion, ApplyCompletion가 생성되었다는 것은 Consumer, Function이 존재한다는 것이기 때문에 따로 if문이 필요없어졌다.

---

### [V3](Ex3Completion.java)
- 이제 에러를 처리하는 기능을 구현해보자.
- 에러처리 방법은 특정 지점에 에러처리 메서드가 들어가면 그 이전에 발생한 비동기 통신 중 하나라도 에러가 발생하면 통신을 중단한다.
- 만약 에러가 없다면 다음 메서드로 결과를 그대로 넘겨준다.

#### andError(Consumer<Throwable> econ)
- 에러가 발생되면 호출 될 andError() 메서드를 구현한다.
- 에러가 발생한다면 거기서 통신이 멈추므로 Consumer를 파라미터로 받는다.
- ErrorCompletion클래스를 생성하고 andError의 파라미터를 멤버로 갖게 설정한다.
- 그리고 andAccept와 동일하게 next를 설정하고 결과를 넘겨야 하므로 생성된 Completion을 반환한다.

#### error(Throwable e)
- Completion에서 error()메서드가 호출되면 next의 error를 그냥 호출해주면된다.
- 왜냐하면 andError가 호출되기 전까지는 에러가 발생해도 계속해서 넘겨줘야하기 때문이다.
- ErrorCompletion의 error가 호춛될 때 Consumer를 처리하여 에러를 처리하게 하면된다.

#### run(ResponseEntity<String> res) 
- ErrorCompletion의 run()메서드가 호출될 때는 에러가 발생하지 않은 것이므로 그대로 next의 run을 호출시켜주면된다.

> - 현재 기능의 문제점은 Completion들의 파라미터가 지정되어있기 때문에 내부 비동기 호출을 할 수 없다. 그렇다고 새로운 메서드를 만드는것은 우아하지 못하다.
> - 제네릭스를 적용하여 기능을 확장시켜보자.

---

### [V4](Ex4Completion.java)
- 이전의 Completion을 넘겨받아서 수행하고, 다음 Completion에게 넘겨줘야하므로 제네릭스의 타입 파라미터는 두개가 존재해야 한다.
- 앞의 파라미터는 이전 Completion에서 넘어온 파라미터이고 뒤의 파라미터는 다음 Completion에게 전달할 파라미터이다.
- 대부분은 그냥 적용하면 된다.
- 대신 from은 처음 호출될 때 사용되고 static 메서드이므로 메서드 타입 파라미터를 적용해준다.
- 그리고 andApply의 경우 andApply를 호출하는 대상의 비동기 통신 결과를 현재 Completion이 알 수 없으므로 넘겨주는 타입은 메서드 타입 파라미터를 정의해야 한다. 

> - 이렇게 구현한 기능보다 훨씬 더 많고 잘 구현되어 있는 CompletableFuture가 자바 8이후부터 생겨났으므로 실제 사용할 때에는 CompletableFuture를 사용하자.
> - 기술의 동작원리를 알고 쓰는것과 모르고 쓰는것은 나중에 확장이 필요할 때 많은 차이를 낼 수 있다. 그러므로 이렇게 구현한 기능을 한번 더 직접 구현해보자.!! 
 
