package sun.lee.t4_fifth;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2MySub2 implements Subscriber<Integer> {
    Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("\n=============== Start One Req ================");
        System.out.println("onSubscribe");
        this.subscription = subscription;
        subscription.request(1L);
    }

    @Override
    public void onNext(Integer item) {
        System.out.println("onNext = " + item);
        this.subscription.request(1L); // 위에서 한번만 호출하고 요청이 처리되었을 때 또 다시 요청한다!

        /** 버퍼사이즈를 사용할 수도 있다.
         *  - 직접 이렇게 관리할 필요없고 스케줄러라는 유용한 기능을 사용하면 된다.
         if(--bufferSize <= 0){
         bufferSize = 2;
         this.subscription.request(2);
         }
         */
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("onError");
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
