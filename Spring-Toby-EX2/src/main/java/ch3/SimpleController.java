package ch3;

import java.util.Map;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
public interface SimpleController {
    void control(Map<String, String> params, Map<String, Object> model);
}

