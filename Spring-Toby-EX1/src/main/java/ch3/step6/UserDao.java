package ch3.step6;

import ch1.User;
import ch3.step3.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(final User user) throws SQLException {

        final StatementStrategy strategy = (connection) -> {
            final PreparedStatement ps =
                    connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            return ps;
        };

        jdbcContextWithStateStrategy(strategy);
    }

    public void deleteAll() throws SQLException {
        jdbcContextWithStateStrategy(
                (connection) -> connection.prepareStatement("delete from users"));
    }

    private void jdbcContextWithStateStrategy(final StatementStrategy strategy) throws SQLException {
        try (final Connection c = dataSource.getConnection()) {
            try (final PreparedStatement ps = strategy.makePreparedStatement(c)) {
                ps.executeUpdate();
            }
        }
    }

}
