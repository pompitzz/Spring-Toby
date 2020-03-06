package sun.lee.t4_fifth;


import org.reactivestreams.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2MySub1 implements Subscriber<Integer> {
    // 반드시 호출해야 한다.
    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("\n=============== Start Long.MAX_VALUE ================");
        System.out.println("onSubscribe");
        // Pub는 매우 빨라 백만개의 이벤트를 발생시키는데 Sub는 그를 해결할 능력이 없을 상황
        // 혹은 그 반대인 상황등에 대해 대처하기 위해 Subscription이 필요한 것이다.
        subscription.request(Long.MAX_VALUE);
    }

    // 옵저버의 update와 동일한 것.
    @Override
    public void onNext(Integer item) {
        System.out.println("onNext = " + item);
    }

    // 옵저버 패턴의 에러처리가 불가능한 단점을 극복한 것
    // 그러므로 try catch구문이 필요 없으며 에러가 발생하면 이 메서드로 넘어온다.
    // onError, onComplete는 해당 메서드가 호출되면 기능 종료될 것이다.
    @Override
    public void onError(Throwable throwable) {
        System.out.println("onError");
    }

    // 옵저버 패턴의 완료처리가 불가능한 단점을 극복한 것
    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
