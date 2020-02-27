package ch1.step1;

import ch1.User;
import ch1.UserDaoInterface;

import java.sql.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao implements UserDaoInterface {
    public void add(User user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        final Connection c = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/springtoby", "sunlee", "pass");

        final PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) values(?,?,?)"
        );

        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        final Connection c = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/springtoby", "sunlee", "pass");

        final PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        final ResultSet rs = ps.executeQuery();
        rs.next();
        final User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }
}
