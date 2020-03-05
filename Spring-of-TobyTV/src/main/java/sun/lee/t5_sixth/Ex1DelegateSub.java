package sun.lee.t5_sixth;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */

// 미리 뼈대를 정의해놓고 재사용할 기능만 따로 하위에서 정의하게 했다.
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
