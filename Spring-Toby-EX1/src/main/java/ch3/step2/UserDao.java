package ch3.step2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public abstract class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execQuery() throws SQLException {
        try (final Connection c = dataSource.getConnection()) {
            try (final PreparedStatement ps = makeStatement(c)) {
                ps.executeUpdate();
            }
        }
    }

    abstract protected PreparedStatement makeStatement(final Connection c) throws SQLException;
}
