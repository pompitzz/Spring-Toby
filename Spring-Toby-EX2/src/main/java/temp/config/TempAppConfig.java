package temp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import temp.domain.TempProtoModel;

@Configuration
@ComponentScan(basePackageClasses = TempProtoModel.class)
public class TempAppConfig {

}
