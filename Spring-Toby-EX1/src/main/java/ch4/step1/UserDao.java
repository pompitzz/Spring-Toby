package ch4.step1;

<<<<<<< HEAD
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
=======
import ch1.User;
import com.mysql.cj.exceptions.MysqlErrorNumbers;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
>>>>>>> 363609ada5e21b8286070b6745b03a254153e003

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

<<<<<<< HEAD
=======
    public void add(final User user) throws DuplicateUserIdException {
        try {
            jdbcTemplate.update("insert into users values(?, ?, ?)"
                    , user.getId(), user.getName(), user.getPassword());
            throw new SQLException();
        } catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
                throw new DuplicateUserIdException(e);
            else
                throw new RuntimeException(e);
        }
    }

>>>>>>> 363609ada5e21b8286070b6745b03a254153e003
    public void deleteAll() {
        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
        jdbcTemplate.update("delete from users");
    }
}
