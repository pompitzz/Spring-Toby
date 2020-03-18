
package ch1.step1;
/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
public class ConsolePrinter implements Printer {
    @Override
    public void print(String message) {
        System.out.println(message);
    }
}
