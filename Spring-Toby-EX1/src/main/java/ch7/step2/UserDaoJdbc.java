package ch7.step2;

import ch5.step2.Level;
import ch5.step2.User;
import ch5.step2.UserDao;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
@Setter
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private Map<String, String> sqlMap;

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        jdbcTemplate.update(sqlMap.get("add"),
                user.getId(), user.getName(),
                user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public void deleteAll() {
        jdbcTemplate.update(sqlMap.get("delete"));
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject(sqlMap.get("get")
                , new Object[]{id}, this::makeUser);
    }

    public long getCount() {
        return jdbcTemplate.queryForObject(sqlMap.get("count"), Long.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                sqlMap.get("update"),
                user.getName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(),
                user.getRecommend(), user.getEmail(), user.getId()
        );
    }

    public List<User> getAll() {
        return jdbcTemplate.query(sqlMap.get("getAll"), this::makeUser);
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
