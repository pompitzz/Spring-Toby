package ch3.step13;

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
        return fileReadTemplate(path,
                reader -> {
                    int sum = 0;
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sum += Integer.parseInt(line);
                    }
                    return sum;
                });
    }

    public int multiply(final String path) throws IOException {
        return fileReadTemplate(path,
                reader -> {
                    int multiply = 1;
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        multiply *= Integer.parseInt(line);
                    }
                    return multiply;
                });
    }

    private int fileReadTemplate(final String path, final BufferReaderCallback callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return callback.doSomeThingWithBufferReader(reader);
        }
    }
}
