package ch7.proxy;

public class HelloImpl implements Hello {
    @Override
    public void hello() {
        System.out.println("helloImpl");
    }
}
