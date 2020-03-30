package me.sun.tobyreview.non_spring.reactive.ch3;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.RequestMapping;

public class RestController {

    @RequestMapping("/hello")
    public Publisher<String> hello(String name){
        return s -> s.onSubscribe(
                new Subscription() {
                    @Override
                    public void request(long n) {
                        s.onNext("Hello" + name);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                }
        );
    }

}
