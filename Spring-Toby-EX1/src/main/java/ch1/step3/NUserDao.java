package ch1.step3;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class NUserDao extends UserDao {
    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        // NUser 전용 DB Connection
        return null;
    }
}
