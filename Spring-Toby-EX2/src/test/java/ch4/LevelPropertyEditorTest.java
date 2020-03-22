package ch4;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/22
 */
class LevelPropertyEditorTest {
    @Test
    void property() throws Exception{
        LevelPropertyEditor levelEditor = new LevelPropertyEditor();
        levelEditor.setAsText("3");
        assertThat((Level) levelEditor.getValue()).isEqualTo(Level.GOLD);

        levelEditor.setValue(Level.SILVER);
        assertThat(levelEditor.getAsText()).isEqualTo("2");
    }
}