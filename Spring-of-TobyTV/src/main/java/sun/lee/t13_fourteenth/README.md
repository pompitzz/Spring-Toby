# \# 12. Reactive Streams (10) Flux
- Flux는 여러개의 개별타입들을 처리할 때 사용할 수 있는 Publisher이다.


#### Mono vs Flux
```java
@Data
@AllArgsConstructor
class Event {
    long id;
    String value;
}
```
- 우선 사용할 클래스를 하나 정의 한다.

```java
@GetMapping("/events")
Flux<Event> events() {
    return Flux.just(new Event(1, "evnet1"), new Event(2, "event2"));
}

@GetMapping("/events2")
Mono<List<Event>> events2() {
    List<Event> list = Arrays.asList(new Event(1, "evnet1"), new Event(2, "event2"));
    return Mono.just(list);
}
```
- Mono를 컬렉션으로 감싸면 Flux와 결과가 차이나지 않는다.
- 하지만 Mono에 컬레션을 감싸게되면 List<Event>에 대해서 Mono의 기능을 사용할 수 있지만 Event 개별적으로는 Mono의 기능을 사용할 수 없다.
- 반면에 Flux는 Flux<Event>와 같이 Event를 개별적으로 가지고 있기 때문에 Flux의 기능들을 Evnet에 적용시킬 수 있다.


#### STREAM 반환하기

```java
@GetMapping(value = "/events3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<Event> events3() {
    List<Event> list = Arrays.asList(new Event(1, "evnet1"), new Event(2, "event2"));
    return Flux.fromIterable(list);
}
```
- fromIterable로 컬렉션을 Flux로 만들 수도 있다.
- 데이터의 반환타입을 STREAM으로 지정해주면 JSON이 아닌 아래와 같은 형식으로 데이터가 반환된다.
- 이는 시간단위로 데이터들을 순차적으로 내보내야할 때 유용하게 사용할 수 있다.
```
data:{"id":13,"value":"value 13"}

data:{"id":14,"value":"value 14"}
```

#### STREAM 활용하기
```java
@GetMapping(value = "/events5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<Event> events5(int size) {
    return Flux
            .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
            .delayElements(Duration.ofMillis(300))
            // 딜레이는 백그라운드 쓰레드가 10초동안 물고 있는다, 딜레이는 블럭킹이 되기 때문에 다른 쓰레드가 필요하다.
            .take(size); // size번 이후 cancel된다.
}
```
- Stream.generate를 사용하면 무한 스트림을 만들 수 있고 그 스트림을 Flux로 만들 수 있다.
- 해당 결과는 size 수 많큼 300ms마다 결과가 반환된다.
- take의 size가 초과되면 cancel를 호출하여 subscibre를 완료시킨다.

<br>

```java
@GetMapping(value = "/events6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<Event> events6(int size) {
    return Flux
            // sink는 데이터를 계속해서 흘러 보내준다. Stream말고 이렇게 Event를 생성할 수 있다.
            .<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))
            .delayElements(Duration.ofMillis(300))
            .take(size);
}
```
- Flux의 generate를 사용해도 동일하게 만들 수 있다.


<br>

```java
@GetMapping(value = "/events7", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<Event> events7(int size) {
   return Flux
           // generate를 상태 값(1L)을 활용하여 계속해서 작업을 진행 시킬 수 있다.
           .<Event, Long>generate(() -> 1L,
                   (id, sink) -> {
                       sink.next(new Event(id, "value " + id));
                       return id + 1;
                   })
           .delayElements(Duration.ofMillis(300))
           .take(size);
}
```
- generate는 Callable을 활용하여 초기 상태값을 통해 계속해서 값을 생성할 수도 있다.
- 이를 통해 의미있는 데이터를 가공할수도 있다.

<br>

```java
@GetMapping(value = "/events8", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
Flux<Event> events8(int size) {
    Flux<String> fe = Flux.<String>generate(sink -> sink.next("Value"));
    Flux<Long> interval = Flux.interval(Duration.ofMillis(300));

    // 데이터를 묶어준다. 같이 묶으면 interval의 Duration을 같이 타기때문에 fe도 300ms로 딜레이가 걸릴것이다.
    return Flux.zip(fe, interval).map(tu -> new Event(tu.getT2(), tu.getT1() + tu.getT2()));
}
```
- zip이라는 여러개의 Flux들을 합칠 수 있는 기능도 제공된다.
- interval을 통해 300ms마다 Long을 생성하고 그 Long에 맞게 String Flux들을 합쳐 Event를 만들어 반환할 수도 있다.
- 이는 300ms마다 계속해서 Event를 만들어 반환할 것이다.
