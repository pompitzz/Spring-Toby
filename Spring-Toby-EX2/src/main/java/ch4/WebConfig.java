package ch4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addFormatters(FormatterRegistry registry) {
    }
}
