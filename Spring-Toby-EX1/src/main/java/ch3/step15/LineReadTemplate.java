package ch3.step15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class LineReadTemplate {
    public <T> T lineReadTemplate(final String path, final T initValue, final LineCallback<T> callback) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            T retValue = initValue;
            String line = null;
            while((line = reader.readLine()) != null){
                retValue = callback.doSomethingWithLine(line, retValue);
            }
            return retValue;
        }
    }
}
