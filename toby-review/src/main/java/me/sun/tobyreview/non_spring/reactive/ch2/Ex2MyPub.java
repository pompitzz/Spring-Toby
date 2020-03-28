package me.sun.tobyreview.non_spring.reactive.ch2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;

public class Ex2MyPub implements Publisher<Integer> {
    private Iterable<Integer> iterable;

    public Ex2MyPub(Iterable<Integer> iterable) {
        this.iterable = iterable;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> s) {
        Iterator<Integer> iter = iterable.iterator();

        s.onSubscribe(
                new Subscription() {
                    @Override
                    public void request(long n) {
                        System.out.println("requested " + n + " Size" + " CurrentThread :: " + Thread.currentThread().getName());
                        try {
                            while (n-- > 0) {
                                if (iter.hasNext()) {
                                    s.onNext(iter.next());
                                } else {
                                    s.onComplete();
                                    break;
                                }
                            }
                        } catch (RuntimeException e) {
                            s.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
    }
}
