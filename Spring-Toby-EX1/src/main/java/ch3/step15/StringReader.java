package ch3.step15;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class StringReader {

    private LineReadTemplate template;

    public StringReader() {
        template = new LineReadTemplate();
    }

    public List<String> readAsList(String path) throws IOException {
        return template.lineReadTemplate(path, new ArrayList<String>(),
                ((line, value) -> {
                    value.add(line);
                    return value;
                })
        );
    }

    public List<String> readAsListPlus10(String path) throws IOException {
        return template.lineReadTemplate(path, new ArrayList<String>(),
                ((line, value) -> {
                    value.add("1" + line);
                    return value;
                }));
    }
}
