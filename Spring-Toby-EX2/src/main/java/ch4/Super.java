package ch4;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@RequestMapping("/user")
@Controller
public class Super {
    @RequestMapping("/complex")
    public String complex(@RequestParam("name") String name,
                          @CookieValue("auth") String auth,
                          ModelMap modelMap) {

        modelMap.put("info", name + "/" + auth);
        return "myview";
    }
}

