package ch3.step11;

import org.assertj.core.api.FileAssert;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
class CalculatorTest {
    @Test
    void sumOfNumbers() throws Exception{
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum("/Users/LeeHyeEun/Downloads/DongProject/Toby-Spring/Spring-Toby-EX1/src/test/java/ch3/step11/numbers.txt");
        assertThat(sum).isEqualTo(10);
    }
}