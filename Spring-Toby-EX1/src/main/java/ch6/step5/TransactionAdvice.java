package ch6.step5;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class TransactionAdvice implements MethodInterceptor {
    private PlatformTransactionManager transactionManager;

    public TransactionAdvice(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 콜백을 호출하여 타깃의 메서드를 실행한다.
            Object ret = invocation.proceed();
            transactionManager.commit(status);
            return ret;
        }
        // 다이내믹 프록시는 예외를 Method에서 따로 변환하여 던져주지만 MethodInvocation은 포장하지 않고 바로 던져주므로 그대로 전달이 가능하다.
        catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
