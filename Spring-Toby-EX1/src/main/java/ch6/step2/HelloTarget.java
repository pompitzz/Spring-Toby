package ch6.step2;

import ch6.step3.Transactional;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
interface Hello {
    String sayHello(String name);
    String sayHi(String name);
    void print(String name);
}

public class HelloTarget implements Hello{

    @Override
    @Transactional
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

    @Override
    public void print(String name) {
        System.out.println("Hello " + name);
    }
}

