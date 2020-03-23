package ch4;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/23
 */
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return (User.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

//        if (user.getName() == null || user.getName().length() == 0)
//            errors.rejectValue("name", "filed.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required");

    }
}
