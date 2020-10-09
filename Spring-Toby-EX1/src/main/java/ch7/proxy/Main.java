package ch7.proxy;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        Hello hello = (Hello) Proxy.newProxyInstance(Main.class.getClassLoader(),
                new Class[]{Hello.class},
                new TestHandler(new HelloImpl())
        );
    }
}
