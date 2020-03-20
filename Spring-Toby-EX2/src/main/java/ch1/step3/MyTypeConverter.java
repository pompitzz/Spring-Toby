package ch1.step3;

import org.springframework.core.convert.converter.Converter;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
public class MyTypeConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        return Integer.parseInt(source);
    }
}
