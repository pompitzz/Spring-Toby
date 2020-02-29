package ch3.step15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class Calculator {
    private LineReadTemplate template;

    public Calculator() {
        template = new LineReadTemplate();
    }

    public int calcSum(String path) throws IOException {
        return template.lineReadTemplate(path, 0,
                ((line, value) -> value + Integer.parseInt(line)));
    }

    public int multiply(final String path) throws IOException {
        return template.lineReadTemplate(path, 1,
                ((line, value) -> value * Integer.parseInt(line)));
    }

}
