# \# 12. Reactive Streams (8) WebFlux
- AsyncRestTemplate의 결과인 ListenableFuture를 자바 표준인 CompotableFuture로 바꾸어 줬었다.
- AsyncRestTemplate는 기능이 동작하지만 하지만 스프링 5.0의 리액티브 스타일의 코드가 아니다.
- ListenabelFuture를 리턴하여 callback의 스타일로 구현하여야 하였다.
- 스프링 5.0부터 리액티브 프로그래밍을 지원해주는 WebClinet가 나왔다.

### Mono
- 이전에 비동기작업을 처리할 때에는 Callable, DeferredResult와 같은 타입으로 반환하였지만 리액티브 프로그래밍에서는 Mono, Flux로 반환한다.
- Mono는 하나의 값을 가질 때 사용할 때 값을 감쌀 수 있는 일종의 컨테이너이다.
- Mono는 CorePublisher를 구현하고 있으므로 일종의 Publisher이다.
- 개발자는 Publisher인 Mono만 잘 활용하면 Subscriber의 역할을 스프링에서 알아서 처리해준다.


### [WebFlux](Ex1WebFluxController.java)
- 지난번에 테스트해봤던 외부 API와의 통신 및 내부 비동기 작업을 WebFlux의 리액티브 스트리밍 스타일로 구현해보자.

#### 외부 API와 한번 통신하기
```java
WebClient client = WebClient.create();

@GetMapping("/webflux2") // 외부 API 한번 연결하기
public Mono<String> webflux2(int idx) {
    // AsyncRestTemplate, DeferredResult로 비동기 방식으로 구현한거 처럼 비동기로 동작된다.
    Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange();
    return res.flatMap(c -> c.bodyToMono(String.class));
}
```
- AsyncRestTemplate, DeferredResult로 비동기 방식으로 구현한거 처럼 webCient를 이용해 Mono타입으로 반환하여 비동기 작업을 수행할 수 있다.
- clinet는 AsyncRestTemplate보다 더 명시적으로 요청을 수행할 수 있고 요청 결과를 Mono로 감싼 ClientResponse로 반환된다.
- 이는 flatMap에서 bodyToMono를 통해 스트링으로 변환 시킬 수 있다.
- flatMap을 쓰는 이유는 map일 경우 Mono<ClientResponse> -> Mono<매핑되는 타입>이 되는데 매핑되는 타입이 bodyToMono로 Mono<String>이 되므로 Mono<Mono<String>>이 되기 때문이다.



#### 외부 API와 연달아 두번 통신하기
```java
@GetMapping("/webflux3") // 외부 API 두번 연결하기
public Mono<String> webflux3(int idx) {
    // AsyncRestTemplate와 동일하게 4초대에 동작된다.
    return client.get().uri(URL1, idx).exchange()
                 .flatMap(c -> c.bodyToMono(String.class))
                 .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                 .flatMap(c -> c.bodyToMono(String.class));
}
```
- 리액티브 프로그래밍은 함수형 프로그래밍과 같이 파이프라이닝을 이용해 한번에 작업을 수행하게 도와준다.
- 처음에 URL1에 요청을 보낸 후 String으로 flatMap하고 flatMap된 스트링을 또 URL2에 넘겨 새로운 요청을 보냈다.
- 이를 테스트 해보면 이전부터 계속 테스트 했던 것과 동일하게 4초대에 결과가 완료되는것을 알 수 있다.

#### 외부 API와 두번 통신 및 내부 비동기 작업
```java
@GetMapping("/webflux4") // 외부 API 두번 연결하기, 내부 비동기 작업
public Mono<String> webflux4(int idx) {
    return client.get().uri(URL1, idx).exchange()
                 .flatMap(c -> c.bodyToMono(String.class))
                 .doOnNext(c -> log.info("First : {}", c.toString()))
                 .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                 .flatMap(c -> c.bodyToMono(String.class))
                 .doOnNext(c -> log.info("Second : {}", c.toString()))
                 .flatMap(res2 -> Mono.fromCompletionStage(myTempService.work(res2)))
            ;
}

@Service
public static class MyTempService{
    @Async
    public CompletableFuture<String> work(String req) {
        log.info("work : {}", req);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(req + "/asyncwork");
    }
}

@Bean
public ThreadPoolTaskExecutor myThreadPool() {
    ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
    te.setCorePoolSize(10);
    te.setMaxPoolSize(10);
    te.initialize();
    return te;
}
```
- 외부 API와 두번의 통신 후 내부에 비동기 작업을 수행하게 동작시켜보았다.
- 이번에는 지난번에 했던 테스트와 조금 다르게 내부 비동기 작업에 1초간의 지연을 추가로 주었다.
- doOnNext로 로깅한 내역들을 보면 외부 API요청은 reactor-http-nio에서 일어나는것을 알 수 있다.
- 그리고 내부 비동기 요청은 따로 설정했던 myThreadPool에서 동작된다.
- myThreadPool의 개수는 10개이기 때문에 모든 요청은 4초에서 10초가 더해진 14초가 된다.

#### 외부 API와 두번 통신 및 내부 동기 작업
```java
@GetMapping("/webflux5") // API 두번 연결하기, 내부 동기 작업
public Mono<String> webflux5(int idx) {
    return client.get().uri(URL1, idx).exchange()
                 .flatMap(c -> c.bodyToMono(String.class))
                 .doOnNext(c -> log.info("First : {}", c.toString()))
                 .flatMap(res1 -> client.get().uri(URL2, res1).exchange())
                 .flatMap(c -> c.bodyToMono(String.class))
                 .doOnNext(c -> log.info("Second : {}", c.toString()))
                 .map(res2 -> myTempService.work2(res2));
}

@Service
public static class MyTempService{
    public String work2(String req) {
        log.info("work2 : {}", req);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return req + "/asyncwork";
    }
}
```
- 이번엔 내부 작업을 동기로 실행시켜 보았다.
- 동기로 실행되기 때문에 로그를 살펴보면 외부 API요청의 쓰레드와 동일한 reactor-http-nio를 사용한다.
- reactor-http-nio Thread는 기본값이 4개로 설정되는거 같다.
- 4개의 쓰레드로 내부 동기 작업을 기다리고, 외부 비동기 API들도 모두 처리해야한다.
- 내부 동기 작업을 기다리는 동안 200개나 되는 외부 비동기 처리들도 지연이 되기 때문에 40 ~ 60초대정도로 심각한 지연이 발생한다.
