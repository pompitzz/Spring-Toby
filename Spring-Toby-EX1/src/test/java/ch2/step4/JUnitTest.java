package ch2.step4;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class JUnitTest {
    private static Set<JUnitTest> testObjects = new HashSet<>();

    @Test
    void test1() throws Exception{
        assertThat(testObjects.contains(this)).isFalse();
        testObjects.add(this);
    }

    @Test
    void test2() throws Exception{
        assertThat(testObjects.contains(this)).isFalse();
        testObjects.add(this);
    }

    @Test
    void test3() throws Exception{
        assertThat(testObjects.contains(this)).isFalse();
        testObjects.add(this);
    }
}
