package sun.lee.t7_eighth;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
@Slf4j
public class Ex3Callback {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        // FutureTask를 만드록 execute에 넘겨서 사용할 수도 있다.
        // 이를 바로 구현해서 done()이라는 후크를 통해 구현할수도있다.
        FutureTask<String> f = new FutureTask<String>(() ->
        {
            TimeUnit.SECONDS.sleep(2);
            log.info("Callable Async");
            return "Hello";
        }) {
            @Override
            protected void done() {
                try {
                    System.out.println(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        es.execute(f);
        es.shutdown();
    }
}
