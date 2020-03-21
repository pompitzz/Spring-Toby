package ch3;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequiredParams {
    String[] value();
}
