package ch5.step2;

import lombok.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String id;
    private String name;
    private String password;

    private Level level;
    int login;
    int recommend;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}