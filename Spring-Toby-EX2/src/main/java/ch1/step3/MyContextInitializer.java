package ch1.step3;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/19
 */
public class MyContextInitializer implements ApplicationContextInitializer<AnnotationConfigWebApplicationContext> {
    @Override
    public void initialize(AnnotationConfigWebApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        Map<String, Object> m = new HashMap<>();
        m.put("db.username", "spring");

        environment.getPropertySources().addFirst(new MapPropertySource("myPs", m));
    }
}
