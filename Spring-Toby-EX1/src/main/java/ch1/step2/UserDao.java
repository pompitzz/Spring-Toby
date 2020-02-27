package ch1.step2;

import ch1.User;

import java.sql.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {
    public void add(User user) throws ClassNotFoundException, SQLException {
        final Connection c = getConnection();

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
        final Connection c = getConnection();

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

    // 리팩토링 중 메서드 추출 기법을 통해 유연한 코드가 되었다.
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(
                        "jdbc:mysql://localhost:3306/springtoby", "sunlee", "pass"
                );
    }
}
