package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex4Callback {
    // 비동기 작업이 끝나면 해당 결과를 담을 수 있는 인터페이스
    interface SuccessCallback {
        void onSuccess(String result);
    }

    // 비동기 작업 후 예외가 발생할 때 사용하는 콜백
    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallBackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallBackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc); // Null이면 NPE를 해
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                // InterruptedException는 작업을 종료시키는게 아닌 메시지를 줘서 해당 결과를 처리해야 한다.
                // 그러므로 이 예외는 호출한 곳에 던지는게 아니고 현재 쓰레드의 인터럽트를 발생시킨다.
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                // 포장한게 아닌 원인을 던저야한다.(?)
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallBackFutureTask f = new CallBackFutureTask(
                () -> {
                    TimeUnit.SECONDS.sleep(2);
                    if(true) throw new RuntimeException("Async Error!!!");
                    log.info("Callable Async");
                    return "Hello";
                },
                s -> System.out.println("Result: " + s),
                e -> System.out.println("Error: " + e.getMessage()));

        // 비동기를 수행시키는 코드, 비즈니스 로직, 쓰레드 관리가 한 공간에 응집되어 있다.
        // 이를 깔끔하게 분리하고 추상화할 수 있어야 한다.
        // 이를 10년전 스프링은 어떻게 구현할 수 있는지 알아보자.
        es.execute(f);
        es.shutdown();
    }
}
