package ch3.step9;

import ch1.User;
import ch3.step3.StatementStrategy;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    private JdbcContext jdbcContext;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void deleteAll() throws SQLException {
        executeSql("delete from users");
        jdbcContext.workWithStatementStrategy(this::executeDeleteSql);
    }

    private void executeSql(final String sql) throws SQLException {
        jdbcContext.workWithStatementStrategy(connection -> connection.prepareStatement(sql));
    }

    private PreparedStatement executeDeleteSql(final Connection connection) throws SQLException {
        return connection.prepareStatement("delete from users");
    }

}
