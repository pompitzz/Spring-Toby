package sun.lee;

import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/07
 */
public class Parent {
    private String name;

    public Parent(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        List<Parent> parents = Arrays.asList(new Parent("1"), new Parent("2"), new Parent("1"));
        List<Child> children = Arrays.asList(
                new Child("1", 10),
                new Child("2", 20),
                new Child("3", 30)
        );

        test(children);
        test(parents);

    }

    static void test(List<? extends Parent> list){
        for (Parent parent : list) {
            System.out.println(parent);
        }
    }

    static void test2(List<? super Parent> list){
        for (Object sou : list) {
            System.out.println(sou);
        }
    }
}


class Child extends Parent{
    private int age;

    public Child(String name, int age) {
        super(name);
        this.age = age;
    }
}
