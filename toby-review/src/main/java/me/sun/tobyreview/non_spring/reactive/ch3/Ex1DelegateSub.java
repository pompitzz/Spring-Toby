package me.sun.tobyreview.non_spring.reactive.ch3;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Ex1DelegateSub<T, R> implements Subscriber<T> {

    Subscriber<? super R> sub;

    public Ex1DelegateSub(Subscriber<? super R> sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Subscription s) {
        sub.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {
        sub.onError(t);
    }

    @Override
    public void onComplete() {
        sub.onComplete();
    }
}
