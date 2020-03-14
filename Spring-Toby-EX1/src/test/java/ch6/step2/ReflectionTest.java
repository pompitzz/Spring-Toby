package ch6.step2;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class ReflectionTest {
    @Test
    void invokeMethod() throws Exception{
        String name = "Toby's Spring";

        assertThat(name.length()).isEqualTo(13);

        Method length = String.class.getMethod("length");
        assertThat((Integer)length.invoke(name)).isEqualTo(13);

        assertThat(name.charAt(0)).isEqualTo('T');

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat(charAtMethod.invoke(name, 0)).isEqualTo('T');
    }
}
