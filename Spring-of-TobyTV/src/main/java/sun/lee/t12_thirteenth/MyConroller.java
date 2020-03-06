package sun.lee.t12_thirteenth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@RestController
@Slf4j
public class MyConroller {
    @GetMapping("/v1")
    public Mono<String> hello() {
        Mono<String> m = Mono.just("Hello WebFlux").log();
        return m;
        // 스프링이 알아서 onSubscribe -> request -> onNext -> onComplete를 동작시켜 준다.
        // Mono는 데이터가 하나이기 때문에 request는 하나이고 그 후에 onComplete가 호출된다.
        // Publisher -> (Pub) -> (Pub) -> Subscriber
    }

    @GetMapping("/v2")
    public Mono<String> hello2() {
        log.info("pos1"); // 1) 첫 번째로 찍힘
        Mono<String> m = Mono.just("Hello WebFlux").doOnNext(c -> log.info(c)).log(); // 3) 마지막에 찍힌다.
        log.info("pos2"); // 2) 두 번째로 찍힘
        return m;
        // 이 메서드가 끝나고 나서야 Mono들이 동작되는 것이다.
        // 사실상 이 코드는 동기적인 코드이다. 코드가 진행되는 흐름이 하나의 쓰레드에서 즉시 처리되기 때문이다.
        // Mono는 실행되지 않다가 Spring이 관리하는 Subscriber가 subscribe하면 이 Mono가 그 때 실행되는 것이다.
    }

    @GetMapping("/v3")
    public Mono<String> hello3() {
        log.info("pos1");
        Mono<String> m = Mono.just(generateHello()).doOnNext(c -> log.info(c)).log();
        // just는 미리 준비된 것이기 때문에 just안의 동작은 먼저 동작되고 그 결과가 Mono에 들어 간 후 subscribe될 때 그 다음 동작이 수행된다.
        // 그렇기 때문에 just에 넣는다고 해당 값이 비동기적으로 동작되진 않는다.
        log.info("pos2");
        return m;
    }

    @GetMapping("/v4")
    public Mono<String> hello4() {
        log.info("pos1");
        Mono<String> m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
        // Supplier의 지연로딩을 활용하면 subscribe시 동작되게 할 수 있다.
        log.info("pos2");
        return m;
    }

    @GetMapping("/v5")
    public Mono<String> hello5() {
        log.info("pos1");
        Mono<String> m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
        m.subscribe();
        // subscribe하면 mono 데이터가 다 동작되고 pos2가 출력된 후 Mono를 리턴할 때 또 똑같이 subscibre해서 두번 동작되게 된다.
        // 즉 Mono와 같은 Publisher는 Subscriber를 여러개 가질 수 있다.

        // Publisher는 보통 핫타입 소스, 콜드타입 소스가 존재한다.
        // 콜드타입 소스란 데이터가 만들어져서 고정이 되어있는(어느 Sub가 요청을 하던지 동일한 결과인 경우) 경우를 콜드타입 소스라고 한다.
        // 즉 콜드타입 퍼블리싱은 데이터를 처음부터 똑같이 흘러내보내 준다.

        // 핫타입 소스란 디비에서 읽어오거나, 미리 준비된 데이터를 보내주는게 아닌 실시간으로 일어나는 외부의 데이터들 (유저 인터페이스 등)을 핫타입 소스라고 한다.
        // 이는 새로운 subscribe가 일어나면 그 시점부터 실시간으로 발생하는 데이터를 가져오는 것이다.
        // 하지만 콜드소스타입은 subscribe하면 데이터를 처음부터 가져온다.
        log.info("pos2");
        return m;
    }

    private String generateHello(){
        log.info("method generateHello()");
        return "Hello Mono";
    }
}
