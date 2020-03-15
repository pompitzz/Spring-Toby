package ch6.step8;

import ch5.step2.User;

import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public interface UserService {
    void add(User user);

    User get(String id);

    List<User> getAll();

    void deleteAll();

    void update(User user);

    void upgradeLevels();
}