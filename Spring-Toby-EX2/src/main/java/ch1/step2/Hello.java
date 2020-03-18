package ch1.step2;

import ch1.step1.Printer;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */

@Setter
@Getter
public class Hello {
    private String name;

    @Resource(name = "printer")
    private Printer printer;

    public String sayHello() {
        return "Hello " + name;
    }

    public void print() {
        this.printer.print(sayHello());
    }
}
