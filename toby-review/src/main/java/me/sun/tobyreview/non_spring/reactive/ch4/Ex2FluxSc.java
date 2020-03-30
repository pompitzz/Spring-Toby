package me.sun.tobyreview.non_spring.reactive.ch4;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Ex2FluxSc{
    public static void main(String[] args) throws InterruptedException {

        Flux
                .interval(Duration.ofMillis(200))
                .take(10)
                .subscribe(s -> log.debug("onNext:{}", s));
        TimeUnit.SECONDS.sleep(5);
    }
}
