package ch3;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/20
 */
public class AbstractDispatcherServletTest implements AfterRunService {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockServletConfig config = new MockServletConfig("Spring");
    protected MockHttpSession session;

    private ConfigurableDispatcherServlet dispatcherServlet;

    // 설정 메타정보는 클래스, 클래스패스, 상대 클래스패스로 지정할 수있다
    private Class<?>[] classes;
    private String[] locations;
    private String[] relativeLocations;

    private String servletPath;

    public AbstractDispatcherServletTest setLocations(String... location){
        this.locations = location;
        return this;
    }

    public AbstractDispatcherServletTest setRelativeLocations(String... relativeLocations){
        this.relativeLocations = relativeLocations;
        return this;
    }

    public AbstractDispatcherServletTest setClasses(Class<?>... classes){
        this.classes = classes;
        return this;
    }

    public AbstractDispatcherServletTest setServletPath(String servletPath){
        if (this.request == null){
            this.servletPath = servletPath;
        }
        else{
            this.request.setServletPath(servletPath);
        }
        return this;
    }

    public AbstractDispatcherServletTest initRequest(String requestUri, String method){
        this.request = new MockHttpServletRequest(method, requestUri);
        this.response = new MockHttpServletResponse();
        if (this.servletPath != null) this.setServletPath(this.servletPath);
        return this;
    }

    public AbstractDispatcherServletTest initRequest(String requestUri, RequestMethod method){
        return this.initRequest(requestUri, method.toString());
    }

    public AbstractDispatcherServletTest initRequest(String requestUri){
        return this.initRequest(requestUri, RequestMethod.GET);
    }

    public AbstractDispatcherServletTest addParameter(String name, String value){
        if (this.request == null){
            throw new IllegalStateException("request가 초기화 되지 않았습니다");
        }
        this.request.addParameter(name, value);
        return this;
    }

    public AbstractDispatcherServletTest buildDispatcherServlet() throws ServletException {
        if (this.classes == null && this.locations == null && this.relativeLocations == null){
            throw new IllegalStateException("classes와 lcoations 중 하나는 설정되어야 합니다");
        }
        this.dispatcherServlet = new ConfigurableDispatcherServlet();
        this.dispatcherServlet.setClasses(this.classes);
        this.dispatcherServlet.setLocations(this.locations);
        if(this.relativeLocations != null){
            this.dispatcherServlet.setRelativeLocations(getClass(), this.relativeLocations);
        }
        this.dispatcherServlet.init(this.config);
        return this;
    }

    public AbstractDispatcherServletTest runService() throws ServletException, IOException{
        if (this.dispatcherServlet == null) buildDispatcherServlet();
        if (this.request == null){
            throw new IllegalStateException("request가 준비되지 않았습니다.");
        }
        this.dispatcherServlet.service(this.request, this.response);
        return this;
    }

    public AbstractDispatcherServletTest runService(String requestUri) throws ServletException, IOException{
        initRequest(requestUri);
        runService();
        return this;
    }

    @Override
    public String getContentAsString() throws UnsupportedEncodingException {
        return this.response.getContentAsString();
    }

    @Override
    public WebApplicationContext getContext() {
        if (this.dispatcherServlet == null)
            throw new IllegalStateException("DispatcherServlet이 준비되지 않았습니다.");
        return this.dispatcherServlet.getWebApplicationContext();
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        if (this.dispatcherServlet == null)
            throw new IllegalStateException("DispatcherServlet이 준비되지 않았습니다.");
        return getContext().getBean(beanType);
    }

    @Override
    public ModelAndView getModelAndView() {
        return this.dispatcherServlet.getModelAndView();
    }

    @Override
    public AfterRunService assertViewName(String viewName) {
        assertThat(this.getModelAndView().getViewName()).isEqualTo(viewName);
        return this;
    }

    @Override
    public AfterRunService assertModel(String name, Object value) {
        assertThat(this.getModelAndView().getModel().get(name)).isEqualTo(value);
        return this;
    }
}
