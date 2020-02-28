package ch1.step8;

import ch1.User;
import ch1.UserDaoInterface;
import ch1.step4.ConnectionMaker;
import ch1.step7.DaoFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
public class UserDao implements UserDaoInterface {

    private final ConnectionMaker connectionMaker;

    public UserDao() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(DaoFactory.class);
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        final Connection c = connectionMaker.makeNewConnection();

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
        final Connection c = connectionMaker.makeNewConnection();

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
