package sun.lee.t9_tenth;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
public class Ex1Completion {

    Ex1Completion next;
    Consumer<ResponseEntity<String>> con;

    public Ex1Completion(Consumer<ResponseEntity<String>> con) {
        this.con = con;
    }

    Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func;

    public Ex1Completion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        this.func = func;
    }
    /* 첫번째 비동기 작업은 의존하는 작업이 없지만 두번째, 세번째는 앞의 작업에 의존적인 작업이다. */

    // ListenableFuture를 받는 from을 하나 정의하고 Completion을 반환한다.
    // Completion안에는 콜백이 실행된 결과를 담을 수 있는 메서드를 정의하자.
    // 즉 Completion은 콜백의 결과를 가지고 있는 클래스이다.
    public static Ex1Completion from(ListenableFuture<ResponseEntity<String>> lf) {
        Ex1Completion c = new Ex1Completion();
        lf.addCallback(s -> c.complete(s), e -> c.error(e));
        return c;
    }

    // 비동기 작업으로 리턴된 결과를 처리하는 메서드를 정의하자
    public void andAccept(Consumer<ResponseEntity<String>> con) {
        // 내가만든 Completion이랑 from의 Comletion이랑 연결을 시켜주어야 한다.
        Ex1Completion c = new Ex1Completion(con);
        this.next = c;
    }

    // andApply는 리턴값이 필요하므로 Function으로 만들자
    public Ex1Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        Ex1Completion c = new Ex1Completion(func);
        this.next = c; // 그 전의 Completion의 next로 지정해준다.
        return c;
    }

    private void complete(ResponseEntity<String> s) {
        // next가 설정되어 있다면 run을 설정한다.
        // next는 두번째 Completion이며 그 next한테 첫번째 작업의 대한 결과값 s를 넘겨준다.
        // 즉 run은 andAccept에서 만들어진 Completion에서 run이 실행된다.
        if (next != null) next.run(s);
    }

    private void run(ResponseEntity<String> res) {
        // 일일히 체크하는건 리팩토링의 대상이다.
        if (con != null) {
            con.accept(res);
        } else if (func != null) {
            // 다음 비동기 작업의 인풋은 전 단계의 아웃풋이된다.
            ListenableFuture<ResponseEntity<String>> lf = func.apply(res);
            lf.addCallback(s -> complete(s), e -> error(e));
        }
    }

    private void error(Throwable e) {

    }
}