package ch6.step2;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
class HelloTargetTest {
    @Test
    void simpleProxy() throws Exception {
        var hello = new HelloTarget();
        assertThat(hello.sayHello("Dexter")).isEqualTo("Hello Dexter");
        assertThat(hello.sayHi("Dexter")).isEqualTo("Hi Dexter");
    }

    @Test
    void invocation() throws Exception {
        var proxyHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(), // 클래스 로더를 제공한다.
                new Class[]{Hello.class}, // 다이내믹 프록시가 구현할 인터페이스를 제공하며 다수를 제공할 수 있다.
                new UpperCaseHandler(new HelloTarget())); // 부가기능과 위임 관련 코드를 담고 있는 InvocationHandler 구현 오브젝트를 제공한다.

        assertThat(proxyHello.sayHello("Dexter")).isEqualTo("HELLO DEXTER");
        assertThat(proxyHello.sayHi("Dexter")).isEqualTo("HI DEXTER");
        proxyHello.print("Dexter");
    }

    @Test
    void proxyFactoryBean() throws Exception {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxyHello = (Hello) pfBean.getObject();
        assertThat(proxyHello.sayHello("Dexter")).isEqualTo("HELLO DEXTER");
        assertThat(proxyHello.sayHi("Dexter")).isEqualTo("HI DEXTER");
        proxyHello.print("Dexter");
    }

    @Test
    void pointcutAdvisor() throws Exception {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxyHello = (Hello) pfBean.getObject();
        assertThat(proxyHello.sayHello("Dexter")).isEqualTo("HELLO DEXTER");
        assertThat(proxyHello.sayHi("Dexter")).isEqualTo("HI DEXTER");
        proxyHello.print("Dexter");
    }

    @Test
    void classNamePointcutAdvisor() throws Exception{

        NameMatchMethodPointcut classMethodPointCut = new NameMatchMethodPointcut(){
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> clazz.getSimpleName().startsWith("HelloT");
            }
        };
        classMethodPointCut.setMappedName("sayH*");

        checkAdvice(new HelloTarget(), classMethodPointCut, true);

        class HelloTest extends HelloTarget{};
        checkAdvice(new HelloTest(), classMethodPointCut, true);

        class HelloWorld extends HelloTarget{};
        checkAdvice(new HelloWorld(), classMethodPointCut, false);
        
    }
    
    private void checkAdvice(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxyHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxyHello.sayHello("Dexter")).isEqualTo("HELLO DEXTER");
            assertThat(proxyHello.sayHi("Dexter")).isEqualTo("HI DEXTER");
        } else {
            assertThat(proxyHello.sayHello("Dexter")).isEqualTo("Hello Dexter");
            assertThat(proxyHello.sayHi("Dexter")).isEqualTo("Hi Dexter");
        }
    }
    
    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            boolean notReturnString = invocation.getMethod().getReturnType() != String.class;
            if (notReturnString) return invocation.proceed();

            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }
}