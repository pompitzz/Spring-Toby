package ch7;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class HelloSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        String mode = (String) importingClassMetadata
                .getAnnotationAttributes(EnableHello.class.getName()).get("mode");
        String name = mode.equals("mode1") ? HelloConfig1.class.getName() : HelloConfig2.class.getName();
        return new String[]{name};
    }
}
