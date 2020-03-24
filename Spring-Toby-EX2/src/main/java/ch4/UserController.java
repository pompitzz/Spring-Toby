package ch4;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.validation.Valid;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserValidator userValidator;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(this.userValidator);
    }

    @RequestMapping("/add")
    public void add(@ModelAttribute @Valid User user, BindingResult bindingResult) {
        requestMappingHandlerMapping
                .getHandlerMethods()
                .forEach((key, value) -> System.out.println(key + " ====> " + value));
    }

}

@AllArgsConstructor
@Setter
@Getter
class User {
    int id;
    String name;
    String email;
}

class UserService {
}
