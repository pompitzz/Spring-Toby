package ch6.step2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class UpperCaseHandler2 implements InvocationHandler {
    // 최종적으로는 타깃 오브젝트에게 위임해야 하므로 타깃 오브젝트를 주입받아준다.
    Object target;

    public UpperCaseHandler2(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = method.invoke(target, args);
        if (invoke instanceof String && method.getName().startsWith("say")){
            return ((String) invoke).toUpperCase();
        }
        return invoke;
    }
}
