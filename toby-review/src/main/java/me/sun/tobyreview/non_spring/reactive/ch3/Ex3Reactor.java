package me.sun.tobyreview.non_spring.reactive.ch3;

import reactor.core.publisher.Flux;

public class Ex3Reactor {
    public static void main(String[] args) {
        Flux.<Integer>create(
                e -> {
                    e.next(1);
                    e.next(2);
                    e.next(3);
                    e.next(4);
                    e.complete();
                })
                .log()
                .reduce((a, b) -> a * b)
                .log()
                .subscribe();
    }
}
