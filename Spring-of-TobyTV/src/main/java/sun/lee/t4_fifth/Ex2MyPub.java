package sun.lee.t4_fifth;

import java.util.Iterator;
import java.util.concurrent.Flow;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2MyPub implements Flow.Publisher<Integer> {

    Iterable<Integer> iterable;

    public Ex2MyPub(Iterable<Integer> iterable) {
        this.iterable = iterable;
    }

    @Override
    public void subscribe(Flow.Subscriber subscriber) {

        // 옵저버에는 전달 인자가 존재하지 않지만 여기에는 존재한다.

        // Sub가 Pub에게 구독을 요청하면 해당 메서드(onSubscribe)가 호출되어서 Subscription이라는
        // Sub Pub를 중간을 이어주는 구독이라는 개념을 가진 중개자를 통해 Sub가 먼저 요청을 할 수 있게된다.
        // 흔히 백프레셔라고 부르며 Sub와 Pub사이의 속도차를 해결하기 위해 요청의 개수를 조정할 수 있는 request()가 있다.
        // The Reactive Streams Contract 그림을 잘 기억하고 있자.

        // subcribe는 한번에 여러개 동시에 동작할 수도 연달아 동작할 수도 있기 때문에 매번 Itertator를 만들어 줘야한다.
        // 디비에서 가져온 데이터라고 생각해보자.
        Iterator<Integer> it = iterable.iterator();

        subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                try {
                    while (n-- > 0) {
                        if (it.hasNext()) {
                            subscriber.onNext(it.next());
                        } else {
                            subscriber.onComplete();
                            break;
                        }
                    }
                }catch (RuntimeException e){
                    subscriber.onError(e);
                }
            }

            @Override
            public void cancel() {

            }
        });
    }
}
