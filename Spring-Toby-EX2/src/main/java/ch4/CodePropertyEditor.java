package ch4;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.beans.PropertyEditorSupport;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CodePropertyEditor extends PropertyEditorSupport {

    private final CodeService codeService;

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(this.codeService.getCode(Integer.parseInt(text)));
    }

    @Override
    public String getAsText() {
        return String.valueOf(((Code) getValue()).getId());
    }
}

@Service
class CodeService{

    public Code getCode(int parseInt) {
        return null;
    }
}