package ch1.step1;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */

@Setter
@Getter
public class Hello {
    private String name;
    private Printer printer;

    public String sayHello() {
        return "Hello " + name;
    }

    public void print(){
        this.printer.print(sayHello());
    }
}
