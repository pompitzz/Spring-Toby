package ch1.step1;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */

@Component
@Setter
@Getter
public class AnnotationHello {
    private String name;
    private Printer printer;

    public String sayHello() {
        return "Hello " + name;
    }

    public void print(){
        this.printer.print(sayHello());
    }
}
