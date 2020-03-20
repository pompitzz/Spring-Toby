package ch3;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/20
 */
public interface AfterRunService {
    String getContentAsString() throws UnsupportedEncodingException;

    WebApplicationContext getContext();

    <T> T getBean(Class<T> beanType);

    ModelAndView getModelAndView();

    AfterRunService assertViewName(String viewName);

    AfterRunService assertModel(String name, Object value);
}
