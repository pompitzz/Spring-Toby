package ch1.step9;

import ch1.step4.ConnectionMaker;
import ch1.step4.MysqlConnectionMaker;
import ch1.step4.UserDao;
import ch1.step7.AccountDao;
import ch1.step7.MessageDao;
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
        return new UserDao(connectionMaker());
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
