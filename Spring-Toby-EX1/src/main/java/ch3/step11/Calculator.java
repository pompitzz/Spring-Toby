package ch3.step11;

import java.io.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class Calculator {
    public int calcSum(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        int sum = 0 ;
        String line = null;
        while((line = reader.readLine()) != null){
            sum += Integer.parseInt(line);
        }

        reader.close();
        return sum;
    }
}
