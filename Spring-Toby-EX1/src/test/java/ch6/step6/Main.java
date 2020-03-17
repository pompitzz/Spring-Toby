package ch6.step6;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
public class Main {

    public static void main(String[] args) {
        List<String> partis = Arrays.asList("제임", "가수니", "망", "붕", "배배", "니니", "베나지대", "주뱌", "으즙야");

        new Random()
                .ints(0, partis.size())
                .distinct()
                .limit(100)
                .forEach(System.out::println);
    }

}
