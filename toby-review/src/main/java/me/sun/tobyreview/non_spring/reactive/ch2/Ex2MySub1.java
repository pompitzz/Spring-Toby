package me.sun.tobyreview.non_spring.reactive.ch2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Ex2MySub1 implements Subscriber<Integer> {
    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("====================== Start OnSubscribe ======================");
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.println(integer + " ");
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
        System.out.println("====================== End OnComplete ======================");
    }
}
