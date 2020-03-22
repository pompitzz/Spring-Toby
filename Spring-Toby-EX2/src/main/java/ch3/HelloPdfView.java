package ch3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
public class HelloPdfView {
    public static void main(String[] args) throws InterruptedException {
        while(true){
            List<Integer> lotto = new Random()
                                        .ints(1, 46)
                                        .distinct()
                                        .limit(6)
                                        .sorted()
                                        .boxed()
                                        .collect(Collectors.toList());

            System.out.println("Arrays.toString(lottos) = " + lotto);
            Thread.sleep(500);
        }
    }
}
