package ch5.step2;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        jdbcTemplate.update("insert into users(id, name, password, Level, Login, Recommend, Email) values(?, ?, ?, ?, ?, ?, ?)",
                user.getId(), user.getName(),
                user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?"
                , new Object[]{id}, this::makeUser);
    }

    public long getCount() {
        return jdbcTemplate.queryForObject("select count(*) from users", Long.class);
    }

@Override
public void update(User user) {
    this.jdbcTemplate.update(
            "update users set name = ?, password = ?, level = ?, Login = ?, Recommend = ?, Email = ? where id = ?",
            user.getName(), user.getPassword(),
            user.getLevel().intValue(), user.getLogin(),
            user.getRecommend(), user.getEmail(), user.getId()
    );
}

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("password"),
                Level.valueOf(rs.getInt("level")),
                rs.getInt("login"),
                rs.getInt("recommend"),
                rs.getString("email")
        );
    }
}
