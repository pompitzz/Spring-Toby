package ch3.step3;

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
        try (final Connection c = dataSource.getConnection()) {
            StatementStrategy strategy = new DeleteAllStatement();
            try (final PreparedStatement ps = strategy.makePreparedStatement(c)) {
                ps.executeUpdate();
            }
        }
    }

}
