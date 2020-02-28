package ch1.step10;

import ch1.step4.ConnectionMaker;
import ch1.step4.MysqlConnectionMaker;
import ch1.step7.AccountDao;
import ch1.step7.MessageDao;
import ch1.step9.CountingConnectionMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao(){
        final UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        return userDao;
    }


    public AccountDao accountDao(){
        return new AccountDao(connectionMaker());
    }

    public MessageDao messageDao(){
        return new MessageDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(new MysqlConnectionMaker());
    }
}
