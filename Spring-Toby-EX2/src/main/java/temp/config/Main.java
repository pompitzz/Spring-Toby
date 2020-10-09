package temp.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import temp.domain.TempService1;
import temp.domain.TempService2;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(TempAppConfig.class);
        TempService1 tempService1 = annotationConfigApplicationContext.getBean("tempService1", TempService1.class);
        TempService2 tempService2 = annotationConfigApplicationContext.getBean("tempService2", TempService2.class);

        tempService1.print();
        tempService2.print();
    }
}
