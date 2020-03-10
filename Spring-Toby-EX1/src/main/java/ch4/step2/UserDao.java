package ch4.step2;

import ch1.User;

import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
public interface UserDao {
    void add(User user);

    User get(String id);

    List<User> getAll();

    void deleteAll();

    int getCount();

// setDataSource() 메서드는 구현에 따라 달라 질 수 있고 userDao를 사용하는 클라이언트가 알 필요 없는 정보이므로 인터페이스에 정의하지 않는다.
}
