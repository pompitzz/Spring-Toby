package sun.lee.t9_tenth;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
public class Ex3Completion {

    Ex3Completion next;

    public static Ex3Completion from(ListenableFuture<ResponseEntity<String>> lf) {
        Ex3Completion c = new Ex3Completion();
        lf.addCallback(s -> c.complete(s), e -> c.error(e));
        return c;
    }

    public void andAccept(Consumer<ResponseEntity<String>> con) {
        Ex3Completion c = new Ex3AcceptCompletion(con);
        this.next = c;
    }

    public Ex3Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        Ex3Completion c = new Ex3ApplyCompletion(func);
        this.next = c;
        return c;
    }

    public Ex3Completion andError(Consumer<Throwable> econ) {
        Ex3Completion c = new Ex3ErrorCompletion(econ);
        this.next = c;
        return c;
    }

    public void complete(ResponseEntity<String> s) {
        if (next != null) next.run(s);
    }

    public void run(ResponseEntity<String> value) {

    }

    public void error(Throwable e) {
        // error를 계속 다음으로 넘겨준다.
        // ErorrCompletion이 얘를 받을 때 적합한 처리가 필요하다.
        if (next != null) next.error(e);
    }
}

class Ex3AcceptCompletion extends Ex3Completion {
    Consumer<ResponseEntity<String>> con;

    public Ex3AcceptCompletion(Consumer<ResponseEntity<String>> con) {
        this.con = con;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        // AcceptCompletion이 생겼다는 것은 con이 있음을 확신할 수 있으므로 따로 검증이 필요없다.
        con.accept(value);
    }
}

class Ex3ApplyCompletion extends Ex3Completion {
    Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func;

    public Ex3ApplyCompletion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        this.func = func;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        ListenableFuture<ResponseEntity<String>> lf = func.apply(value);
        lf.addCallback(s -> complete(s), e -> error(e));
    }
}

class Ex3ErrorCompletion extends Ex3Completion {
    Consumer<Throwable> econ;

    public Ex3ErrorCompletion(Consumer<Throwable> econ) {
        this.econ = econ;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        // run이 호출된다면 에러가 발생하지 않은것이므로 다음얘한테 넘겨준다.
        if (next != null) next.run(value);
    }

    @Override
    public void error(Throwable e) {
        // error가 발생하면 여기서 끝낸다.
        econ.accept(e);
    }
}