package ch3.step4;

import ch3.step3.DeleteAllStatement;
import ch3.step3.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao{

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteAll() throws SQLException {
        final StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStateStrategy(strategy);
    }

    public void jdbcContextWithStateStrategy(final StatementStrategy strategy) throws SQLException{
        try(final Connection c = dataSource.getConnection()){
            try(final PreparedStatement ps = strategy.makePreparedStatement(c)){
                ps.executeUpdate();
            }
        }
    }
}
