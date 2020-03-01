package ch3.step17;

import ch1.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) throws SQLException {
        jdbcTemplate.update("insert into users values(?, ?, ?)"
                , user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() throws SQLException {
        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        return jdbcTemplate.queryForObject("select * from users where id = ?"
                , new Object[]{id}, this::makeUser);
    }

    public long getCount() throws SQLException {
        return jdbcTemplate.queryForObject("select count(*) from users",  Long.class);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("password"));
    }
}
