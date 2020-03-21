package ch3;

import lombok.Setter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/21
 */
@Setter
public abstract class SimpleControllerV1 implements Controller {
    private String[] requiredParams;
    private String viewName;


    @Override
    final public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(viewName == null) throw new IllegalStateException();
        Map<String, String> params = new HashMap<>();
        for (String param : requiredParams) {
            String value = request.getParameter(param);
            if (value == null) throw new IllegalStateException();
            params.put(param, value);
        }

        Map<String, Object> model = new HashMap<>();

        this.control(params, model);

        return new ModelAndView(this.viewName, model);
    }

    public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
}


class HelloControllerV1 extends SimpleControllerV1{

    public HelloControllerV1() {
        this.setRequiredParams(new String[]{"name"});
        this.setViewName("/WEB-INF/view/hellp.jsp");
    }

    @Override
    public void control(Map<String, String> params, Map<String, Object> model) throws Exception {
        model.put("message", "Hello " + params.get("name"));
    }
}
