package ch3.step2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class UserDaoDeleteAll extends UserDao {

    @Override
    protected PreparedStatement makeStatement(final Connection c) throws SQLException {
        return c.prepareStatement("delete from users");
    }
}
