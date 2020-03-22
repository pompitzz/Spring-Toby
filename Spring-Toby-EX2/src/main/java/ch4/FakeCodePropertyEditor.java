package ch4;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.PropertyEditorSupport;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
public class FakeCodePropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Code code = new Code();
        code.setId(Integer.parseInt(text));
        setValue(code);
    }

    @Override
    public String getAsText() {
        return String.valueOf(((Code)getValue()).getId());
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
class Code{
    int id;
}
