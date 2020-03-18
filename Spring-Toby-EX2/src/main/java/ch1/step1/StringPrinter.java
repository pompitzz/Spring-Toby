package ch1.step1;

import ch1.step1.Printer;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/18
 */
public class StringPrinter implements Printer {
    private StringBuffer buffer = new StringBuffer();

    @Override
    public void print(String message) {
        buffer.append(message);
    }

    public String toString(){
        return buffer.toString();
    }
}
