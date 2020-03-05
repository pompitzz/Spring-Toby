package sun.lee.t4_fifth;

import java.util.Arrays;
import java.util.Iterator;

import static java.util.concurrent.Flow.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class Ex2Old_PubSub {
    public static void main(String[] args) {
        // Publisher가 Observer Pattern의 Observable과 동일하다.
        // Subscriber가 Observer Pattern의 Observer를 조금더 확장한 것과 동일하다.

        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);

        // publisher는 subscribe하나만 존재한다.
        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                // 옵저버에는 전달 인자가 존재하지 않지만 여기에는 존재한다.

                // Sub가 Pub에게 구독을 요청하면 해당 메서드(onSubscribe)가 호출되어서 Subscription이라는
                // Sub Pub를 중간을 이어주는 구독이라는 개념을 가진 중개자를 통해 Sub가 먼저 요청을 할 수 있게된다.
                // 흔히 백프레셔라고 부르며 Sub와 Pub사이의 속도차를 해결하기 위해 요청의 개수를 조정할 수 있는 request()가 있다.
                // The Reactive Streams Contract 그림을 잘 기억하고 있자.

                // subcribe는 한번에 여러개 동시에 동작할 수도 연달아 동작할 수도 있기 때문에 매번 Itertator를 만들어 줘야한다.
                // 디비에서 가져온 데이터라고 생각해보자.
                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Subscription() {
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
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
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
        };

        Subscriber<Integer> s2 = new Subscriber<Integer>() {
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
        };

        p.subscribe(s);
        p.subscribe(s2);
    }
}
