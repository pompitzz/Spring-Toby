package ch6.step4;

import ch6.step3.TransactionHandler;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
@Setter
public class TxProxyFactoryBean implements FactoryBean<Object> {

    private Object target;
    private PlatformTransactionManager transactionManager;
    private Class<?> serviceInterface;

    public TxProxyFactoryBean(Object target, PlatformTransactionManager transactionManager, Class<?> serviceInterface) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler(target, transactionManager);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    // 싱글톤 빈이 아니라는 뜻이 아니고, getObject()가 매번 다른 오브젝트를 반환한다는 뜻이다.
    @Override
    public boolean isSingleton() {
        return false;
    }
}
