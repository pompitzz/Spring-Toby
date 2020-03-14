package ch6.step3;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class TransactionHandler implements InvocationHandler {
    private Object target;
    private PlatformTransactionManager transactionManager;
    private List<String> objectMethodNames;

    public TransactionHandler(Object target, PlatformTransactionManager transactionManager) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.objectMethodNames = Arrays.stream(Object.class.getMethods()).map(Method::getName).collect(Collectors.toList());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (objectMethodNames.stream().anyMatch(methodName -> methodName.equals(name))) {
            return method.invoke(target, args);
        }

        Method targetMethod;
        // 아규먼트가 없다면 바로 targetMethod를 가져온다.
        if (args == null) {
            targetMethod = target.getClass().getMethod(name);
        }
        // 아규먼트가 있다면 해당 타입을 통해 targetMehod를 가져온다.
        else {
            Class[] argsClasses = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            targetMethod = target.getClass().getMethod(name, argsClasses);
        }

        // targetMehod의 Annotation에 Transactional.class가 존재하는지 확인한다.
        boolean hasTransactional = targetMethod.getAnnotation(Transactional.class) != null;

        // Transactional Annotation이 존재한다면 트랜잭션을 수행한다.
        if (hasTransactional) {
            return invokeInTransaction(method, args);
        }

        return method.invoke(target, args);
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object invoke = method.invoke(target, args);
            this.transactionManager.commit(status);
            return invoke;
        } catch (InvocationTargetException e) {
            this.transactionManager.rollback(status);
            throw e.getTargetException();
        }
    }
}
