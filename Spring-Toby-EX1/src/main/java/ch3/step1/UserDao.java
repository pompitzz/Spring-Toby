package ch3.step1;

import ch1.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void add(User user) throws ClassNotFoundException, SQLException {
        try (final Connection c = dataSource.getConnection()) {

            try (final PreparedStatement ps =
                         c.prepareStatement("insert into users(id, name, password) values(?,?,?)")) {

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                ps.executeUpdate();
            }
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        User user = null;
        try(final Connection c = dataSource.getConnection()){
            try(final PreparedStatement ps = c.prepareStatement("select * from users where id = ?")){
                ps.setString(1, id);
                try(final ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                    }


                    if (user == null) throw new EmptyResultDataAccessException("해당 아이디의 유저는 존재하지 않습니다", 1);
                }
            }
        }
        return user;
    }

    public void deleteAll() throws SQLException {
        try (final Connection c = dataSource.getConnection()) {
            try (final PreparedStatement ps = c.prepareStatement("truncate users")) {
                ps.executeUpdate();
            }
        }
    }

    public long getCount() throws SQLException {
        long count;
        try (final Connection c = dataSource.getConnection()) {
            try (final PreparedStatement ps =
                         c.prepareStatement("select count(*) from users")) {
                try (final ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }
}
