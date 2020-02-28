package ch3.step8;

import ch3.step3.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(final StatementStrategy strategy) throws SQLException{
        try(final Connection c = dataSource.getConnection()){
            try(final PreparedStatement ps = strategy.makePreparedStatement(c)){
                ps.executeUpdate();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
