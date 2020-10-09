package ch7.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TestHandler implements InvocationHandler {
    private Hello hello;

    public TestHandler(Hello hello) {
        this.hello = hello;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoke");
        if (method.getName().equals("hi")) {
            System.out.println("hi");
            return null;
        }
        return method.invoke(hello, args);
    }
}
