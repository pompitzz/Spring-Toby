package ch1.step6;

import ch1.step4.ConnectionMaker;
import ch1.step4.MysqlConnectionMaker;
import ch1.step4.UserDao;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class DaoFactory {
    public UserDao userDao(){
//        return new UserDao(new MysqlConnectionMaker());
        return new UserDao(getConnectionMaker());
    }


    public AccountDao accountDao(){
//        return new AccountDao(new MysqlConnectionMaker());
        return new AccountDao(getConnectionMaker());
    }

    public MessageDao messageDao(){
//        return new MessageDao(new MysqlConnectionMaker());
        return new MessageDao(getConnectionMaker());
    }

    private ConnectionMaker getConnectionMaker() {
        return new MysqlConnectionMaker();
    }
}
