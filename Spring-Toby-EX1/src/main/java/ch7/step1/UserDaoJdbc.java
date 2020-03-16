package ch7.step1;

import ch5.step2.Level;
import ch5.step2.User;
import ch5.step2.UserDao;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
@Setter
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private String sqlAdd;
    private String sqlGet;
    private String sqlGetAll;
    private String sqlUpdate;
    private String sqlDelete;
    private String sqlGetCount;

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        jdbcTemplate.update(sqlAdd,
                user.getId(), user.getName(),
                user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public void deleteAll() {
        jdbcTemplate.update(sqlDelete);
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject(sqlGet
                , new Object[]{id}, this::makeUser);
    }

    public long getCount() {
        return jdbcTemplate.queryForObject(sqlGetCount, Long.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                sqlUpdate,
                user.getName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(),
                user.getRecommend(), user.getEmail(), user.getId()
        );
    }

    public List<User> getAll() {
        return jdbcTemplate.query(sqlGetAll, this::makeUser);
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
