package ch5.step2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user;

    @BeforeEach
    void setUp(){
        user = new User();
    }

    @Test
    void upgradeLevel() throws Exception{
        Level[] levels =  Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    void cannotUpgradeLevel() throws Exception{
        assertThatThrownBy(() -> {
            Level[] levels = Level.values();
            for (Level level : levels) {
                if(level.nextLevel() != null) continue;
                user.setLevel(level);
                user.upgradeLevel();
            }
        })
        .isInstanceOf(IllegalArgumentException.class);

    }

}