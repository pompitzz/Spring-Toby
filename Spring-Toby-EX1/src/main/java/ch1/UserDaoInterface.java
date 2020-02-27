package ch1;

import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
// test에서 서로다른 Dao를 하나의 메서드로 사용하기 위해 적용
public interface UserDaoInterface {

    public void add(User user) throws ClassNotFoundException, SQLException;

    public User get(String id) throws ClassNotFoundException, SQLException;
}
