package ch3.step3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public interface StatementStrategy {
    PreparedStatement makePreparedStatement(final Connection c) throws SQLException;
}
