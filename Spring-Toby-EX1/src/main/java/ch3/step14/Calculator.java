package ch3.step14;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class Calculator {
    public int calcSum(String path) throws IOException {
        return lineReadTemplate(path, 0,
                ((line, value) -> value + Integer.parseInt(line)));
    }

    public int multiply(final String path) throws IOException {
        return lineReadTemplate(path, 1,
                ((line, value) -> value * Integer.parseInt(line)));
    }

    private int lineReadTemplate(final String path, int initValue, final LineCallback callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            int retValue = initValue;
            String line = null;
            while((line = reader.readLine()) != null){
                retValue = callback.doSomethingWithLine(line, retValue);
            }
            return retValue;
        }
    }
}
