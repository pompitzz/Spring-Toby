package ch4;


import java.beans.PropertyEditorSupport;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
public class LevelPropertyEditor extends PropertyEditorSupport {
    public String getAsText(){
        return String.valueOf(((Level)this.getValue()).intValue());
    }

    public void setAsText(String text){
        this.setValue(Level.valueOf(Integer.parseInt(text.trim())));
    }
}
