package ch3.step15;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
class StringReaderTest {
    StringReader stringReader = new StringReader();
    static final String PATH = "/Users/LeeHyeEun/Downloads/DongProject/Toby-Spring/Spring-Toby-EX1/src/test/java/ch3/step13/";

    @Test
    void justReadAsList() throws Exception{
        List<String> result = stringReader.readAsList(PATH + "numbers.txt");
        assertThat(result).containsOnly("1", "2", "3", "4");
    }

    @Test
    void plus10ReadAsList() throws Exception{
        List<String> result = stringReader.readAsListPlus10(PATH + "numbers.txt");
        assertThat(result).containsOnly("11", "12", "13", "14");
    }
}