package me.sun.tobyreview.non_spring.reactive.ch2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Ex2MySub2 implements Subscriber<Integer> {
    Subscription s;
    @Override
    public void onSubscribe(Subscription s) {
        this.s = s;
        System.out.println("========================= Start OnSubscribe =========================");
        s.request(1);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.print(integer);
        System.out.println(" request(2)");
        s.request(2);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
