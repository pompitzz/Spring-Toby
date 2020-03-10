package ch4.step1;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void deleteAll() {
        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
        jdbcTemplate.update("delete from users");
    }
}
