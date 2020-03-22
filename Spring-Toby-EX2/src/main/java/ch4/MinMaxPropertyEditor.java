package ch4;

import lombok.AllArgsConstructor;

import java.beans.PropertyEditorSupport;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
@AllArgsConstructor
public class MinMaxPropertyEditor extends PropertyEditorSupport {
    int min;
    int max;

    @Override
    public String getAsText() {
        return String.valueOf((Integer) this.getValue());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        int val = Integer.parseInt(text);
        if (val < min) val = min;
        else if(val > max) val = max;
        setValue(val);
    }
}
