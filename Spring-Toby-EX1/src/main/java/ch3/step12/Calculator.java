package ch3.step12;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class Calculator {
    public int calcSum(String path) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            int sum = 0 ;
            String line = null;
            while((line = reader.readLine()) != null){
                sum += Integer.parseInt(line);
            }
            return sum;
        }
    }
}
