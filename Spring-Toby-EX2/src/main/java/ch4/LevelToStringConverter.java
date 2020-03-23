package ch4;

import org.springframework.core.convert.converter.Converter;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/23
 */
public class LevelToStringConverter implements Converter<Level, String> {
    @Override
    public String convert(Level source) {
        return String.valueOf(source.intValue());
    }
}

class StringToLevelConverter implements Converter<String, Level> {
    @Override
    public Level convert(String source) {
        return Level.valueOf(Integer.parseInt(source));
    }
}
