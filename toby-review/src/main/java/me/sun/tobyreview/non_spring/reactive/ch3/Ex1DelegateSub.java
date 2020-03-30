package me.sun.tobyreview.non_spring.reactive.ch3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class Ex1DelegateSub<T, R> implements Subscriber<T> {

    Subscriber<? super R> sub;

    public Ex1DelegateSub(Subscriber<? super R> sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Subscription s) {
        log.debug("onSubscribe");
        sub.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        log.debug("onNext:{}", t);
    }

    @Override
    public void onError(Throwable t) {
        sub.onError(t);
    }

    @Override
    public void onComplete() {
        log.debug("onComplete");
        sub.onComplete();
    }

    // pub -- map -- sub
    //
}
