package ch1.step1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
class HelloTest {
    @Test
    void init() throws Exception {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerSingleton("hello1", Hello.class);
        Hello hello1 = context.getBean("hello1", Hello.class);
        assertThat(hello1).isNotNull();
    }

    @Test
    void bd() throws Exception {
        StaticApplicationContext ac = new StaticApplicationContext();

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");

        ac.registerBeanDefinition("hello2", helloDef);
        Hello hello2 = ac.getBean("hello2", Hello.class);

        assertThat(hello2.getName()).isEqualTo("Spring");
    }

    @Test
    void registerBeanWithDependency() throws Exception {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        RootBeanDefinition helloDef = new RootBeanDefinition(Hello.class);

        // helloDef에 DI 작업을 수행해준다.
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef
                .getPropertyValues()
                .addPropertyValue("printer", new RuntimeBeanReference("printer"));

        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        // printer 빈은 hello.print()에 의해 StringBuffer에 값이 저장되었다.
        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }

    @Test
    void xml() throws Exception {
        GenericApplicationContext ac = new GenericApplicationContext();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
        reader.loadBeanDefinitions("applicationContext.xml");

        // 모든 메터 정보 등록 후 애플리케이션 초기화
        ac.refresh();

        ac.getBean("hello", Hello.class).print();
        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }

    @Test
    void GXAC() throws Exception {
        GenericXmlApplicationContext ac = new GenericXmlApplicationContext("applicationContext.xml");
        ac.getBean("hello", Hello.class).print();
        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }

    @Test
    void layerContext() throws Exception {
        GenericApplicationContext parent = new GenericXmlApplicationContext("parentContext.xml");
        GenericApplicationContext child = new GenericApplicationContext(parent);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions("childContext.xml");

        child.refresh();

        child.getBean("hello", Hello.class).print();
        assertThat(child.getBean("printer", Printer.class).toString()).isEqualTo("Hello Child");
    }
}
