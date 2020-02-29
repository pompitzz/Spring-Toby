package ch3.step14;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
class CalculatorTest {
    Calculator calculator = new Calculator();
    static final String PATH = "/Users/LeeHyeEun/Downloads/DongProject/Toby-Spring/Spring-Toby-EX1/src/test/java/ch3/step13/";

    @Test
    void sumOfNumbers() throws Exception {
        int sum = calculator.calcSum(PATH + "numbers.txt");
        assertThat(sum).isEqualTo(10);
    }

    @Test
    void multiplyOfNumbers() throws Exception {
        int multiply = calculator.multiply(PATH + "numbers.txt");
        assertThat(multiply).isEqualTo(24);
    }
}