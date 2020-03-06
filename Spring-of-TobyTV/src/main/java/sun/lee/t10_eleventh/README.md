# \# 11. Reactive Streams (7) CompletableFuture
- 자바 8이후부터 이전에 만들어보았던 Completion보다 더 뛰어난 기능을 제공해주는 CompletableFuture를 제공해준다.
- CompletableFuture를 이용하면 비동기 작업을 매우 간단하게 만들 수 있다.
- CompletableFuture는 Future와 CompletionStage를 구현하고 있다.
- Future는 자바에서 비동기 통신을 할 때 가장 기본이되는 인터페이스로 이전부터 계속 다루었었다.
- CompletionStage에는 Completion을 구현했을 때 처럼 하나의 비동기작업을 수행하고 그 작업에 의존적인 비동기 명령을 수행할 수 있는 기능들이 들어있다.
- CompletableFuture는 쓰레드를 생성하고 submit을 날리는 등의 작업을 하지 않고도 간단하게 새로운 쓰레드를 만들어 작업를 수행할 수 있다.
- CompletableFuture는 리스트의 모든 값이 완료될 때 까지 기다릴지 하나의 값만 완료될 때 기다릴지 선택할 수도 있으며 람다표현식과 파이프라이닝으로 구조적으로도 이쁘게 동작시킬 수 있다.

### [V1](Ex1CFuture.java)
- CompletableFuture의 특징에 대해 알아보았다.
- CompletableFuture자체는 비동기 작업의 결과를 담고 있는 클래스이다.
- CompletableFuture의 결과는 get()을 했을 때 비로소 실제 값을 가져오게 된다.

### [V2](Ex2CFuture.java)
- CompletableFuture로 어떻게 연쇄적인 비동기 작업을 수행할 수 있는지 알아보았다.
- CompletableFuture 자체는 데몬쓰레이드 이므로 일시적으로 쓰레드풀을 만들어 주었다.

#### test1
- CompletableFuture.runAsync로 간단하게 Runnable을 파라미터로 가지는 비동기 작업을 수행할 수 있다.
- thenRun을 통해 연쇄적으로 작업을 이어나갈 수 있다. 이는 앞의 작업에 의존적이기 때문에 같은 쓰레드에서 동작되는 것을 알 수 있다.
#### test2
- runAsync뿐만아니라 supplyAsync로도 비동기 작업을 진행할 수 있다.
- 물론 thenApply, thenAccept또한 가능하다.
- supply, apply등으로 리턴한 값을 다음 작업에서 이어받을 수 있다.

#### test3
- supply, apply에서 리턴한 값이 CompletableFuture일 때가 있을 것이다.
- 이럴경우 themCompose를 사용하면 FlatMap과 유사하게 CompletableFuture안의 값을 뽑을 수 있다.

#### test4
- exceptionally를 통해 에러를 유연하게 처리할 수 있다.
- 예외가 발생하면 그 다음 연쇄작업을 무시하고 exceptionally가 있는 메서드로 넘어간다.
- 그렇기 때문에 매번 예외처리를 할 필요없이 논리적인 단위로 예외처리를 하여도 된다.

#### test5
- thenApplyAsync와 같이 then절에서도 원한다면 비동기 처리가 가능하다.
- 시간이 많이 소모되는 작업을 할 때는 따로 비동기로 빼는게 좋을 것이다.
- 쓰레드 풀을 따로 설정하지않으면 기본 쓰레드 풀(ForkJoinPool.commonPool)이 사용된다.
- 원한다면 Executor를 파라미터에 넣어주면된다.

---

### [연쇄적인 비동기 API 요청](Ex3CompotableFutureController.java)
- 지난번에 Completion을 만들어서 수행했던 비동기 API 요청기능들을 CompotableFuture로 구현할 수 있다.
- AsyncRestTemplate으로 요청하여 반환받는 ListenableFuture 타입은 불가피하다.
- 그렇기 때문에 ListenableFuture를 CompotableFuture로 변환해주는 작업을 수행해주는 메서드를 정의하였다.
- ListenableFuture의 콜백에 CompotableFuture를 등록시켜주면 된다.
- 이제 그 메서드를 기점으로 CompotableFuture를 통한 연쇄적인 비동기 통신이 가능해졌다.
- 비동기 통신은 ListenableFuture를 반환하므로 구현한 메서드를 계속 사용하면서 연쇄적으로 수행시킨다.
- 해당 메서드는 CompotableFutue를 반환하기 때문에 thenCompose를 사용하면 된다.
- 지난번에는 내부 비동기 처리를 myService에서 @Async를 이용하여 수행하였다.
- 이제는 myService에서는 동기적으로 요청을 처리하게 놔두고 thenApplyAsync를 사용하여 간단하게 비동기로 처리할 수 있게되었다.

> - 이러한 기능은 스프링 4이후 부터 자바8이상을 사용하여 구현할 수 있다.
> - 여기에서 더 고성능으로 프로듀서 컨슈머간의 속도차를 완하시키는 다양한 기법과 더 세밀하고 확장가능하게 비동기 처리를 할 수 있는 리액티브 프로그래밍 기법이 스프링 5부터 지원된다.
> - 그것을 사용하자!


