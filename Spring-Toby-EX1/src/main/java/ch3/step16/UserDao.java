package ch3.step16;

import ch1.User;
import ch3.step3.StatementStrategy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao {

    public DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setDataSource(dataSource);
        this.dataSource = dataSource;
    }

    public void add(final User user) throws SQLException {

//        final StatementStrategy strategy = (connection) -> {
//            final PreparedStatement ps =
//                    connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//            ps.setString(1, user.getId());
//            ps.setString(2, user.getName());
//            ps.setString(3, user.getPassword());
//            return ps;
//        };

        jdbcTemplate.update("insert into users values(?, ?, ?)"
                , user.getId(), user.getName(), user.getPassword());

    }

    public void deleteAll() throws SQLException {
//        jdbcTemplate.workWithStatementStrategy(
//                (connection) -> connection.prepareStatement("delete from users"));

        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
//        User user = null;
//        try (final Connection c = jdbcTemplate.getConnection()) {
//            try (final PreparedStatement ps = c.prepareStatement("select * from users where id = ?")) {
//                ps.setString(1, id);
//                try (final ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        user = new User();
//                        user.setId(rs.getString("id"));
//                        user.setName(rs.getString("name"));
//                        user.setPassword(rs.getString("password"));
//                    }
//
//
//                    if (user == null) throw new EmptyResultDataAccessException("해당 아이디의 유저는 존재하지 않습니다", 1);
//                }
//            }
//        }

        return jdbcTemplate.queryForObject("select * from users where id = ?"
                , new Object[]{id}
                , ((rs, rowNum) -> {
                    final User user1 = new User();
                    user1.setId(rs.getString("id"));
                    user1.setName(rs.getString("name"));
                    user1.setPassword(rs.getString("password"));
                    return user1;
                }));
    }

    public long getCount() throws SQLException {
//        long count;
//        try (final Connection c = jdbcTemplate.getConnection()) {
//            try (final PreparedStatement ps =
//                         c.prepareStatement("select count(*) from users")) {
//                try (final ResultSet rs = ps.executeQuery()) {
//                    rs.next();
//                    count = rs.getInt(1);
//                }
//            }
//        }
//        return count;

        final Long query = jdbcTemplate.query(
                con -> con.prepareStatement("select count(*) from users"),
                rs -> {
                    rs.next();
                    return rs.getLong(1);
                }
        );
//        return query;

        return jdbcTemplate.queryForObject("select count(*) from users",  Long.class);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id",
                (rs, rowNum) ->
                        new User(rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("password"))
        );
    }

}
