package sun.lee.t5_sixth;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
@RestController
public class Ex4Controller {
    @RequestMapping("/hello")
    public Publisher<String> hello(String name) {
        // 스프링에서는 Pub만 만들면 되고 Subscriber는 스프링이 적절한 시점에 만들어준다.
        return s ->
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        s.onNext("Hello " + name);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {
                    }
                });
    }
}
