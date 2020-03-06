# \# 11. Reactive Streams (9) Mono
- Mono는 하나의 아이템을 처리할 때 사용하는 Publisher의 일종이다.

#### Mono.just
```java
@GetMapping("/v1")
public Mono<String> hello() {
    Mono<String> m = Mono.just("Hello WebFlux").log();
    return m;
}
```
- 위에서 언급했듯이 Mono는 Publisher이다.
- Mono.just를 통해 특정 타입의 데이터를 Mono로 감싼 데이터로 만들 수 있다.
- Mono의 log()메서드를 사용하면 Publisher, Subscriber가 동작되는 과정을 로깅해준다.
- 결과를 확인해보면 onSubscribe -> request -> onNext -> onComplete의 일련의 과정이 로깅되는 것을 알 수 있다.
- 개발자는 Mono와 같은 Publisher를 통해 데이터를 가공하면 스프링이 알아서 Subscriber를 통해 subscibre하여 해댕 Publisher를 동작시켜준다.


#### Mono는 언제 동작될까? V1
```java
@GetMapping("/v2")
public Mono<String> hello2() {
    log.info("pos1"); // 1) 첫 번째로 찍힘
    Mono<String> m = Mono.just("Hello WebFlux").doOnNext(c -> log.info(c)).log();
    // 3) 마지막에 찍힌다.
    log.info("pos2"); // 2) 두 번째로 찍힘
    return m;

}
```
- 주석을 보면 순차적으로 로그가 찍히지 않는 것을 알 수 있다.
- 계속 강조하지만 Mono는 Publisher이다. Publisher는 동작의 흐름을 가지고 있을 뿐 Subscriber가 subscribe하지 않는한 해당 동작은 수행되지 않는다.
- 핸들러에서 Mono를 리턴하면 그 때 스프링이 subscribe해주기 때문에 Mono가 마지막에 찍히게 된다.


#### Mono는 언제 동작될까? V2
```java
@GetMapping("/v3")
public Mono<String> hello3() {
    log.info("pos1"); // 1)
    Mono<String> m = Mono.just(generateHello()).doOnNext(c -> log.info(c)).log();
    // 4)
    log.info("pos2"); // 3)
    return m;
}

private String generateHello(){
    log.info("method generateHello()"); // 2)
    return "Hello Mono";
}
```
- Mono의 just에 넣는 값을 로깅해보면 어떻게 될까?
- just에 넣는값을 이미 준비된 값이기 때문에 주석과 같은 순서로 출력이 찍히게 된다.
- subscribe시에 로깅을 하고 싶으면 어떻게 할까?

#### Mono는 언제 동작될까? V3
```java
@GetMapping("/v4")
public Mono<String> hello4() {
    log.info("pos1");
    Mono<String> m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
    // Supplier의 지연로딩을 활용하면 subscribe시 동작되게 할 수 있다.
    log.info("pos2");
    return m;
}
```
- 람다의 Supplier의 지연로딩을 활용하면 subscibre할 때 결과가 나타난다.

#### Mono를 직접 subscribe하기
```java
@GetMapping("/v5")
public Mono<String> hello5() {
    log.info("pos1");
    Mono<String> m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
    m.subscribe();

    // Publisher는 보통 핫타입 소스, 콜드타입 소스가 존재한다.

    // 핫타입 소스란 디비에서 읽어오거나, 미리 준비된 데이터를 보내주는게 아닌 실시간으로 일어나는 외부의 데이터들 (유저 인터페이스 등)을 핫타입 소스라고 한다.
    // 이는 새로운 subscribe가 일어나면 그 시점부터 실시간으로 발생하는 데이터를 가져오는 것이다.
    // 하지만 콜드소스타입은 subscribe하면 데이터를 처음부터 가져온다.
    log.info("pos2");
    return m;
}
```
- Mono의 subscibre를 직접 호출하면 어떻게 될까?
- 결과를 확인해보면 두번 동작이 이루어지는 것을 알 수 있다.
- 즉 Mono와 같은 Publisher는 여러개의 Subscriber를 가질 수 있는 것이다.

---

#### Cold And Hot
- 콜드라는 개념은 subscibre할 때 마다 매번 독립적으로 데이터를 생성된다. 즉 subscibre하지 않는다면 해당 데이터는 생성되지 않는다.
- 핫이라는 개념은 Subscriber가 있던없던 상관하지 않는다. 얘를 들어 마우 클릭과 같은 유저인터페이스 기능은 해당 동작이 수행될 때 발생하며 어떤 구독자가 들어왔을 때 해당동작을 수행하면 동일한 데이터를 전달받게 될 것이다.
