package sun.lee.t9_tenth;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
public class Ex2Completion {

    Ex2Completion next;

    public static Ex2Completion from(ListenableFuture<ResponseEntity<String>> lf) {
        Ex2Completion c = new Ex2Completion();
        lf.addCallback(s -> c.complete(s), e -> c.error(e));
        return c;
    }

    public void andAccept(Consumer<ResponseEntity<String>> con) {
        Ex2Completion c = new Ex2AcceptCompletion(con);
        this.next = c;
    }

    public Ex2Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        Ex2Completion c = new Ex2ApplyCompletion(func);
        this.next = c;
        return c;
    }

    public void complete(ResponseEntity<String> s) {
        if (next != null) next.run(s);
    }

    public void run(ResponseEntity<String> value) {

    }

    public void error(Throwable e) {

    }

}

class Ex2AcceptCompletion extends Ex2Completion {
    Consumer<ResponseEntity<String>> con;

    public Ex2AcceptCompletion(Consumer<ResponseEntity<String>> con) {
        this.con = con;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        // AcceptCompletion이 생겼다는 것은 con이 있음을 확신할 수 있으므로 따로 검증이 필요없다.
        con.accept(value);
    }
}

class Ex2ApplyCompletion extends Ex2Completion {
    Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func;

    public Ex2ApplyCompletion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> func) {
        this.func = func;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        ListenableFuture<ResponseEntity<String>> lf = func.apply(value);
        lf.addCallback(s -> complete(s), e -> error(e));
    }
}
