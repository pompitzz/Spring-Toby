package ch3.step13;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public interface BufferReaderCallback {
    int doSomeThingWithBufferReader(final BufferedReader reader) throws IOException;
}
