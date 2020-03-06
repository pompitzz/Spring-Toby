package sun.lee.t10_eleventh;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/06
 */
public class Ex1CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * - Future는 get을 통해 결과를 가져올 수 있다.
         * - CompletableFuture은 비동기 작업을 간단하게 완료하게 만들 수 있다.
         * - ListenerFuture은 꽤 복잡하게 비동기 작업을 처리해야했지만 CompletableFuture는 매우 간단하게 처리가 가능하다.
         * - CompletableFuture는 리스트의 모든 값이 완료될 때 까지 기다릴지 하나의 값만 완료될 때 기다릴지 선택할 수도 있으며
         * - 람다표현식과 파이프라이닝으로 구조적으로도 이쁘게 동작시킬 수 있다.
         * - 쓰레드를 생성하고 submit하고 돌리지않아도 비동기 코드를 만들 수 있다.
         */

        // 이미 작업이완료된 Future이다.
        CompletableFuture<Integer> ff = CompletableFuture.completedFuture(1);

        // 작업이 완료되지 않음, 비동기 작업의 결과를 담고 있는 인터페이스이다.
        CompletableFuture<Integer> f = new CompletableFuture<>();

        // 작업이 완료됨, complete를 통해 결과를 넣어주면 끝난다
        f.complete(2);
        // get을 할때 그 값이 적용된다.
        System.out.println(f.get());


        CompletableFuture<Object> f2 = new CompletableFuture<>();

        // 작업이 완료된것일 뿐 예뢰를 발생시키지 않음
        f2.completeExceptionally(new RuntimeException());
        f2.get();  // 이때 예외가 발생한다.
    }
}
