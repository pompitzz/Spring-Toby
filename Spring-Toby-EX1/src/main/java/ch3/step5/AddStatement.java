package ch3.step5;

import ch1.User;
import ch3.step3.StatementStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/29
 */
public class AddStatement implements StatementStrategy {
    private User user;

    public AddStatement(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement makePreparedStatement(final Connection c) throws SQLException {
        final PreparedStatement ps =
                c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        return ps;
    }
}
