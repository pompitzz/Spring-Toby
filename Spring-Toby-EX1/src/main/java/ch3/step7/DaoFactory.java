package ch3.step7;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao(){
        final UserDao userDao = new UserDao();
        userDao.setJdbcContext(jdbcContext());
        return userDao;
    }
    @Bean
    public DataSource dataSource(){
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:3306/springtoby");
        dataSource.setUsername("sunlee");
        dataSource.setPassword("pass");

        return dataSource;
    }

    @Bean
    public JdbcContext jdbcContext(){
        final JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());
        return jdbcContext;
    }

}
