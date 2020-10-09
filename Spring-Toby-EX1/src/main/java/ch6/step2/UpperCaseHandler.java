package ch6.step2;

import ch6.step3.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class UpperCaseHandler implements InvocationHandler {
    // 최종적으로는 타깃 오브젝트에게 위임해야 하므로 타깃 오브젝트를 주입받아준다.
    Object target;

    public UpperCaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.invoke(proxy, args);
        String name = method.getName();
        Method[] methods = target.getClass().getMethods();
        for (Method method1 : methods) {
            System.out.println("method1.getName() = " + method1.getName());
        }
        Class[] argsClasses = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        Method targetMethod = target.getClass().getMethod(method.getName(), argsClasses);

        boolean hasTransactionalAnno = targetMethod.getAnnotation(Transactional.class) != null;
        if (hasTransactionalAnno){
            System.out.println(">>>> "+ method.getName() + " <<<< Has Transactional Annotation");
        }

        Object invoke = method.invoke(target, args);

        if (invoke instanceof String){
            return ((String) invoke).toUpperCase();
        }
        System.out.println("\n\n");
        return invoke;
    }
}
