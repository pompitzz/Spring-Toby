package ch4;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@Controller
public class UserController {
    @Inject
    Provider<CodePropertyEditor> codeEditorProvider;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Code.class, codeEditorProvider.get());
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
