package sun.lee.t9_tenth;

import lombok.NoArgsConstructor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
public class Ex4Completion<S, T> {
    // 이전의 작업을 넘겨받아서 수행하고, 다음 작업에게 넘겨주기 때문에 타입이 두개가 존재해야한다.
    // S는 넘어온 파라미터, T는 Result가 된다.

    Ex4Completion next;

    // from은 첫 시작이고 static에서 정의되기 때문에 Method Type을 지정해주어야 한다.
    public static <S, T> Ex4Completion<S, T> from(ListenableFuture<T> lf) {
        Ex4Completion<S, T> c = new Ex4Completion<>();
        lf.addCallback(s -> c.complete(s), e -> c.error(e));
        return c;
    }

    // 최종적으로 마무리하는 녀셕이니깐 T가 되어야 한다.
    public void andAccept(Consumer<T> con) {
        Ex4Completion<T, Void> c = new Ex4AcceptCompletion<>(con);
        this.next = c;
    }

    // andApply의 경우 andApply를 호출하는 녀석의 비동기 통신 결과를 현재 Completion이 알 수 없으므로 메서드 레벨의 타입으로 지정해준다.
    public <V> Ex4Completion<T, V> andApply(Function<T, ListenableFuture<V>> func) {
        Ex4Completion<T, V> c = new Ex4ApplyCompletion<>(func);
        this.next = c;
        return c;
    }

    // 정상적인 상태에서는 값을 그대로 넘기므로 같은 타입으로 지정한다.
    public Ex4Completion<T, T> andError(Consumer<Throwable> econ) {
        Ex4Completion<T, T> c = new Ex4ErrorCompletion<>(econ);
        this.next = c;
        return c;
    }

    // 스스로가 진행한 비동기 작업의 결과를 받아오니 T 타입니다.
    public void complete(T s) {
        if (next != null) next.run(s);
    }

    // run은 앞의 Completion에서 해당 결과를 수행하기 때문에 S여야 한다.
    public void run(S value) {

    }

    public void error(Throwable e) {
        // error를 계속 다음으로 넘겨준다.
        // ErorrCompletion이 얘를 받을 때 적합한 처리가 필요하다.
        if (next != null) next.error(e);
    }
}


class Ex4AcceptCompletion<S> extends Ex4Completion<S, Void> {
    Consumer<S> con;

    public Ex4AcceptCompletion(Consumer<S> con) {
        this.con = con;
    }

    @Override
    public void run(S value) {
        // AcceptCompletion이 생겼다는 것은 con이 있음을 확신할 수 있으므로 따로 검증이 필요없다.
        con.accept(value);
    }
}

class Ex4ApplyCompletion<S, T> extends Ex4Completion<S, T> {
    Function<S, ListenableFuture<T>> func;

    public Ex4ApplyCompletion(Function<S, ListenableFuture<T>> func) {
        this.func = func;
    }

    @Override
    public void run(S value) {
        ListenableFuture<T> lf = func.apply(value);
        lf.addCallback(s -> complete(s), e -> error(e));
    }
}

class Ex4ErrorCompletion<T> extends Ex4Completion<T, T> {
    Consumer<Throwable> econ;

    public Ex4ErrorCompletion(Consumer<Throwable> econ) {
        this.econ = econ;
    }

    @Override
    public void run(T value) {
        // run이 호출된다면 에러가 발생하지 않은것이므로 다음얘한테 넘겨준다.
        if (next != null) next.run(value);
    }

    @Override
    public void error(Throwable e) {
        // error가 발생하면 여기서 끝낸다.
        econ.accept(e);
    }
}
