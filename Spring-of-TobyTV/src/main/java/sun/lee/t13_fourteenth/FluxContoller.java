package sun.lee.t13_fourteenth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/07
 */
@Slf4j
@RestController
public class FluxContoller {

    @GetMapping("/event/{id}")
    Mono<Event> event(@PathVariable long id) {
        return Mono.just(new Event(id, "event" + id));
    }

    @GetMapping("/events")
    Flux<Event> events() {
        return Flux.just(new Event(1, "evnet1"), new Event(2, "event2"));
    }

    @GetMapping("/events2")
    Mono<List<Event>> events2() {
        // 이렇게 해도 결과가 같은데?
        // 컬렉션 자체에 대해 Mono 기능을 사용할 수 있겠지만 Event하나하나에 대해 Mono기능을 사용할 수 없다.
        // 그리고 아래와 같이 스트림으로 내보낼 수 없다.
        List<Event> list = Arrays.asList(new Event(1, "evnet1"), new Event(2, "event2"));
        return Mono.just(list);
    }

    // 데이터 타입을 STREAM으로 바꾸면 data{}, data{} 이러게 데이터 단위로 쪼개져서 들어온다.
    // 이는 시간을 단위로 데이터들이 순차적으로 들어올 때 유용하게 사용할 수 있다.
    @GetMapping(value = "/events3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events3() {
        List<Event> list = Arrays.asList(new Event(1, "evnet1"), new Event(2, "event2"));
        return Flux.fromIterable(list);
    }

    @GetMapping(value = "/events4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events4() {
        // 무한 스트림이 되기 때문에 갯수를 제한해줘야 한다.
        return Flux
                .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                .take(10); // 10번 이후 cancel된다.
    }

    // 이벤트 스트림을 이용하면 서버에서 받는 이벤트를 클라이언트에게 지속적으로 보내줄 수 있다.
    @GetMapping(value = "/events5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events5(int size) {
        return Flux
                .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                .delayElements(Duration.ofMillis(300))
                // 딜레이는 백그라운드 쓰레드가 10초동안 물고 있는다, 딜레이는 블럭킹이 되기 때문에 다른 쓰레드가 필요하다.
                .take(size); // size번 이후 cancel된다.
    }

    @GetMapping(value = "/events6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events6(int size) {
        return Flux
                // sink는 데이터를 계속해서 흘러 보내준다. Stream말고 이렇게 Event를 생성할 수 있다.
                .<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))
                .delayElements(Duration.ofMillis(300))
                .take(size);
    }

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

    @GetMapping(value = "/events8", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events8(int size) {
        Flux<String> fe = Flux.<String>generate(sink -> sink.next("Value"));
        Flux<Long> interval = Flux.interval(Duration.ofMillis(300));

        // 데이터를 묶어준다. 같이 묶으면 interval의 Duration을 같이 타기때문에 fe도 300ms로 딜레이가 걸릴것이다.
        return Flux.zip(fe, interval).map(tu -> new Event(tu.getT2(), tu.getT1() + tu.getT2()));
    }

    @GetMapping(value = "/events9", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events9(int size) {
        return Flux.zip
                (Flux.<String>generate(sink -> sink.next("Value")),
                        Flux.interval(Duration.ofMillis(300)))
                .map(tu -> new Event(tu.getT2(), tu.getT1() + tu.getT2()));
    }

}

@Data
@AllArgsConstructor
class Event {
    long id;
    String value;
}